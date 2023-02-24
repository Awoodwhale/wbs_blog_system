package top.woodwhale.blog.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.woodwhale.blog.interceptor.CheckTooFrequentCommit;
import top.woodwhale.blog.pojo.Article;
import top.woodwhale.blog.pojo.ArticleSketch;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.IArticleService;

import static top.woodwhale.blog.utils.Constants.Article.*;

/**
 * 文章API
 */
@RestController
@RequestMapping("/admin/article")
@PreAuthorize("@Permission.admin()")
public class ArticleAdminApi {

    /**
     * 注入的articleService层
     */
    @Autowired
    private IArticleService articleService;

    /**
     * 新建文章
     * @param article 文章Bean类
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PostMapping
    public ResponseResult postArticle(@RequestBody Article article) {
        return articleService.addArticle(article);
    }

    /**
     * 新建草稿，保存到MySQL中
     * 草稿的删除是直接删除，从MySQL中抹除
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PostMapping("/sketch")
    public ResponseResult sketchArticle(@RequestBody ArticleSketch articleSketch) {
        return articleService.addSketchArticle(articleSketch);
    }

    /**
     * 发布草稿文章
     * @param articleId 文章id
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PostMapping("/sketch/{articleId}")
    public ResponseResult publishSketchArticle(@PathVariable("articleId") String articleId) {
        return articleService.publishSketchArticle(articleId);
    }

    /**
     * 更新草稿文章
     * @param articleId articleId
     * @param articleSketch article
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PutMapping("/sketch/{articleId}")
    public ResponseResult updateSketchArticle(@PathVariable("articleId") String articleId, @RequestBody ArticleSketch articleSketch) {
        return articleService.updateSketchArticle(articleId, articleSketch);
    }

    /**
     * 删除草稿
     * @param articleId 草稿id
     * @return ResponseResult
     */
    @DeleteMapping("/sketch/{articleId}")
    public ResponseResult deleteSketchArticle(@PathVariable("articleId") String articleId) {
        return articleService.deleteSketchArticle(articleId);
    }

    /**
     * 通过草稿id获取草稿
     * @param articleId 草稿id
     * @return ResponseResult
     */
    @GetMapping("/sketch/{articleId}")
    public ResponseResult getSketchArticle(@PathVariable("articleId") String articleId) {
        return articleService.getSketchArticle(articleId);
    }

    /**
     * 草稿列表
     * @param page 页数
     * @param size 每页容量
     * @return ResponseResult
     */
    @GetMapping("/sketch/list")
    public ResponseResult listSketchArticle(@RequestParam("page")int page, @RequestParam("size") int size) {
        return articleService.listSketchArticles(page,size);
    }

    /**
     * 根据文章ID删除文章
     * 真实的在MySQL中删除，只有回收站的文章可以彻底删除
     * @param articleId 文章ID
     * @return ResponseResult
     */
    @DeleteMapping("/{articleId}")
    public ResponseResult deleteArticle(@PathVariable("articleId") String articleId) {
        return articleService.deleteArticle(articleId);
    }

    /**
     * 根据文章ID更新文章
     * 管理员有权限修改任何人的文章，但是署名权还是属于原作者
     * @param articleId 文章ID
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PutMapping("/{articleId}")
    public ResponseResult updateArticle(@PathVariable("articleId") String articleId, @RequestBody Article article) {
        return articleService.updateArticle(articleId,article);
    }


    /**
     * 根据文章ID获取文章
     * @param articleId 文章ID
     * @return ResponseResult
     */
    @GetMapping("/{articleId}")
    public ResponseResult getArticle(@PathVariable("articleId") String articleId) {
        boolean isFromAdmin = true;
        return articleService.getArticle(articleId,isFromAdmin);
    }

    /**
     * 获取文章列表
     * @param page 页数
     * @param size 每页容量
     * @return ResponseResult
     */
    @GetMapping("/list")
    public ResponseResult listArticle(@RequestParam("page")int page, @RequestParam("size") int size,
                                      @RequestParam(value = "keyword", required = false) String keyword,
                                      @RequestParam(value = "categoryId",required = false) String categoryId,
                                      @RequestParam(value = "state",required = false) String state) {
        return articleService.listArticles(page,size,keyword,categoryId,state);
    }

    /**
     * 根据文章id删除文章
     * 这里的删除是修改数据库中的状态
     * @param articleId 文章ID
     * @return ResponseResult
     */
    @DeleteMapping("/state/{articleId}/delete")
    public ResponseResult deleteArticleByState(@PathVariable("articleId") String articleId) {
        return articleService.updateArticleState(articleId,ARTICLE_REMOVE_STATE);
    }

    /**
     * 回收站恢复文章
     * @param articleId 文章ID
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PutMapping("/state/{articleId}/recover")
    public ResponseResult recoverArticle(@PathVariable("articleId") String articleId) {
        return articleService.updateArticleState(articleId,ARTICLE_PUBLISH_STATE);
    }



    /**
     * 根据文章ID置顶文章或者取消置顶
     * @param articleId 文章ID
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PutMapping("/state/{articleId}/top")
    public ResponseResult topArticleState(@PathVariable("articleId") String articleId) {
        return articleService.updateArticleState(articleId,ARTICLE_TOP_STATE);
    }

    /**
     * 通过文章id获取文章标题
     * @param articleId 文章id
     * @return ResponseResult
     */
    @GetMapping("/{articleId}/title")
    public ResponseResult getArticleTitle(@PathVariable("articleId") String articleId) {
        return articleService.getArticleTitle(articleId);
    }

}
