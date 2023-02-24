package top.woodwhale.blog.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 用户实体类
 */
@Entity
@Table ( name ="tb_user" )
public class UserNoPassword {
	public UserNoPassword() {
	}

	@Id
	private String id;

	@Column(name = "user_name" )
	private String username;

	@Column(name = "roles" )
	private String roles;

	@Column(name = "avatar" )
	private String avatar;

	@Column(name = "email" )
	private String email;

	@Column(name = "sign" )
	private String sign;

	@Column(name = "state" )
	private String state;

	@Column(name = "reg_ip" )
	private String regIp;

	@Column(name = "login_ip" )
	private String loginIp;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	@Column(name = "create_time" )
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	@Column(name = "update_time" )
	private Date updateTime;

	@Column(name = "profession")
	private String profession;

	@Column(name = "major")
	private String major;

	@Column(name = "location")
	private String location;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String user_name) {
		this.username = user_name;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}


	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}


	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}


	public String getRegIp() {
		return regIp;
	}

	public void setRegIp(String reg_ip) {
		this.regIp = reg_ip;
	}


	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String login_ip) {
		this.loginIp = login_ip;
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

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
