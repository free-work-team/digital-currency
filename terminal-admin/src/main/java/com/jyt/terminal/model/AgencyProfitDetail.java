package com.jyt.terminal.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
@TableName("t_agency_profit_detail")
public class AgencyProfitDetail extends Model<AgencyProfitDetail>{

	
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
     * 代理商分润的比例(%)
     */
    @TableField("agency_fee")
    private BigDecimal agencyFee;
    
    /**
     * 单笔费用
     */
    @TableField("agency_single_fee")
    private BigDecimal agencySingleFee;
    
    /**
     * 代理商id
     */
    @TableField("agency_id")
    private String agencyId;
    
    /**
     * 订单金额
     */
    @TableField("agency_amount")
    private BigDecimal agencyAmount;
    

    /**
     * 代理商接收虚拟币的地址
     */
    @TableField("agency_receive_address")
    private String agencyReceiveAddress;
    

    /**
     * 发送虚拟币地址
     */
    @TableField("send_address")
    private String sendAddress;
    

    /**
     * 发送虚拟币的客户id
     */
    @TableField("send_customer_id")
    private String sendCustomerId;
    
    /**
     * 交易时间
     */
    @TableField("trade_time")
    private Date tradeTime;
    
    /**
     * 交易状态（0：初始化，1：处理中，2：已确认）
     */
    @TableField("trade_status")
    private String tradeStatus;
    
    /**
     * 虚拟币交易平台的订单号
     */
    @TableField("trade_upper_id")
    private String tradeUpperId;
    
    /**
     * 产生买卖币的原始交易订单号
     */
    @TableField("trade_source_id")
    private String tradeSourceId;

    /**
     * 产生买卖币的原始交易订单号
     */
    @TableField("update_time")
    private Date updateTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BigDecimal getAgencyFee() {
		return agencyFee;
	}

	public void setAgencyFee(BigDecimal agencyFee) {
		this.agencyFee = agencyFee;
	}

	public BigDecimal getAgencySingleFee() {
		return agencySingleFee;
	}

	public void setAgencySingleFee(BigDecimal agencySingleFee) {
		this.agencySingleFee = agencySingleFee;
	}

	public String getAgencyId() {
		return agencyId;
	}

	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
	}

	public BigDecimal getAgencyAmount() {
		return agencyAmount;
	}

	public void setAgencyAmount(BigDecimal agencyAmount) {
		this.agencyAmount = agencyAmount;
	}

	public String getAgencyReceiveAddress() {
		return agencyReceiveAddress;
	}

	public void setAgencyReceiveAddress(String agencyReceiveAddress) {
		this.agencyReceiveAddress = agencyReceiveAddress;
	}

	public String getSendAddress() {
		return sendAddress;
	}

	public void setSendAddress(String sendAddress) {
		this.sendAddress = sendAddress;
	}

	public String getSendCustomerId() {
		return sendCustomerId;
	}

	public void setSendCustomerId(String sendCustomerId) {
		this.sendCustomerId = sendCustomerId;
	}

	public Date getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}

	public String getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public String getTradeUpperId() {
		return tradeUpperId;
	}

	public void setTradeUpperId(String tradeUpperId) {
		this.tradeUpperId = tradeUpperId;
	}

	public String getTradeSourceId() {
		return tradeSourceId;
	}

	public void setTradeSourceId(String tradeSourceId) {
		this.tradeSourceId = tradeSourceId;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

    
	
}
