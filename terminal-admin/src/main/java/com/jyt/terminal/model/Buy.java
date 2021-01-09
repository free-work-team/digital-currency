package com.jyt.terminal.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.crypto.Data;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * <p>
 * 比特币购买流水表实体类
 * </p>
 *
 * @author sunshubin
 * @since 2019-04-29
 */
@TableName("t_buy")
public class Buy extends Model<Buy> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
 
    /**
     * 交易号
     */
    @TableField("trans_id")
    private String transId;
    
    /**
     * 终端机号
     */
    @TableField("terminal_no")
    private String terminalNo;
    /**
     * 加密币种
     */
    @TableField("crypto_currency")
    private String cryptoCurrency;
    
    /**
     * 钱包id
     */
    private String address;
    
    /**
     * 交易比特币量
     */
    private String amount;
    
    /**
     * 商户手续费
     */
    private String fee;
    
    /**
     * 平台手续费
     */
    @TableField("channel_fee")
    private String channelFee;
    
    /**
     * 交易金额（现金）
     */
    private String cash;
    
    /**
     * 扩展订单号
     */
    @TableField("ext_id")
    private String extId;
    
    /**
     * 创建时间
     */
    @TableField("trans_time")
    private String transTime;

    /**
     * 状态
     */
    private String status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 渠道类型（1：bitgo，2：coinbase）
     */
    @TableField("channel")
    private Integer channel;
    /**
     * 币种
     */
    @TableField("currency")
    private String currency;
   /**
    * 交易所
    */
    private String strategy;
    
    /**
     * 交易时比特币价格
     */
    private String price;
    
    /**
     * 交易策略
     */
    @TableField("exchange_strategy")
    private Integer exchangeStrategy;
    
    /**
     * 客户ID（邮箱、手机号）
     */
    @TableField("customer_id")
    private String customerId;
    
    /**
     * 交易所汇率
     */
    @TableField("exchange_rate")
    private String exchangeRate;
    
    /**
     * 最后修改时间
     */
    @TableField("update_time")
    private Date updateTime;
    
	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

	public Integer getId() {
		return id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public String getTerminalNo() {
		return terminalNo;
	}

	public void setTerminalNo(String terminalNo) {
		this.terminalNo = terminalNo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getCash() {
		return cash;
	}

	public void setCash(String cash) {
		this.cash = cash;
	}
	
	public String getChannelFee() {
		return channelFee;
	}

	public void setChannelFee(String channelFee) {
		this.channelFee = channelFee;
	}

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Integer getExchangeStrategy() {
		return exchangeStrategy;
	}

	public void setExchangeStrategy(Integer exchangeStrategy) {
		this.exchangeStrategy = exchangeStrategy;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCryptoCurrency() {
		return cryptoCurrency;
	}

	public void setCryptoCurrency(String cryptoCurrency) {
		this.cryptoCurrency = cryptoCurrency;
	}

	@Override
    protected Serializable pkVal() {
        return this.id;
    }

	
}
