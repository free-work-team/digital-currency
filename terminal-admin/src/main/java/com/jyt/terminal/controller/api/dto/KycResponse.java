package com.jyt.terminal.controller.api.dto;

import com.jyt.terminal.commom.BaseResponse;
import com.jyt.terminal.commom.enums.ServiceExceptionEnum;

/**
 * 查询kyc返回验证码
 * @className KycResponse
 * @author wangwei
 * @date 2019年9月11日
 *
 */
public class KycResponse extends BaseResponse{
 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
   

	/**
     * 验证码
     */
    private String verificationCode;

    
    public KycResponse(ServiceExceptionEnum serviceExceptionEnum,String verificationCode) {
    	this.setCode(serviceExceptionEnum.getCode());
    	this.setMessage(serviceExceptionEnum.getMessage());
    	this.setVerificationCode(verificationCode);
    }


	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

}
