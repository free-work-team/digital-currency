package com.jyt.terminal.model;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

@TableName("t_customer")
public class Customer extends Model<Customer>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 客户ID
	 */
	@TableId(value="id",type=IdType.AUTO)
	private Integer id;
	/**
	 * 客户姓名
	 */
	@TableField("cust_name")
	private String custName;
	/**
	 * 邮箱
	 */
	@TableField("e_mail")
	private String email;
	/**
	 * 证件类型
	 */
	@TableField("card_type")
	private Integer cardType;
	/**
	 * 身份ID正面照
	 */
	@TableField("id_card_positive")
	private String idCardPositive;
	/**
	 * 身份ID反面照
	 */
	@TableField("id_card_obverse")
	private String idCardObverse;
	/**
	 * 手持身份证照
	 */
	@TableField("id_card_handheld")
	private String idCardHandheld;
	/**
	 * 护照证照
	 */
	@TableField("id_passport")
	private String idPassport;
	/**
	 * 客户状态（0：待审核，1：审核通过，2审核未通过）
	 */
	private Integer status;
	/**
	 * 审核意见
	 */
	@TableField("audit_opinion")
	private String auditOpinion;
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

	public Integer getCardType() {
		return cardType;
	}

	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}

	public String getIdPassport() {
		return idPassport;
	}

	public void setIdPassport(String idPassport) {
		this.idPassport = idPassport;
	}

	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getCustName() {
		return custName;
	}


	public void setCustName(String custName) {
		this.custName = custName;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getIdCardPositive() {
		return idCardPositive;
	}


	public void setIdCardPositive(String idCardPositive) {
		this.idCardPositive = idCardPositive;
	}


	public String getIdCardObverse() {
		return idCardObverse;
	}


	public void setIdCardObverse(String idCardObverse) {
		this.idCardObverse = idCardObverse;
	}


	public String getIdCardHandheld() {
		return idCardHandheld;
	}


	public void setIdCardHandheld(String idCardHandheld) {
		this.idCardHandheld = idCardHandheld;
	}


	public Integer getStatus() {
		return status;
	}


	public void setStatus(Integer status) {
		this.status = status;
	}


	public String getAuditOpinion() {
		return auditOpinion;
	}


	public void setAuditOpinion(String auditOpinion) {
		this.auditOpinion = auditOpinion;
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
