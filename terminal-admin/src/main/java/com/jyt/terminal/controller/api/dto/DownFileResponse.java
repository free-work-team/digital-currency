package com.jyt.terminal.controller.api.dto;

import com.jyt.terminal.commom.BaseResponse;
import com.jyt.terminal.commom.enums.ServiceExceptionEnum;

public class DownFileResponse extends BaseResponse{

	
	private static final long serialVersionUID = 1L;
	/**
	 * 文件内容
	 */
	private String fileStr;	
	/**
	 * ocr图片内容
	 */
	private String ocrContent;
	/**
	 * 证件类型
	 */
	private String cardType;

	public DownFileResponse(ServiceExceptionEnum serviceExceptionEnum,String fileStr,String ocrContent,String cardType) {
    	this.setCode(serviceExceptionEnum.getCode());
    	this.setMessage(serviceExceptionEnum.getMessage());
    	this.setFileStr(fileStr);
    	this.setOcrContent(ocrContent);
    	this.setCardType(cardType);
    }
	
	public String getFileStr() {
		return fileStr;
	}

	public void setFileStr(String fileStr) {
		this.fileStr = fileStr;
	}

	public String getOcrContent() {
		return ocrContent;
	}

	public void setOcrContent(String ocrContent) {
		this.ocrContent = ocrContent;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	
	
}
