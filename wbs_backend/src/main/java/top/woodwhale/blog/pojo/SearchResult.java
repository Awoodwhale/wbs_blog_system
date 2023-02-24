package top.woodwhale.blog.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;
import java.util.Date;

public class SearchResult implements Serializable {

    @Field("id")
    private String id;

    @Field("blog_content")
    private String blogContent;

    @Field("blog_create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date blogCreateTime;

    @Field("blog_labels")
    private String blogLabels;

    @Field("blog_url")
    private String blogUrl;

    @Field("blog_title")
    private String blogTitle;

    @Field("blog_view_count")
    private int blogViewCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBlogContent() {
        return blogContent;
    }

    public void setBlogContent(String blogContent) {
        this.blogContent = blogContent;
    }

    public Date getBlogCreateTime() {
        return blogCreateTime;
    }

    public void setBlogCreateTime(Date blogCreateTime) {
        this.blogCreateTime = blogCreateTime;
    }

    public String getBlogLabels() {
        return blogLabels;
    }

    public void setBlogLabels(String blogLabels) {
        this.blogLabels = blogLabels;
    }

    public String getBlogUrl() {
        return blogUrl;
    }

    public void setBlogUrl(String blogUrl) {
        this.blogUrl = blogUrl;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public int getBlogViewCount() {
        return blogViewCount;
    }

    public void setBlogViewCount(int blogViewCount) {
        this.blogViewCount = blogViewCount;
    }
}
