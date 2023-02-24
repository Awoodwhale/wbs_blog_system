package top.woodwhale.blog.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.IArticleService;
import top.woodwhale.blog.services.ICategoryService;
import top.woodwhale.blog.services.ILabelService;

import static top.woodwhale.blog.utils.Constants.Article.ARTICLE_NOT_REMOVE_STATE;

/**
 * 文章门户 API
 */
@RestController
@RequestMapping("/portal/article")
public class ArticlePortalApi {

    /**
     * 分类service层
     */
    @Autowired
    private ICategoryService categoryService;

    /**
     * 标签service层
     */
    @Autowired
    private ILabelService labelService;

    /**
     * 注入articleService
     */
    @Autowired
    private IArticleService articleService;

    /**
     * 获取所有分类
     * @return ResponseResult
     */
    @GetMapping("/categories")
    public ResponseResult getCategories() {
        return categoryService.listCategories();
    }

    /**
     * 获取文章列表
     * 权限： 所有用户
     * 状态： 已经发布的文章(置顶文章由另一个api——listTopArticles获取，“删除”、”草稿“不可获取)
     * @param page 页数
     * @param size 每页容量
     * @return ResponseResult
     */
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listArticle(@PathVariable("page")int page, @PathVariable("size") int size) {
        return articleService.listArticlesFromPortal(page,size);
    }

    /**
     * 通过分类获取文章列表
     * @param categoryId 分类ID
     * @param page 页数
     * @param size 每页容量
     * @return ResponseResult
     */
    @GetMapping("/list/category/{categoryId}/{page}/{size}")
    public ResponseResult listArticleByCategoryId(@PathVariable("categoryId") String categoryId,
            @PathVariable("page")int page,
            @PathVariable("size") int size) {
        return articleService.listArticles(page,size,null,categoryId,ARTICLE_NOT_REMOVE_STATE);
    }

    /**
     * 通过标签获取文章列表
     * @param label 标签
     * @param page 页数
     * @param size 每页容量
     * @return ResponseResult
     */
    @GetMapping("/list/label/{label}/{page}/{size}")
    public ResponseResult listLabelArticles(@PathVariable("label") String label,
            @PathVariable("page")int page,
            @PathVariable("size") int size) {
        return articleService.listLabelArticles(page,size,label);
    }

    /**
     * 获取置顶文章列表
     * @return ResponseResult
     */
    @GetMapping("/list/top")
    public ResponseResult listTopArticles() {
        return articleService.listTopArticles();
    }

    /**
     * 根据文章ID获取文章细节
     * 权限 ： 任何用户
     * @param articleId 文章ID
     * @return ResponseResult
     */
    @GetMapping("/{articleId}")
    public ResponseResult getArticleDetail(@PathVariable("articleId") String articleId) {
        boolean isFromAdmin = false;
        return articleService.getArticle(articleId, isFromAdmin);
    }

    /**
     * 获取推荐文章
     *
     * 通过标签来计算匹配度
     * 一篇文章的标签由一个或者多个（5个以内）
     * 从文章的标签中随机抽取一个
     * 每次获取的推荐文章其实就是这个标签
     * 通过标签去查询类似的文章，如果没有相关的文章，就获取最新发布的文章
     *
     * @param articleId 文章ID
     * @param size 获取推荐文章数量
     * @return ResponseResult
     */
    @GetMapping("/list/recommend/{articleId}/{size}")
    public ResponseResult listRecommendArticles(@PathVariable("articleId") String articleId,
            @PathVariable("size") int size) {
        return articleService.listRecommendArticles(articleId,size);
    }

    /**
     * 获取所有标签
     * @return ResponseResult
     */
    @GetMapping("/labels")
    public ResponseResult getLabels() {
        return labelService.listLabels();
    }

    /**
     * 获取标签云
     * @param size 需要获取的个数
     * @return ResponseResult
     */
    @GetMapping("/label/{size}")
    public ResponseResult getLabels(@PathVariable("size") int size) {
        return articleService.listLabels(size);
    }
}
