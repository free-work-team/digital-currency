package com.jyt.terminal.model;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;


/**
 * <p>
 * 代理商分润设置实体类
 * </p>
 *
 * @author tangfq
 * @since 2021-02-07
 */
@TableName("t_agency_setting")
public class AgencySetting extends Model<AgencySetting>{

	private static final long serialVersionUID = 1L;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
 
    /**
     * 代理商名称
     */
    @TableField("agency_name")
    private String agencyName;

    /**
     * 树排序号
     */
    @TableField("num")
    private String num;

    /**
     * 树层级
     */
    @TableField("levels")
    private String levels;

    /**
     * 当前节点的所有父节点
     */
    @TableField("parent_ids")
    private String parentIds;

    
    /**
     * 父节点
     */
    @TableField("parent_id")
    private String parentId;
    
    /**
     * 是否为父节点,0：是父节点，1为子节点
     */
    @TableField("is_parent")
    private String isParent;
    
    /**
     * 单笔费用
     */
    @TableField("agency_single_fee")
    private String agencySingleFee;
    
    /**
     * 单笔手续费(%)
     */
    @TableField("agency_fee")
    private String agencyFee;
    
    /**
     * 代理商接收虚拟币的地址
     */
    @TableField("agency_address")
    private String agencyAddress;
    
    /**
     * 邮箱
     */
    @TableField("agency_email")
    private String agencyEmail;
    
    /**
     * 手机号
     */
    @TableField("agency_phone")
    private String agencyPhone;

    /**
     * 创建时间
     */
    @TableField("create_date")
    private Date createDate;
    
    /**
     * 代理商状态
     */
    @TableField("agency_statue")
    private String agencyStatue;
    
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getIsParent() {
		return isParent;
	}

	public void setIsParent(String isParent) {
		this.isParent = isParent;
	}

	public String getAgencySingleFee() {
		return agencySingleFee;
	}

	public void setAgencySingleFee(String agencySingleFee) {
		this.agencySingleFee = agencySingleFee;
	}

	public String getAgencyFee() {
		return agencyFee;
	}

	public void setAgencyFee(String agencyFee) {
		this.agencyFee = agencyFee;
	}

	public String getAgencyAddress() {
		return agencyAddress;
	}

	public void setAgencyAddress(String agencyAddress) {
		this.agencyAddress = agencyAddress;
	}

	public String getAgencyEmail() {
		return agencyEmail;
	}

	public void setAgencyEmail(String agencyEmail) {
		this.agencyEmail = agencyEmail;
	}

	public String getAgencyPhone() {
		return agencyPhone;
	}

	public void setAgencyPhone(String agencyPhone) {
		this.agencyPhone = agencyPhone;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getAgencyStatue() {
		return agencyStatue;
	}

	public void setAgencyStatue(String agencyStatue) {
		this.agencyStatue = agencyStatue;
	}
    
	
}
