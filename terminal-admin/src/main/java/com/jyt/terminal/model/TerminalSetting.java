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
 * 终端机设置
 * </p>
 *
 * @author sunshubin
 * @since 2019-05-05
 */
@TableName("t_terminal_setting")
public class TerminalSetting extends Model<TerminalSetting> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id 终端号
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    
    /**
     *  终端号
     */
    @TableField(value = "terminal_no")
    private String terminalNo;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * md5密码盐
     */
    private String salt;
    
    /**
     * 商户名
     */
    @TableField("merchant_name")
    private String merchantName;
    
    /**
     * 电话热线
     */
    private String hotline;
    
    /**
     * 邮箱
     */
    @TableField("e_mail")
    private String email;
    
    /**
     * 购买手续费
     */
    @TableField("buy_transaction_fee")
    private String buyTransactionFee;
    
    /**
     * 购买手续费(单笔)
     */
    @TableField("buy_single_fee")
    private String buySingleFee;
    
    /**
     * 出售手续费
     */
    @TableField("sell_transaction_fee")
    private String sellTransactionFee;
    
    /**
     * 出售手续费(单笔)
     */
    @TableField("sell_single_fee")
    private String sellSingleFee;
    
    /**
     * 出售手续费(单笔)
     */
    private String rate;
    
    /**
     * 最低钱包余额
     */
    @TableField("min_need_bitcoin")
    private String minNeedBitcoin;
    
    /**
     * 最低交易金额
     */
    @TableField("min_need_cash")
    private String minNeedCash;
    
    /**
     * 快照
     */
    @TableField("channel_param")
    private String channelParam;

    /**
     * 是否已发送（0：未发送；1：已发送）
     */
    @TableField("is_send")
    private int isSend;
    
    private int status;
    
    @TableField("create_time")
    private Date createTime;
    
    @TableField("update_time")
    private Date updateTime;
    /**
     * 热钱包
     */
    @TableField("hot_wallet")
    private Integer hotWallet;
    
    /**
     * 币种
     */
    @TableField("currency")
    private String currency;
    /**
     * 交易方式
     */
    @TableField("sell_type")
    private Integer sellType;
    
    /**
     * 交易所
     */
    @TableField("exchange")
    private Integer exchange;
    
    /**
     * 限制金额
     */
    @TableField("limit_cash")
    private String limitCash;
    
    /**
     * 
     */
    @TableField("kyc_url")
    private String kycUrl;
    /**
     * 交易策略
     */
    @TableField("exchange_strategy")
    private Integer exchangeStrategy;
    /**
     * 币汇率来源
     */
    @TableField("rate_source")
    private Integer rateSource;
    /**
     * 是否开启kyc(1:是，2:否)
     */
    @TableField("kyc_enable")
    private Integer kycEnable;
    /**
     * 加密设置
     */
    @TableField("crypto_settings")
    private String cryptoSettings;
    /**
     * 通道（1:单通道，2：双通道）
     */
    private Integer way;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTerminalNo() {
		return terminalNo;
	}

	public void setTerminalNo(String terminalNo) {
		this.terminalNo = terminalNo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getHotline() {
		return hotline;
	}

	public void setHotline(String hotline) {
		this.hotline = hotline;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBuyTransactionFee() {
		return buyTransactionFee;
	}

	public void setBuyTransactionFee(String buyTransactionFee) {
		this.buyTransactionFee = buyTransactionFee;
	}

	public String getBuySingleFee() {
		return buySingleFee;
	}

	public void setBuySingleFee(String buySingleFee) {
		this.buySingleFee = buySingleFee;
	}

	public String getSellTransactionFee() {
		return sellTransactionFee;
	}

	public void setSellTransactionFee(String sellTransactionFee) {
		this.sellTransactionFee = sellTransactionFee;
	}

	public String getSellSingleFee() {
		return sellSingleFee;
	}

	public void setSellSingleFee(String sellSingleFee) {
		this.sellSingleFee = sellSingleFee;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getMinNeedBitcoin() {
		return minNeedBitcoin;
	}

	public void setMinNeedBitcoin(String minNeedBitcoin) {
		this.minNeedBitcoin = minNeedBitcoin;
	}

	public String getMinNeedCash() {
		return minNeedCash;
	}

	public void setMinNeedCash(String minNeedCash) {
		this.minNeedCash = minNeedCash;
	}

	public String getChannelParam() {
		return channelParam;
	}

	public void setChannelParam(String channelParam) {
		this.channelParam = channelParam;
	}

	public int getIsSend() {
		return isSend;
	}

	public void setIsSend(int isSend) {
		this.isSend = isSend;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public Integer getHotWallet() {
		return hotWallet;
	}

	public void setHotWallet(Integer hotWallet) {
		this.hotWallet = hotWallet;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Integer getSellType() {
		return sellType;
	}

	public void setSellType(Integer sellType) {
		this.sellType = sellType;
	}

	public Integer getExchange() {
		return exchange;
	}

	public void setExchange(Integer exchange) {
		this.exchange = exchange;
	}

	public String getLimitCash() {
		return limitCash;
	}

	public void setLimitCash(String limitCash) {
		this.limitCash = limitCash;
	}

	public String getKycUrl() {
		return kycUrl;
	}

	public void setKycUrl(String kycUrl) {
		this.kycUrl = kycUrl;
	}

	public Integer getExchangeStrategy() {
		return exchangeStrategy;
	}

	public void setExchangeStrategy(Integer exchangeStrategy) {
		this.exchangeStrategy = exchangeStrategy;
	}

	public Integer getRateSource() {
		return rateSource;
	}

	public void setRateSource(Integer rateSource) {
		this.rateSource = rateSource;
	}

	public Integer getKycEnable() {
		return kycEnable;
	}

	public void setKycEnable(Integer kycEnable) {
		this.kycEnable = kycEnable;
	}

	public String getCryptoSettings() {
		return cryptoSettings;
	}

	public void setCryptoSettings(String cryptoSettings) {
		this.cryptoSettings = cryptoSettings;
	}

	public Integer getWay() {
		return way;
	}

	public void setWay(Integer way) {
		this.way = way;
	}

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
