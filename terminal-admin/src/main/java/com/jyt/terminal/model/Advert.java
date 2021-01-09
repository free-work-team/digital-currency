package com.jyt.terminal.model;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

@TableName("t_advert")
public class Advert extends Model<Advert>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
     * 主键id
     */
    @TableId(value = "id",type=IdType.AUTO)
	private Integer id;
	
	
	@TableField("advert_title")
	private String advertTitle;
	
	@TableField("advert_content")
	private String advertContent;
	
	
	/**
	 * 创建人
	 */
	@TableField("create_user")
	private String createUser;
	/**
	 * 创建时间
	 */
	@TableField("create_time")
	private Date createTime;
	/**
	 * 修改人
	 */
	@TableField("update_user")
	private String updateUser;
	/**
	 * 修改时间
	 */
	@TableField("update_time")
	private Date updateTime;
		

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAdvertTitle() {
		return advertTitle;
	}

	public void setAdvertTitle(String advertTitle) {
		this.advertTitle = advertTitle;
	}

	public String getAdvertContent() {
		return advertContent;
	}

	public void setAdvertContent(String advertContent) {
		this.advertContent = advertContent;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	protected Serializable pkVal() {
		return null;
	}

}
