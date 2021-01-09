package com.jyt.terminal.controller.api.dto;

import com.jyt.terminal.commom.BaseRequest;


/**
 * 
 *
 * @author fengshuonan
 * @Date 2017/8/24 14:00
 */
public class AddTermRequest extends BaseRequest {
   
	/**
	 * 
	 */
	private static final long serialVersionUID = 6441802596342413921L;

    /**
     * 密码
     */
    private String password;
    
    /**
     * 商户名
     */
    private String merchantName;
    
    /**
     * 电话热线
     */
    private String hotline;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 购买手续费
     */
    private String buyTransactionFee;
    /**
     * 购买手续费(单笔)
     */
    private String buySingleFee;
    /**
     * 出卖手续费
     */
    private String sellTransactionFee;
    /**
     * 出售手续费(单笔)
     */
    private String sellSingleFee;
    /**
     * 
     */
    private String rate;
    /**
     * 
     */
    private String minNeedBitcoin;
    /**
     * 
     */
    private String minNeedCash;
    /**
     * token
     */
    private String channelParam;
    
    /**
     * 渠道类型
     */
    private Integer channelType;
    
    /**
     * 币种
     */
    private String currency;
    /**
     * 交易方式
     */
    private Integer sellType;
    /**
     * 是否热钱包（0：非，1：是）
     */
    private Integer hotWallet;
    
    /**
     *  限制金额
     */
    private String limitCash;
    /**
     * 
     */
    private String kycUrl;
    
    
    
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

	public String getMinNeedCash() {
		return minNeedCash;
	}

	public void setMinNeedCash(String minNeedCash) {
		this.minNeedCash = minNeedCash;
	}

	public String getBuySingleFee() {
		return buySingleFee;
	}

	public void setBuySingleFee(String buySingleFee) {
		this.buySingleFee = buySingleFee;
	}

	public String getSellSingleFee() {
		return sellSingleFee;
	}

	public void setSellSingleFee(String sellSingleFee) {
		this.sellSingleFee = sellSingleFee;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getSellTransactionFee() {
		return sellTransactionFee;
	}

	public void setSellTransactionFee(String sellTransactionFee) {
		this.sellTransactionFee = sellTransactionFee;
	}

	public String getChannelParam() {
		return channelParam;
	}

	public void setChannelParam(String channelParam) {
		this.channelParam = channelParam;
	}

	public Integer getChannelType() {
		return channelType;
	}

	public void setChannelType(Integer channelType) {
		this.channelType = channelType;
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

	public Integer getHotWallet() {
		return hotWallet;
	}

	public void setHotWallet(Integer hotWallet) {
		this.hotWallet = hotWallet;
	}
	
}
