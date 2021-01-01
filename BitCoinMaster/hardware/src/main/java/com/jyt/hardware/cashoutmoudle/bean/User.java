package com.jyt.hardware.cashoutmoudle.bean;

public class User {
	
	private String account;
	
	private String password;
	
	private String name;
	
	private String terminalNo;
	
	private String merchantName;
	
	private String hotline;
	
	private String email;
	
	private String buyTransactionFee;
	
	private String sellTransactionFee;
	
	private String accessToken;
	
	private String walletId;
	
	private String walletPassphrase;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getWalletId() {
		return walletId;
	}

	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}

	public String getWalletPassphrase() {
		return walletPassphrase;
	}

	public void setWalletPassphrase(String walletPassphrase) {
		this.walletPassphrase = walletPassphrase;
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
	
}
