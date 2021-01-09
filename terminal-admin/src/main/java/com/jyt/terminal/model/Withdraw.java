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
 * 比特币提现流水表实体类
 * </p>
 *
 * @author sunshubin
 * @since 2019-04-29
 */
@TableName("t_withdraw")
public class Withdraw extends Model<Withdraw> {

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
    @TableField("target_address")
    private String targetAddress;
    
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
    @TableField("c_fee")
    private String cFee;
    
    /**
     * 交易金额（现金）
     */
    private String cash;
    
    /**
     * 实际出钞数量
     */
    @TableField("out_count")
    private String outCount;
    
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
     * 交易状态
     */
    @TableField("trans_status")
    private Integer transStatus;
    
    /**
     * 出钞状态
     */
    @TableField("redeem_status")
    private Integer redeemStatus;

    /**
     * 备注
     */
    private String remark;
    /**
     * 渠道类型（1：bitgo，2：coinbase）
     */
    @TableField("channel")
    private Integer channel;

    private String strategy;
    /**
     * 币种
     */
    
    private String currency;
    
    @TableField("sell_type")
    private String sellType;
    
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
    
	public String getCryptoCurrency() {
		return cryptoCurrency;
	}

	public void setCryptoCurrency(String cryptoCurrency) {
		this.cryptoCurrency = cryptoCurrency;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getSellType() {
		return sellType;
	}

	public void setSellType(String sellType) {
		this.sellType = sellType;
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

	public String getTargetAddress() {
		return targetAddress;
	}

	public void setTargetAddress(String targetAddress) {
		this.targetAddress = targetAddress;
	}

	public String getOutCount() {
		return outCount;
	}

	public void setOutCount(String outCount) {
		this.outCount = outCount;
	}

	public Integer getTransStatus() {
		return transStatus;
	}

	public void setTransStatus(Integer transStatus) {
		this.transStatus = transStatus;
	}

	public Integer getRedeemStatus() {
		return redeemStatus;
	}

	public void setRedeemStatus(Integer redeemStatus) {
		this.redeemStatus = redeemStatus;
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

	public String getcFee() {
		return cFee;
	}

	public void setcFee(String cFee) {
		this.cFee = cFee;
	}

	public String getCash() {
		return cash;
	}

	public void setCash(String cash) {
		this.cash = cash;
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

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
	
}
