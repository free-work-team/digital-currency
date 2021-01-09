package com.jyt.terminal.model;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

@TableName("t_agreement")
public class Agreement extends Model<Agreement>{
	private static final long serialVersionUID = 1L;
	
	/**
     * 主键id
     */
    @TableId(value = "id",type=IdType.AUTO)
	private Integer id;
	/**
	 * 协议标题
	 */
	@TableField("agreement_title")
	private String agreementTitle;
	/**
	 * 协议内容
	 */
	@TableField("agreement_content")
	private String agreementContent;
	/**
	 * 隐私政策标题
	 */
	@TableField("privacy_policy_title")
	private String privacyPolicyTitle;
	/**
	 * 隐私政策内容
	 */
	@TableField("privacy_policy_content")
	private String privacyPolicyContent;
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

	public String getAgreementTitle() {
		return agreementTitle;
	}

	public void setAgreementTitle(String agreementTitle) {
		this.agreementTitle = agreementTitle;
	}

	public String getAgreementContent() {
		return agreementContent;
	}

	public void setAgreementContent(String agreementContent) {
		this.agreementContent = agreementContent;
	}

	public String getPrivacyPolicyTitle() {
		return privacyPolicyTitle;
	}

	public void setPrivacyPolicyTitle(String privacyPolicyTitle) {
		this.privacyPolicyTitle = privacyPolicyTitle;
	}

	public String getPrivacyPolicyContent() {
		return privacyPolicyContent;
	}

	public void setPrivacyPolicyContent(String privacyPolicyContent) {
		this.privacyPolicyContent = privacyPolicyContent;
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
		return this.id;
	}

}
