package com.jyt.hardware.cashoutmoudle.bean;


public class WithdrawLog {

	private Integer id ;

	private String transId ;

	private String channelTransId ;

	private String terminalNo;
	
	private String targetAddress;
	
	private String amount;

	private String customerId;

	private String currency;

	private String fee;
	
	private String cFee;
	
	private String extId; //渠道额外需要存的信息，json
	
	private String transTime;
	
	private Integer transStatus;

	private Integer confirmStatus;//0 1

	private Integer redeemStatus;
	
	private String remark;
	
	private String cash;
	
	private Integer outCount;
	
	private Integer isUpload;

	private String  channel;

	private String  sellType;

	private String  price;

	private String exchangeRate;

	private String  strategy;

	private String  redeemTime;

	/**
	 *  0.不使用交易所，从热钱包发送和接收币；
	 *  1.热钱包预存币，从热钱包发送和接收币，然后在交易所买卖币，如果购买成功，把这些币发送到热钱包；
	 *  2.热钱包不预存币，直接在交易所买卖币。
	 */
	private Integer exchangeStrategy;

	private String cryptoCurrency;//加密币种

	private String addressId;//



	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public void setRedeemTime(String redeemTime) {
		this.redeemTime = redeemTime;
	}

	public String getRedeemTime() {
		return redeemTime;
	}

	public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setSellType(String sellType) {
		this.sellType = sellType;
	}

	public String getSellType() {
		return sellType;
	}

	public Integer getConfirmStatus() {
		return confirmStatus;
	}

	public void setConfirmStatus(Integer confirmStatus) {
		this.confirmStatus = confirmStatus;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getChannel() {
		return channel;
	}

	public Integer getIsUpload() {
		return isUpload;
	}

	public void setIsUpload(Integer isUpload) {
		this.isUpload = isUpload;
	}
	public Integer getOutCount() {
		return outCount;
	}

	public void setOutCount(Integer outCount) {
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

	public String getcFee() {
		return cFee;
	}

	public void setcFee(String cFee) {
		this.cFee = cFee;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public String getExtId() {
		return extId;
	}

	public String getCash() {
		return cash;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setCash(String cash) {
		this.cash = cash;
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

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public Integer getExchangeStrategy() {
		return exchangeStrategy;
	}

	public void setExchangeStrategy(Integer exchangeStrategy) {
		this.exchangeStrategy = exchangeStrategy;
	}

	public String getChannelTransId() {
		return channelTransId;
	}

	public void setChannelTransId(String channelTransId) {
		this.channelTransId = channelTransId;
	}

	public String getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getCryptoCurrency() {
		return cryptoCurrency;
	}

	public void setCryptoCurrency(String cryptoCurrency) {
		this.cryptoCurrency = cryptoCurrency;
	}


	public String getAddressId() {
		return addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}
}
