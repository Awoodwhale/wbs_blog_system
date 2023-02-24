package top.woodwhale.blog.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.woodwhale.blog.dao.CategoryDao;
import top.woodwhale.blog.pojo.Category;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.ICategoryService;
import top.woodwhale.blog.services.IUserService;
import top.woodwhale.blog.utils.SnowflakeIdUtils;
import top.woodwhale.blog.utils.TextUtils;

import java.util.Date;
import java.util.List;

import static top.woodwhale.blog.utils.Constants.Category.CATEGORY_NORMAL_STATE;
import static top.woodwhale.blog.utils.Constants.Category.CATEGORY_REMOVE_STATE;

@Slf4j
@Service
@Transactional
public class CategoryServiceImpl implements ICategoryService {

    /**
     * 注入的id生成器
     */
    @Autowired
    private SnowflakeIdUtils idUtils;

    /**
     * CategoryDao的dao层
     */
    @Autowired
    private CategoryDao categoryDao;

    /**
     * 用户service层
     */
    @Autowired
    private IUserService userService;

    /**
     * 添加category
     * 有两种情况：
     * 1. 添加一个全新、之前从未添加过的category
     * 2. 添加一个之前被“删除”的category
     * @param category 文章分类
     * @return ResponseResult
     */
    @Override
    public ResponseResult addCategory(Category category) {
        // 1. 检查数据
        if (TextUtils.isEmpty(category.getName())) {
            return ResponseResult.FAILED(TextUtils.notNullable("分类名称"));
        }
        if (TextUtils.isEmpty(category.getPinyin())) {
            return ResponseResult.FAILED(TextUtils.notNullable("分类拼音"));
        }
        if (TextUtils.isEmpty(category.getDescription())) {
            return ResponseResult.FAILED(TextUtils.notNullable("分类描述"));
        }
        // 2. 去MySQL中查询是否存在该分类
        boolean flag = false;
        Category categoryInMysql = categoryDao.findOneByName(category.getName());
        if (categoryInMysql != null) {
            // 如果分类状态是没有删除的，那么该分类存在
            if (categoryInMysql.getState().equals(CATEGORY_NORMAL_STATE)) {
                return ResponseResult.FAILED(TextUtils.hasExisted("分类"));
            }
            // 如果添加的category的name是原来被删除的，那么不需要创建新的数据，而是将原来的数据更改就可以了,这里设置一个标志位
            flag = true;
        }
        // 3. 补充数据
        category.setId(flag ? categoryInMysql.getId(): String.valueOf(idUtils.nextId()) );
        category.setState(CATEGORY_NORMAL_STATE);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        category.setCount(0);

        // 4. 保存数据
        categoryDao.save(category);
        return ResponseResult.SUCCESS(TextUtils.successAdd("分类"));
    }

    /**
     * 获取category
     * @param categoryId 分类ID
     * @return ResponseResult
     */
    @Override
    public ResponseResult getCategory(String categoryId) {
        Category categoryInMysql = categoryDao.findOneById(categoryId);
        if (categoryInMysql == null || categoryInMysql.getState().equals(CATEGORY_REMOVE_STATE)) {
            return ResponseResult.FAILED(TextUtils.notExist("分类"));
        }
        return ResponseResult.SUCCESS(TextUtils.successGet("分类")).setData(categoryInMysql);
    }

    /**
     * 获取categories列表
     * @return ResponseResult
     */
    @Override
    public ResponseResult listCategories() {
        // 根据创建时间和order进行降序排序 ，同时选择没有被删除的categories
        List<Category> categoriesList = categoryDao.findAll((Specification<Category>)
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("state").as(String.class), CATEGORY_NORMAL_STATE),
                Sort.by(Sort.Direction.DESC, "createTime", "order"));

        return ResponseResult.SUCCESS(TextUtils.successGet("分类列表")).setData(categoriesList);
    }

    /**
     * 通过categoryId更新category信息
     * @param categoryId 分类id
     * @param category 分类bean类
     * @return ResponseResult
     */
    @Override
    public ResponseResult updateCategory(String categoryId, Category category) {
        if (TextUtils.isEmpty(categoryId)) {
            return ResponseResult.FAILED(TextUtils.notNullable("分类ID"));
        }
        // 1. 先从MySQL中通过id查询需要修改的category
        Category categoryInMysql = categoryDao.findOneById(categoryId);
        if (categoryInMysql == null || categoryInMysql.getState().equals(CATEGORY_REMOVE_STATE)) {
            return ResponseResult.FAILED(TextUtils.notExist("分类"));
        }
        // 2. 复制一份数据库中的category，否则更改会造成数据库内容修改
        Category copiedCategory = getCopiedCategory(categoryInMysql);
        if (!TextUtils.isEmpty(category.getName())) {
            copiedCategory.setName(category.getName());
        }else {
            return ResponseResult.FAILED(TextUtils.notNullable("分类名称"));
        }
        if (!TextUtils.isEmpty(category.getPinyin())) {
            copiedCategory.setPinyin(category.getPinyin());
        } else {
            return ResponseResult.FAILED(TextUtils.notNullable("分类拼音"));
        }
        if (!TextUtils.isEmpty(category.getDescription())) {
            copiedCategory.setDescription(category.getDescription());
        } else {
            return ResponseResult.FAILED(TextUtils.notNullable("分类描述"));
        }
        // 3. 保存数据到MySQL中
        copiedCategory.setOrder(category.getOrder());
        copiedCategory.setUpdateTime(new Date());
        categoryInMysql = copiedCategory;
        categoryDao.save(categoryInMysql);
        return ResponseResult.SUCCESS(TextUtils.successUpdate("分类"));
    }

    /**
     * 通过id删除分类，并非真的删除，而是让数据库中的该分类状态设置成删除
     * @param categoryId 分类id
     * @return ResponseResult
     */
    @Override
    public ResponseResult deleteCategory(String categoryId) {
        if (categoryId.contains("_") && categoryId.split("_").length > 0) {
            String[] ids = categoryId.split("_");
            for (String id : ids) {
                int res = categoryDao.deleteCategoryById(id);
                if (res <= 0) {
                    return ResponseResult.FAILED(TextUtils.failDelete("分类id " + id + " "));
                }
            }
            return ResponseResult.SUCCESS(TextUtils.successDelete("分类"));
        } else {
            int res = categoryDao.deleteCategoryById(categoryId);
            if (res > 0) {
                return ResponseResult.SUCCESS(TextUtils.successDelete("分类"));
            }
        }
        return ResponseResult.FAILED(TextUtils.failDelete("分类id " + categoryId + " "));
    }

    /**
     * 复制一份category对象
     * @param categoryInMysql 数据库中的category对象
     * @return 复制出来的Category对象
     */
    @NotNull
    private Category getCopiedCategory(Category categoryInMysql) {
        Category newCategory = new Category();
        BeanUtils.copyProperties(categoryInMysql, newCategory);
        return newCategory;
    }
}
