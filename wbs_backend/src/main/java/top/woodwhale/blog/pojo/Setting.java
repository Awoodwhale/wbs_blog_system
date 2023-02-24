package top.woodwhale.blog.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import java.util.Date;

/**
 * 用户状态设置 实体类
 */
@Entity
@Table ( name ="tb_settings" )
public class Setting {

	@Id
	private String id;

	// ``号用来防止MySQL当作关键字
	@Column(name = "`key`" )
	private String key;

	// 1表示存在，0表示删除
	@Column(name = "`value`" )
	private String value;

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


	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}


	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
