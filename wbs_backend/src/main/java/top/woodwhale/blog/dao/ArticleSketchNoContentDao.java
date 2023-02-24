package top.woodwhale.blog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.woodwhale.blog.pojo.ArticleSketchNoContent;

public interface ArticleSketchNoContentDao extends JpaRepository<ArticleSketchNoContent, String>, JpaSpecificationExecutor<ArticleSketchNoContent> {

    ArticleSketchNoContent findOneById(String articleId);

    int deleteOneById(String articleId);
}
