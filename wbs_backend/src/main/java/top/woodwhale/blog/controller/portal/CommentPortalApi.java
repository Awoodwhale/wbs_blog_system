package top.woodwhale.blog.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.woodwhale.blog.interceptor.CheckTooFrequentCommit;
import top.woodwhale.blog.pojo.Comment;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.ICommentService;

/**
 * 评论门户 API
 */
@RestController
@RequestMapping("/portal/comment")
@PreAuthorize("@Permission.sureComment()")  // 判断是否开启了评论权限
public class CommentPortalApi {

    /**
     * 评论service层
     */
    @Autowired
    private ICommentService commentService;

    /**
     * 发表评论
     *
     * @param comment 评论Bean类
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PostMapping
    public ResponseResult postComment(@RequestBody Comment comment) {
        return commentService.postComment(comment);
    }

    /**
     * 根据评论ID删除评论
     *
     * @param commentId 评论ID
     * @return ResponseResult
     */
    @DeleteMapping("/{commentId}")
    public ResponseResult deleteComment(@PathVariable("commentId") String commentId) {
        return commentService.deleteCommentState(commentId);
    }

    /**
     * 根据文章ID获取这篇文章的评论
     *
     * @param articleId 文章ID
     * @return ResponseResult
     */
    @GetMapping("/list/{articleId}")
    public ResponseResult listComments(@PathVariable("articleId") String articleId) {
        return commentService.listCommentsByArticleId(articleId);
    }
}
