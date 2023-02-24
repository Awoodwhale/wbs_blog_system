package top.woodwhale.blog.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static top.woodwhale.blog.utils.Constants.Article.ARTICLE_MARKDOWN_TYPE;

/**
 * 草稿文章pojo
 */
@Entity
@Table(name = "tb_article_sketch")
public class ArticleSketchNoContent {
    @Id
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "user_id")
    private String userId;

    /**
     * 默认为markdown格式
     */
    @Column(name = "type")
    private String type = ARTICLE_MARKDOWN_TYPE;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "cover")
    private String cover;

    @Column(name = "summary")
    private String summary;

    @Column(name = "labels")
    private String labels;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @Column(name = "create_time")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @Column(name = "update_time")
    private Date updateTime;

    @Transient
    private List<String> labelTags = new ArrayList<>();

    public List<String> getLabelTags() {
        return labelTags;
    }

    public void setLabelTags(List<String> labelTags) {
        this.labelTags = labelTags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLabels() {
        labelTags.clear();
        if (labels != null) {
            if (labels.contains(",")) {
                labelTags.addAll(Arrays.asList(labels.split(",")));
            } else {
                labelTags.add(labels);
            }
        }
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
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
