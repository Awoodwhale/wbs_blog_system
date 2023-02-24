package top.woodwhale.blog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import top.woodwhale.blog.pojo.Category;

/**
 * 文章分类dao层
 */
public interface CategoryDao extends JpaRepository<Category, String>, JpaSpecificationExecutor<Category> {

    /**
     * 通过name寻找分类
     * @param name 分类名称
     * @return Category
     */
    Category findOneByName(String name);

    /**
     * 通过id寻找分类
     * @param categoryId 分类id
     * @return Category
     */
    Category findOneById(String categoryId);

    /**
     * 通过id删除分类，其实就是将改分类的状态设置为“被删除”状态，并非从MySQL中彻底delete
     * @param categoryId 分类id
     * @return int
     */
    @Modifying
    @Query(nativeQuery = true, value = "update `tb_categories` set `state` = '0' where `id` = ?")
    int deleteCategoryById(String categoryId);
}
