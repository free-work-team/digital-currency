/**
 * Copyright (c) 2021, All Rights Reserved.
 *
*/
package com.jyt.terminal.controller.api.dto;

import com.jyt.terminal.commom.BaseRequest;

/**
 * <br/>
 *
 * @author tangfq
 * @Date 2021年1月2日 下午4:37:32
 * @since jdk 1.8
 *  
 */
public class SmsRequest extends BaseRequest{

	private static final long serialVersionUID = 1L;


	/**
	 * 手机号
	 */
	private String phone;

	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
		
	
}
