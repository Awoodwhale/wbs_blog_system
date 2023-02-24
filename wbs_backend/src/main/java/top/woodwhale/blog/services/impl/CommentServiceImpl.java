package top.woodwhale.blog.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.woodwhale.blog.dao.ArticleNoContentDao;
import top.woodwhale.blog.dao.CommentDao;
import top.woodwhale.blog.dao.UserNoPasswordDao;
import top.woodwhale.blog.pojo.ArticleNoContent;
import top.woodwhale.blog.pojo.Comment;
import top.woodwhale.blog.pojo.User;
import top.woodwhale.blog.pojo.UserNoPassword;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.response.ResponseState;
import top.woodwhale.blog.services.ICommentService;
import top.woodwhale.blog.services.IUserService;
import top.woodwhale.blog.utils.SnowflakeIdUtils;
import top.woodwhale.blog.utils.TextUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static top.woodwhale.blog.utils.Constants.Article.ARTICLE_REMOVE_STATE;
import static top.woodwhale.blog.utils.Constants.Comment.*;
import static top.woodwhale.blog.utils.Constants.Page.DEFAULT_PAGE;
import static top.woodwhale.blog.utils.Constants.Page.MAX_SIZE;
import static top.woodwhale.blog.utils.Constants.User.ROLE_ADMIN;

