package top.woodwhale.blog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import top.woodwhale.blog.pojo.ArticleNoContent;

import java.util.List;

/**
 * 文章（无内容）dao层
 */
public interface ArticleNoContentDao extends JpaRepository<ArticleNoContent, String>, JpaSpecificationExecutor<ArticleNoContent> {

    @Query(nativeQuery = true, value = "select `labels` from `tb_article` where `id` = ?")
    String listArticleLabelsById(String articleId);

    @Query(nativeQuery = true, value = "select * from `tb_article` where `id` != ? " +
            "and (`state` = '1' or `state` = '2') order by `create_time` desc limit ?")
    List<ArticleNoContent> listArticleLatestBySize(String articleId, int remainSize);

    @Query(nativeQuery = true, value = "select * from `tb_article` where `labels` like ? " +
            "and (`state` = '1' or `state` = '2') and `id` != ? limit ?")
    List<ArticleNoContent> listArticleLabelsByLike(String tag, String articleId, int size);

    ArticleNoContent findOneById(String articleId);
}
