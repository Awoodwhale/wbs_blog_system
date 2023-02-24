package top.woodwhale.blog.services;

import top.woodwhale.blog.pojo.Comment;
import top.woodwhale.blog.response.ResponseResult;

public interface ICommentService {
    ResponseResult postComment(Comment comment);

    ResponseResult listCommentsByArticleId(String articleId);

    ResponseResult deleteComment(String commentId);

    ResponseResult listComments(int page, int size);

    ResponseResult topCommentState(String commentId);

    ResponseResult deleteCommentState(String commentId);

    ResponseResult recoverCommentState(String commentId);
}
