/**
 * 
 */
package com.jyt.terminal.dto;


/**
 * 短信发送输入参数
 * 
 * @author 李春龙
 */
public class SmsSendInDTO extends BaseObject {
	/**
     * 终端id
     */
    private String terminalId;
    
	 /**
     * 商户id
     */
    private String merchantId;
    
	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 手机号
	 */
	private String mobile;

	/**
	 * 短信内容
	 */
	private String smsContent;

	/**
	 * 短信验证编号
	 */
	private Long smsAuthId;

	
	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSmsContent() {
		return smsContent;
	}

	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}

	public Long getSmsAuthId() {
		return smsAuthId;
	}

	public void setSmsAuthId(Long smsAuthId) {
		this.smsAuthId = smsAuthId;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

}
