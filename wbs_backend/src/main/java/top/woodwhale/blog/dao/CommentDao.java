package top.woodwhale.blog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import top.woodwhale.blog.pojo.Comment;

import java.util.List;

/**
 * 评论dao
 */
public interface CommentDao extends JpaRepository<Comment, String>, JpaSpecificationExecutor<Comment> {
    @Modifying
    int deleteAllByArticleId(String articleId);

    @Query(nativeQuery = true, value = "select * from `tb_comment` " +
            "where `article_id` = ? " +
            " order by `state` desc, `create_time` desc ")
    List<Comment> listCommentsByArticleId(String articleId);

    Comment findOneById(String commentId);

    @Modifying
    int deleteOneById(String commentId);

    @Modifying
    @Query(nativeQuery = true, value = "update `tb_comment` set `parent_id` = null where `parent_id` = ?")
    int updateCommentParent(String commentId);

    @Modifying
    @Query(nativeQuery = true, value = "update `tb_comment` set `state` = ? where `id` = ?")
    int updateCommentState(String state, String commentId);
}
