package top.woodwhale.blog.services;

import top.woodwhale.blog.pojo.Article;
import top.woodwhale.blog.response.ResponseResult;

public interface ISolrService {
    ResponseResult doSearch(String keyword, int page, int size, String categoryId, Integer sort);

    void addArticle(Article article);

    void deleteArticle(String id);

    void updateArticle(String articleId, Article article);
}
