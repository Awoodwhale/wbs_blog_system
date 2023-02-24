package top.woodwhale.blog.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

import static top.woodwhale.blog.utils.Constants.Comment.COMMENT_NORMAL_STATE;

/**
 * 评论实体类
 */
@Entity
@Table(name = "tb_comment")
public class Comment {

    @Id
    private String id;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "article_id")
    private String articleId;

    @Column(name = "content")
    private String content;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_avatar")
    private String userAvatar;

    @Column(name = "user_name")
    private String username;

    @Column(name = "state")
    private String state = COMMENT_NORMAL_STATE;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @Column(name = "create_time")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @Column(name = "update_time")
    private Date updateTime;

    @OneToOne(targetEntity = UserNoPassword.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UserNoPassword user;

    public UserNoPassword getUser() {
        return user;
    }

    public void setUser(UserNoPassword user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parent_content) {
        this.parentId = parent_content;
    }


    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String article_id) {
        this.articleId = article_id;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String user_id) {
        this.userId = user_id;
    }


    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String user_avatar) {
        this.userAvatar = user_avatar;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String user_name) {
        this.username = user_name;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
