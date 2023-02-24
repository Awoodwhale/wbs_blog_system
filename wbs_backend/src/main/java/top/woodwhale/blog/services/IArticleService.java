package top.woodwhale.blog.services;

import top.woodwhale.blog.pojo.Article;
import top.woodwhale.blog.pojo.ArticleSketch;
import top.woodwhale.blog.response.ResponseResult;

public interface IArticleService {
    ResponseResult addArticle(Article article);

    ResponseResult listArticles(int page, int size, String keyword, String categoryId, String state);

    ResponseResult getArticle(String articleId, boolean isFromAdmin);

    ResponseResult updateArticle(String articleId, Article article);

    ResponseResult deleteArticle(String articleId);

    ResponseResult updateArticleState(String articleId, String state);

    ResponseResult addSketchArticle(ArticleSketch article);

    ResponseResult updateSketchArticle(String articleId, ArticleSketch article);

    ResponseResult publishSketchArticle(String articleId);

    ResponseResult listTopArticles();

    ResponseResult listRecommendArticles(String articleId, int size);

    ResponseResult listLabelArticles(int page, int size, String label);

    ResponseResult listLabels(int size);

    ResponseResult deleteSketchArticle(String articleId);

    ResponseResult listSketchArticles(int page, int size);

    ResponseResult getSketchArticle(String articleId);

    ResponseResult getArticleTitle(String articleId);

    ResponseResult listArticlesFromPortal(int page, int size);
}
