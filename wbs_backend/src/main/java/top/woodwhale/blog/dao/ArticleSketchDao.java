package top.woodwhale.blog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.woodwhale.blog.pojo.ArticleSketch;

public interface ArticleSketchDao extends JpaRepository<ArticleSketch, String>, JpaSpecificationExecutor<ArticleSketch> {

    ArticleSketch findOneById(String articleId);

    int deleteOneById(String articleId);
}
