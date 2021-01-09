package com.jyt.terminal.controller.api.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.jyt.terminal.commom.BaseResponse;
import com.jyt.terminal.commom.enums.BitEnum.VirtualCurrencyEnum;
import com.jyt.terminal.commom.enums.ServiceExceptionEnum;
import com.jyt.terminal.model.Advert;
import com.jyt.terminal.model.CryptoSettings;
import com.jyt.terminal.model.TerminalSetting;
import com.jyt.terminal.util.ToolUtil;

/**
 * 查询终端机设置参数的响应结果
 *
 * @author zengcong
 * @Date 2018/12/24 13:58
 */
public class TerminalSettingResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3267242256127002368L;
	/**
	 * 终端号
	 */
	private String terminalNo;
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
	 * 是否开启kyc(1:是，2:否)
	 */
	private Integer kycEnable;
	/**
	 * 限制金额
	 */
	private String limitCash;
	/**
	 * 信息提交链接
	 */
	private String kycUrl;
	/**
	 * 加密设置参数
	 */
	private String cryptoSettings;
	/**
	 * 广告标题
	 */
	private String advertTitle;
	/**
	 * 广告内容
	 */
	private String advertContent;

	private boolean encry;
	/**
	 * 最低比特币余额
	 */
	private String minNeedBitcoin;
	/**
	 * 币种
	 */
	private String currency;
	/**
	 * (1:单向，2：双向)
	 */
	private Integer way;

	public TerminalSettingResponse(ServiceExceptionEnum serviceExceptionEnum, TerminalSetting setting,
			List<CryptoSettings> cryptoSettingsList, Advert advert) {
		this.setCode(serviceExceptionEnum.getCode());
		this.setMessage(serviceExceptionEnum.getMessage());
		// 终端参数
		this.setTerminalNo(setting.getTerminalNo());
		this.setMerchantName(setting.getMerchantName());
		this.setEmail(setting.getEmail());
		this.setHotline(setting.getHotline());
		this.setKycEnable(setting.getKycEnable());
		this.setKycUrl(setting.getKycUrl());
		this.setLimitCash(setting.getLimitCash());
		this.setWay(setting.getWay());
		// 加密设置参数
		if (ToolUtil.isNotEmpty(setting.getCryptoSettings())) {
			List<CryptoSettingsResponse> csrList = new ArrayList<>();
			for (CryptoSettings cs : cryptoSettingsList) {
				CryptoSettingsResponse rsp = new CryptoSettingsResponse();
				rsp.setVirtualCurrency(cs.getVirtualCurrency());
				rsp.setStatus(cs.getStatus());
				rsp.setChannelParam(cs.getChannelParam());
				rsp.setExchangeStrategy(cs.getExchangeStrategy());
				rsp.setExchange(cs.getExchange());
				rsp.setHotWallet(cs.getHotWallet());
				rsp.setConfirmations(cs.getConfirmations());
				rsp.setRateSource(cs.getRateSource());
				if(ToolUtil.isEmpty(cs.getPrice())) {
					rsp.setPrice(null);
				}else {
					rsp.setPrice(cs.getPrice().toString());
				}
				rsp.setBuyTransactionFee(cs.getBuyTransactionFee().toString());
				rsp.setBuySingleFee(cs.getBuySingleFee().toString());
				rsp.setMinNeedCash(cs.getMinNeedCash().toString());
				rsp.setSellTransactionFee(cs.getSellTransactionFee().toString());
				rsp.setSellSingleFee(cs.getSellSingleFee().toString());
				csrList.add(rsp);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			for (CryptoSettingsResponse csr : csrList) {
				for (VirtualCurrencyEnum param : VirtualCurrencyEnum.values()) {
					if (csr.getVirtualCurrency() == param.getValue()) {
						map.put(param.getEndesc(), csr);
					}
				}
			}
			String cryptoSettingsStr = JSON.toJSONString(map);
			this.setCryptoSettings(cryptoSettingsStr);
		} else {
			CryptoSettingsResponse rsp = new CryptoSettingsResponse();
			rsp.setVirtualCurrency(VirtualCurrencyEnum.BTC.getValue());
			rsp.setStatus(setting.getStatus());
			rsp.setChannelParam(setting.getChannelParam());
			rsp.setExchangeStrategy(setting.getExchangeStrategy());
			rsp.setExchange(setting.getExchange());
			rsp.setHotWallet(setting.getHotWallet());
			rsp.setConfirmations(setting.getSellType());
			rsp.setRateSource(setting.getRateSource());
			rsp.setPrice(setting.getRate());
			rsp.setBuyTransactionFee(setting.getBuyTransactionFee());
			rsp.setBuySingleFee(setting.getBuySingleFee());
			rsp.setMinNeedCash(setting.getMinNeedCash());
			rsp.setSellTransactionFee(setting.getSellTransactionFee());
			rsp.setSellSingleFee(setting.getSellSingleFee());
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("btc", rsp);
			this.setCryptoSettings(JSON.toJSONString(map));
		}
		// 广告参数
		if (advert != null) {
			this.setAdvertTitle(advert.getAdvertTitle());
			this.setAdvertContent(advert.getAdvertContent());
		}
		this.setEncry(true);
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

	public Integer getKycEnable() {
		return kycEnable;
	}

	public void setKycEnable(Integer kycEnable) {
		this.kycEnable = kycEnable;
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

	public String getCryptoSettings() {
		return cryptoSettings;
	}

	public void setCryptoSettings(String cryptoSettings) {
		this.cryptoSettings = cryptoSettings;
	}

	public String getAdvertTitle() {
		return advertTitle;
	}

	public void setAdvertTitle(String advertTitle) {
		this.advertTitle = advertTitle;
	}

	public String getAdvertContent() {
		return advertContent;
	}

	public void setAdvertContent(String advertContent) {
		this.advertContent = advertContent;
	}

	public boolean isEncry() {
		return encry;
	}

	public void setEncry(boolean encry) {
		this.encry = encry;
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

	public Integer getWay() {
		return way;
	}

	public void setWay(Integer way) {
		this.way = way;
	}

}