@Slf4j
@Service
@Transactional
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private SnowflakeIdUtils idUtils;

    @Autowired
    private IUserService userService;

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private ArticleNoContentDao articleNoContentDao;

    @Autowired
    private UserNoPasswordDao userNoPasswordDao;

    @Autowired
    private MailService mailService;

    /**
     * 发表评论
     *
     * @param comment 评论bean类
     * @return ResponseResult
     */
    @Override
    public ResponseResult postComment(Comment comment) {
        // 1. 检查用户是否登录
        User user = userService.checkUser();
        if (user == null) {
            return ResponseResult.GET(ResponseState.NOT_LOGIN);
        }
        // 2. 检查内容
        if (TextUtils.isEmpty(comment.getArticleId())) {
            return ResponseResult.FAILED(TextUtils.notNullable("评论文章Id"));
        }
        ArticleNoContent articleInMysql = articleNoContentDao.findOneById(comment.getArticleId());
        if (articleInMysql == null ||
                articleInMysql.getState().equals(ARTICLE_REMOVE_STATE)) {
            // 如果评论的文章是被删除状态，那么无法评论
            return ResponseResult.FAILED(TextUtils.notExist("评论的文章"));
        }
        if (TextUtils.isEmpty(comment.getContent())) {
            return ResponseResult.FAILED(TextUtils.notNullable("评论内容"));
        }
        // 检查父评论是否存在
        UserNoPassword parentCommentWriter = null;
        Comment parentComment = null;
        String parentId = comment.getParentId();
        if (!TextUtils.isEmpty(parentId)) {
            parentComment = commentDao.findOneById(parentId);
            if (parentComment == null) {
                return ResponseResult.FAILED(TextUtils.notExist("父评论"));
            } else {
                // 如果有父评论，看看这个父评论的文章id和这次发表的评论的id是否一致
                if (!parentComment.getArticleId().equals(comment.getArticleId())) {
                    return ResponseResult.FAILED("父评论文章与本次评论文章不一致");
                }
            }
            // 找到父评论的发布者
            parentCommentWriter = userNoPasswordDao.findOneById(parentComment.getUserId());
        }
        // 3. 补全内容
        comment.setId(String.valueOf(idUtils.nextId()));
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        comment.setUserId(user.getId());
        comment.setUsername(user.getUsername());
        comment.setUserAvatar(user.getAvatar());
        comment.setState(COMMENT_NORMAL_STATE);
        // 4. 保存入库
        commentDao.save(comment);
        // 5. 发送邮件给文章作者
        if (parentCommentWriter != null) {
            // 自己回复自己也是不会邮箱通知的
            if (!parentCommentWriter.getId().equals(user.getId())) {
                // 如果父评论被评论了，给父评论的发布者发送邮箱，通知他被回复了
                mailService.sendCommentNotify(parentCommentWriter.getEmail(),
                        "您在文章 《" + articleInMysql.getTitle() + "》 中的评论: " + parentComment.getContent() +
                                " ，被用户 " + user.getUsername() + " 回复啦！\n请前往文章详情查看！");
            }
        } else {
            // 自己评论自己是不会给自己发送邮箱的
            if (!articleInMysql.getUser().getId().equals(user.getId())) {
                // 如果父评论不存在，那么通知给文章作者
                mailService.sendCommentNotify(articleInMysql.getUser().getEmail(),
                        "您编写的文章 《" + articleInMysql.getTitle() + "》 " +
                                "，被用户 " + user.getUsername() + " 评论啦！\n" + "内容为: " + comment.getContent() + " \n请前往文章详情查看！");
            }
        }
        return ResponseResult.SUCCESS(TextUtils.successAdd("评论"));
    }

    /**
     * 获取文章的评论列表
     *
     * @param articleId 文章id
     * @return ResponseResult
     */
    @Override
    public ResponseResult listCommentsByArticleId(String articleId) {
        ArticleNoContent articleInMysql = articleNoContentDao.findOneById(articleId);
        if (articleInMysql == null ||
                articleInMysql.getState().equals(ARTICLE_REMOVE_STATE)) {
            // 如果评论的文章是被删除，那么无法评论
            return ResponseResult.FAILED(TextUtils.notExist("文章"));
        }
        // 按照createTime和state降序，并且不给出被删除的评论
        List<Comment> comments = commentDao.listCommentsByArticleId(articleId);
        List<Comment> res = new ArrayList<>();
        comments.forEach(item -> {
            Comment tmp = new Comment();
            BeanUtils.copyProperties(item,tmp);
            if (tmp.getState().equals(COMMENT_REMOVE_STATE)) {
                tmp.setContent("该评论已被删除！");
            }
            res.add(tmp);
        });
        return ResponseResult.SUCCESS(TextUtils.successGet("评论列表")).setData(res);
    }

    /**
     * 删除评论（只有管理员可以调用)
     * 直接从数据库中删除，父评论为该id的评论，父评论都置为空（物理删除）
     *
     * @param commentId 评论id
     * @return ResponseResult
     */
    @Override
    public ResponseResult deleteComment(String commentId) {
        Comment commentInMysql = commentDao.findOneById(commentId);
        if (commentInMysql == null) {
            return ResponseResult.FAILED(TextUtils.notExist("评论"));
        }
        int res = commentDao.deleteOneById(commentId);
        int res2 = commentDao.updateCommentParent(commentId);
        if (res > 0) {
            log.info("删除了父评论id为:" + commentId + "的记录 " + res2 + "条");
            return ResponseResult.SUCCESS(TextUtils.successDelete("评论"));
        }
        return ResponseResult.FAILED(TextUtils.smtError("评论删除"));
    }


    /**
     * 管理员获取评论list
     *
     * @param page 页数
     * @param size 每页容量
     * @return ResponseResult
     */
    @Override
    public ResponseResult listComments(int page, int size) {
        page = Math.max(page, DEFAULT_PAGE);
        size = Math.min(MAX_SIZE, Math.max(size, 1));
        Page<Comment> all = commentDao
                .findAll(PageRequest
                        .of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime")));
        return ResponseResult.SUCCESS(TextUtils.successGet("评论列表")).setData(all);
    }

    /**
     * 管理员置顶评论
     *
     * @param commentId 评论id
     * @return ResponseResult
     */
    @Override
    public ResponseResult topCommentState(String commentId) {
        // 1. 判断评论是否存在
        Comment commentInMysql = commentDao.findOneById(commentId);
        if (commentInMysql == null || commentInMysql.getState().equals(COMMENT_REMOVE_STATE)) {
            return ResponseResult.FAILED(TextUtils.notExist("评论"));
        }
        // 2. 更改评论状态，如果不是置顶状态，修改为置顶，如果是置顶，取消置顶
        if (commentInMysql.getState().equals(COMMENT_TOP_STATE)) {
            int res = commentDao.updateCommentState(COMMENT_NORMAL_STATE, commentId);
            if (res > 0) {
                return ResponseResult.SUCCESS(TextUtils.successUpdate("评论取消置顶状态"));
            }
            return ResponseResult.FAILED(TextUtils.smtError("评论取消置顶"));
        } else {
            int res = commentDao.updateCommentState(COMMENT_TOP_STATE, commentId);
            if (res > 0) {
                return ResponseResult.SUCCESS(TextUtils.successUpdate("评论置顶状态"));
            }
            return ResponseResult.FAILED(TextUtils.smtError("评论置顶"));
        }
    }

    /**
     * 将评论状态改为删除状态
     *
     * @param commentId 评论id
     * @return ResponseResult
     */
    @Override
    public ResponseResult deleteCommentState(String commentId) {
        User user = userService.checkUser();
        if (user == null) {
            return ResponseResult.GET(ResponseState.NOT_LOGIN);
        }
        Comment commentInMysql = commentDao.findOneById(commentId);
        if (commentInMysql == null) {
            return ResponseResult.FAILED(TextUtils.notExist("评论"));
        }
        // 如果已经是删除状态，无法删除
        if (commentInMysql.getState().equals(COMMENT_REMOVE_STATE)) {
            return ResponseResult.FAILED("该评论已被删除");
        }
        // 如果当前用户不是评论者，也不是管理员，是没有权限删除该评论的
        if (!commentInMysql.getUserId().equals(user.getId()) && !user.getRoles().equals(ROLE_ADMIN)) {
            return ResponseResult.GET(ResponseState.NO_PERMISSION);
        }
        int res = commentDao.updateCommentState(COMMENT_REMOVE_STATE, commentId);
        if (res > 0) {
            return ResponseResult.SUCCESS(TextUtils.successUpdate("评论删除状态"));
        }
        return ResponseResult.FAILED(TextUtils.smtError("评论删除"));
    }

    /**
     * 恢复评论（只有管理员可以恢复评论）
     *
     * @param commentId 评论id
     * @return ResponseResult
     */
    @Override
    public ResponseResult recoverCommentState(String commentId) {
        Comment commentInMysql = commentDao.findOneById(commentId);
        if (commentInMysql == null) {
            return ResponseResult.FAILED(TextUtils.notExist("评论"));
        }
        if (!commentInMysql.getState().equals(COMMENT_REMOVE_STATE)) {
            return ResponseResult.FAILED("该评论不处于删除状态");
        }
        int res = commentDao.updateCommentState(COMMENT_NORMAL_STATE, commentId);
        if (res > 0) {
            return ResponseResult.SUCCESS(TextUtils.successUpdate("评论正常状态"));
        }
        return ResponseResult.FAILED(TextUtils.failUpdate("评论状态"));
    }
}
