/**
 * Copyright (c) 2021, All Rights Reserved.
 *
*/
package com.jyt.terminal.controller.api.dto;

import com.jyt.terminal.commom.BaseResponse;
import com.jyt.terminal.commom.enums.ServiceExceptionEnum;

/**
 * 此处应有类说明<br/>
 *
 * @author tangfq
 * @Date 2021年1月2日 下午8:26:50
 * @since jdk 1.8
 *  
 */
public class SmsResponse extends BaseResponse{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 验证码
     */
    private String verificationCode;

    
    public SmsResponse(ServiceExceptionEnum serviceExceptionEnum,String verificationCode) {
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
