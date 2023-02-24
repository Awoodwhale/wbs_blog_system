package top.woodwhale.blog.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import java.util.Date;

/**
 * 轮播图实体类
 */
@Entity
@Table ( name ="tb_looper" )
public class Looper {

	@Id
	private String id;

	@Column(name = "title" )
	private String title;

	@Column(name = "`order`" )
	private long order = 1;

	@Column(name = "state" )
	private String state;

	@Column(name = "target_url" )
	private String targetUrl;

	@Column(name = "image_url" )
	private String imageUrl;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	@Column(name = "create_time" )
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	@Column(name = "update_time" )
	private Date updateTime;



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


	public long getOrder() {
		return order;
	}

	public void setOrder(long order) {
		this.order = order;
	}


	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}


	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String target_url) {
		this.targetUrl = target_url;
	}


	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String image_url) {
		this.imageUrl = image_url;
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
