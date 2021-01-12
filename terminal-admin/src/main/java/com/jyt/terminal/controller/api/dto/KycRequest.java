package com.jyt.terminal.controller.api.dto;

import com.jyt.terminal.commom.BaseRequest;


public class KycRequest extends BaseRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * kyc上传的id
     */
    private String kycId;

	public String getKycId() {
		return kycId;
	}

	public void setKycId(String kycId) {
		this.kycId = kycId;
	}
	    
}
