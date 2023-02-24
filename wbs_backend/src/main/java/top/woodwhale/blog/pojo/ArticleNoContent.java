package top.woodwhale.blog.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 文章实体类
 */
@Entity
@Table ( name ="tb_article" )
public class ArticleNoContent {

	@Id
	private String id;

	@Column(name = "title" )
	private String title;

	@Column(name = "user_id" )
	private String userId;

	@Column(name = "user_avatar" )
	private String userAvatar;

	@Column(name = "user_name" )
	private String username;

	@Column(name = "category_id" )
	private String categoryId;


	@Column(name = "type" )
	private String type;

	@Column(name = "cover")
	private String cover;

	@Column(name = "state" )
	private String state;

	@Column(name = "summary" )
	private String summary;

	@Column(name = "labels" )
	private String labels;

	@Column(name = "view_count" )
	private long viewCount;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	@Column(name = "create_time" )
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	@Column(name = "update_time" )
	private Date updateTime;

	@OneToOne(targetEntity = UserNoPassword.class)
	@JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
	private UserNoPassword user;

	@Transient
	private List<String> labelTags = new ArrayList<>();

	public List<String> getLabelTags() {
		return labelTags;
	}

	public void setLabelTags(List<String> labelTags) {
		this.labelTags = labelTags;
	}

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


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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


	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String category_id) {
		this.categoryId = category_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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


	public long getViewCount() {
		return viewCount;
	}

	public void setViewCount(long view_count) {
		this.viewCount = view_count;
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
