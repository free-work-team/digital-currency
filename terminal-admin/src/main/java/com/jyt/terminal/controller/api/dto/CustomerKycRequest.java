package com.jyt.terminal.controller.api.dto;

import com.jyt.terminal.commom.BaseRequest;

public class CustomerKycRequest extends BaseRequest{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 客户邮箱
	 */
	private String email;
	
	/**
	 * 是否超过限额
	 */
	private boolean isExceedQuota;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isExceedQuota() {
		return isExceedQuota;
	}
	public void setExceedQuota(boolean isExceedQuota) {
		this.isExceedQuota = isExceedQuota;
	}
	

}
