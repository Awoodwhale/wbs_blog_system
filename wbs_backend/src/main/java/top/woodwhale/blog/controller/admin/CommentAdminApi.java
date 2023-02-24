package top.woodwhale.blog.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.woodwhale.blog.pojo.Comment;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.ICommentService;

/**
 * 评论API
 */
@RestController
@RequestMapping("/admin/comment")
public class CommentAdminApi {

    @Autowired
    private ICommentService commentService;

    /**
     * 根据评论ID删除评论
     *
     * @param commentId 评论ID
     * @return ResponseResult
     */
    @DeleteMapping("/{commentId}")
    public ResponseResult deleteComment(@PathVariable("commentId") String commentId) {
        return commentService.deleteComment(commentId);
    }

    /**
     * 获取评论列表
     *
     * @param page 页数
     * @param size 每页容量
     * @return ResponseResult
     */
    @GetMapping("/list")
    public ResponseResult listComments(@RequestParam("page") int page, @RequestParam("size") int size) {
        return commentService.listComments(page, size);
    }

    /**
     * 将评论状态置为删除状态
     *
     * @param commentId 评论id
     * @return ResponseResult
     */
    @PutMapping("/{commentId}/delete")
    public ResponseResult deleteCommentState(@PathVariable("commentId") String commentId) {
        return commentService.deleteCommentState(commentId);
    }

    /**
     * 恢复评论的状态
     *
     * @param commentId 评论id
     * @return ResponseResult
     */
    @PutMapping("/{commentId}/recover")
    public ResponseResult recoverCommentState(@PathVariable("commentId") String commentId) {
        return commentService.recoverCommentState(commentId);
    }

    /**
     * 根据评论ID置顶评论
     *
     * @param commentId 评论ID
     * @return ResponseResult
     */
    @PutMapping("/{commentId}/top")
    public ResponseResult topCommentState(@PathVariable("commentId") String commentId) {
        return commentService.topCommentState(commentId);
    }

    /**
     * 管理员回复评论
     *
     * @param comment 评论pojo
     * @return ResponseResult
     */
    @PostMapping
    public ResponseResult replyComment(@RequestBody Comment comment) {
        return commentService.postComment(comment);
    }
}
