package com.jyt.terminal.controller.api.dto;

import com.jyt.terminal.commom.BaseResponse;
import com.jyt.terminal.commom.enums.ServiceExceptionEnum;

public class DownFileResponse extends BaseResponse{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	private String fileStr;

	public DownFileResponse(ServiceExceptionEnum serviceExceptionEnum,String fileStr) {
    	this.setCode(serviceExceptionEnum.getCode());
    	this.setMessage(serviceExceptionEnum.getMessage());
    	this.setFileStr(fileStr);
    }
	
	public String getFileStr() {
		return fileStr;
	}

	public void setFileStr(String fileStr) {
		this.fileStr = fileStr;
	}
	
	
	
}
