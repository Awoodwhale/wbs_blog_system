package top.woodwhale.blog.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.woodwhale.blog.interceptor.CheckTooFrequentCommit;
import top.woodwhale.blog.pojo.Category;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.ICategoryService;

/**
 * 分类API
 */
@RestController
@RequestMapping("/admin/category")
@PreAuthorize("@Permission.admin()")
public class CategoryAdminApi {

    /**
     * 注入ICategoryService接口实现类
     */
    @Autowired
    private ICategoryService categoryService;

    /**
     * 添加分类
     * @param category 分类
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PostMapping
    public ResponseResult addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    /**
     * 获取分类
     * @param categoryId 分类ID
     * @return ResponseResult
     */
    @GetMapping("/{categoryId}")
    public ResponseResult getCategory(@PathVariable("categoryId") String categoryId) {
        return categoryService.getCategory(categoryId);
    }

    /**
     * 根据分类ID删除分类
     * @param categoryId 分类ID
     * @return ResponseResult
     */
    @DeleteMapping("/{categoryId}")
    public ResponseResult deleteCategory(@PathVariable("categoryId") String categoryId) {
        return categoryService.deleteCategory(categoryId);
    }

    /**
     * 根据分类ID更新分类
     * @param categoryId 分类ID
     * @param category category的Bean类
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PutMapping("/{categoryId}")
    public ResponseResult updateCategory(@PathVariable("categoryId") String categoryId, @RequestBody Category category) {
        return categoryService.updateCategory(categoryId,category);
    }

    /**
     * 获取分类集合
     * @return ResponseResult
     */
    @GetMapping("/list")
    public ResponseResult listCategories() {
        return categoryService.listCategories();
    }

}
