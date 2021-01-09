package com.jyt.terminal.controller.api.dto;

import java.io.Serializable;

/**
 * 加密设置响应结果
 * @author 王伟
 */
public class CryptoSettingsResponse implements Serializable{

    private static final long serialVersionUID = 1L;
	/**
	 * 虚拟币种
	 */
	private Integer virtualCurrency;
	/**
	 * 状态
	 */
	private Integer status;
	/**
	 * 渠道参数参数
	 */
	private String channelParam;
	/**
	 * 交易策略
	 */
	private Integer exchangeStrategy;
	/**
	 * 交易所
	 */
	private Integer exchange;
	/**
	 * 钱包
	 */
	private Integer hotWallet;
	/**
	 * 确认方式
	 */
	private Integer confirmations;
	/**
	 * 币汇率来源
	 */
	private Integer rateSource;
	/**
	 * 比特币汇率
	 */
	private String price;
	/**
	 * 购买手续费（%）
	 */
	private String buyTransactionFee;
	/**
	 * 购买单笔费用
	 */
	private String buySingleFee;
	/**
	 * 最低钱包余额
	 */
	private String minNeedCash;
	/**
	 * 出售手续费（%）
	 */
	private String sellTransactionFee;
	/**
	 * 出售单笔费用
	 */
	private String sellSingleFee;
	public Integer getVirtualCurrency() {
		return virtualCurrency;
	}
	public void setVirtualCurrency(Integer virtualCurrency) {
		this.virtualCurrency = virtualCurrency;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getChannelParam() {
		return channelParam;
	}
	public void setChannelParam(String channelParam) {
		this.channelParam = channelParam;
	}
	public Integer getExchangeStrategy() {
		return exchangeStrategy;
	}
	public void setExchangeStrategy(Integer exchangeStrategy) {
		this.exchangeStrategy = exchangeStrategy;
	}
	public Integer getExchange() {
		return exchange;
	}
	public void setExchange(Integer exchange) {
		this.exchange = exchange;
	}
	public Integer getHotWallet() {
		return hotWallet;
	}
	public void setHotWallet(Integer hotWallet) {
		this.hotWallet = hotWallet;
	}
	public Integer getConfirmations() {
		return confirmations;
	}
	public void setConfirmations(Integer confirmations) {
		this.confirmations = confirmations;
	}
	public Integer getRateSource() {
		return rateSource;
	}
	public void setRateSource(Integer rateSource) {
		this.rateSource = rateSource;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
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
	public String getMinNeedCash() {
		return minNeedCash;
	}
	public void setMinNeedCash(String minNeedCash) {
		this.minNeedCash = minNeedCash;
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
	
}
