package com.jyt.terminal.dto;


public class SMSAuthInDTO extends BaseObject{
	 /**
     * 商户id，非必传字段
     */
    private String merchantId;
    
	 /**
     * 订单号。必须保证整个系统唯一
     */
    private String orderNo;
    
    /**
	 * 手机号
	 */
	private String mobile;
    
    /**
	 * 短信内容。注意，短信验证码请用${smsCode}代替。该字段会自动进行替换。
	 * @see com.huirongunion.hru.common.constant.HRUConstant.AUTH_INFO_PLACEHOLDER
	 */
	private String smsContent;
    
    /**
     * 有效时间（单位：分钟）
     */
    private Integer maxValidTime = 15;
    
    /**
     * 最大认证次数
     */
    private Integer maxAuthTime = 3;

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getSmsContent() {
		return smsContent;
	}

	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}

	public Integer getMaxValidTime() {
		return maxValidTime;
	}

	public void setMaxValidTime(Integer maxValidTime) {
		this.maxValidTime = maxValidTime;
	}

	public Integer getMaxAuthTime() {
		return maxAuthTime;
	}

	public void setMaxAuthTime(Integer maxAuthTime) {
		this.maxAuthTime = maxAuthTime;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
 
}
