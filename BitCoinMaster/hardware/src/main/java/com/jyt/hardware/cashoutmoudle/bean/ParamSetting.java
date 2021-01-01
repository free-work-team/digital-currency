package com.jyt.hardware.cashoutmoudle.bean;

public class ParamSetting {
	
	private String id;
	/** 联网模式 **/
	private String webAddress;

	private String terminalNo;

	private String password;
	/************/

	/** 公共参数 **/
	private String merchantName;
	
	private String hotline;
	
	private String email;

	private Integer kycEnable;

	private String limitCash;

	private String kycUrl;

	private Integer rateSource;
	/************/

	private String online;

	private Crypto cryptoSettings;

	private String advertContent; //广告

	private Integer way; // 单双向

	/** 挪到cyptoSettings里 **/
//	private String buyTransactionFee;
//	private String sellTransactionFee;
//	private String buySingleFee;
//	private String sellSingleFee;
//	private String minNeedCash;
//	private String rate;
//	private String sellType;
//	private String channel;
//	private Integer hotWallet;
	//订单 交易对
//	private String currencyPair;
	// 订单下单类型 limit market
//	private String orderType;
	//bitgo参数
//	private String accessToken;
//	private String walletId;
//	private String walletPassphrase;
	//coinbase参数
//	private String  apiKey;
//	private String apiSecret;
	//blockchain参数
//	private String  bcWalletId;
//	private String  bcPassword;
	//pro参数
//	private String proKey;
//	private String proSecret;
//	private String proPassphrase;
	// kraken 参数
//	private String krakenApiKey;
//	private String krakenSecret;
//	private String krakenWithdrawalsAddressName;
	/**
	 *  0.不使用交易所，从热钱包发送和接收币；
	 *  1.热钱包预存币，从热钱包发送和接收币，然后在交易所买卖币，如果购买成功，把这些币发送到热钱包；
	 *  2.热钱包不预存币，直接在交易所买卖币。
	 */
//	private Integer exchangeStrategy;
	/************/

	/** 已废弃 **/
//	private String minNeedBitcoin;
//	private String currency;
//	private String channelParam;


	public Integer getWay() {
		return way;
	}

	public void setWay(Integer way) {
		this.way = way;
	}

	/************/


	public void setKycUrl(String kycUrl) {
		this.kycUrl = kycUrl;
	}

	public String getKycUrl() {
		return kycUrl;
	}

	public void setLimitCash(String limitCash) {
		this.limitCash = limitCash;
	}

	public String getLimitCash() {
		return limitCash;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setWebAddress(String webAddress) {
		this.webAddress = webAddress;
	}

	public String getWebAddress() {
		return webAddress;
	}



	public void setOnline(String online) {
		this.online = online;
	}

	public String getOnline() {
		return online;
	}



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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


	public String getTerminalNo() {
		return terminalNo;
	}

	public void setTerminalNo(String terminalNo) {
		this.terminalNo = terminalNo;
	}
	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
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


	public Crypto getCryptoSettings() {
		return cryptoSettings;
	}

	public void setCryptoSettings(Crypto cryptoSettings) {
		this.cryptoSettings = cryptoSettings;
	}

	public String getAdvertContent() {
		return advertContent;
	}

	public void setAdvertContent(String advertContent) {
		this.advertContent = advertContent;
	}
}
