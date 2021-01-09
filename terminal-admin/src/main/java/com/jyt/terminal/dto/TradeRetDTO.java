/**
 * 
 */
package com.jyt.terminal.dto;

import com.jyt.terminal.commom.enums.TradeRetCodeEnum;

/**
 * 交易返回DTO
 * 
 * @author 李春龙
 */
public class TradeRetDTO extends BaseObject{

	/**
	 * 交易状态.1:成功， 2:失败， 3:处理中/未知
	 */
	private int tradeStatus;

	/**
	 * 返回码
	 */
	private Integer retCode;

	/**
	 * 返回信息
	 */
	private String retInfo;

	/**
	 * 交易编号
	 */
	private Long tradeNo;
	
	/**
	 * 附加信息
	 */
	private String extraInfo;

	public TradeRetDTO() {

	}

	public TradeRetDTO(int tradeStatus, long tradeNo) {
		this.tradeStatus = tradeStatus;
		this.tradeNo = tradeNo;
	}

	public TradeRetDTO(int tradeStatus, Integer retCode, String retInfo) {
		this.tradeStatus = tradeStatus;
		this.retCode = retCode;
		this.retInfo = retInfo;
	}
	
	public TradeRetDTO(int tradeStatus, TradeRetCodeEnum tradeRetCodeEnum) {
		this.tradeStatus = tradeStatus;
		this.retCode = tradeRetCodeEnum.getCode();
		this.retInfo = tradeRetCodeEnum.getMessage();
	}

	public TradeRetDTO(int tradeStatus, Integer retCode, String retInfo, long tradeNo) {
		this.tradeStatus = tradeStatus;
		this.retCode = retCode;
		this.retInfo = retInfo;
		this.tradeNo = tradeNo;
	}
	
	public void setBasic(int tradeStatus, Integer retCode, String retInfo) {
		setTradeStatus(tradeStatus);
		setRetCode(retCode);
		setRetInfo(retInfo);
	}

	public int getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(int tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public Integer getRetCode() {
		return retCode;
	}

	public void setRetCode(Integer retCode) {
		this.retCode = retCode;
	}

	public String getRetInfo() {
		return retInfo;
	}

	public void setRetInfo(String retInfo) {
		this.retInfo = retInfo;
	}

	public Long getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(Long tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

}
