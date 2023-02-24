package top.woodwhale.blog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import top.woodwhale.blog.pojo.Article;

/**
 * 文章dao层
 */
public interface ArticleDao extends JpaRepository<Article, String>, JpaSpecificationExecutor<Article> {

    Article findOneById(String articleId);

    @Modifying
    int deleteOneById(String articleId);

    @Modifying
    @Query(nativeQuery = true, value = "update `tb_article` set `state` = '0' where `id` = ?")
    int deleteOneStateById(String articleId);

    @Modifying
    @Query(nativeQuery = true, value = "update `tb_article` set `state` = '3' where `id` = ?")
    int topOneById(String articleId);

    @Modifying
    @Query(nativeQuery = true, value = "update `tb_article` set `state` = '1' where `id` = ?")
    int publishOneById(String articleId);
}
