package com.jyt.terminal.dto;


import java.io.Serializable;
import com.jyt.terminal.commom.enums.ServiceExceptionEnum;


public class Response implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8199695705932920985L;
	
	/**
	 * 应答码
	 */
	 private Integer code;

	 /**
	  * 描述
	  */
	 private String message;
	 
	 
	 public Response() {
	 }
	 
	
	 public Response(Integer code,String message) {
		 this.code = code;
		 this.message = message;
	 }
	 
	 public Response(ServiceExceptionEnum serviceExceptionEnum) {
	        this.code = serviceExceptionEnum.getCode();
	        this.message = serviceExceptionEnum.getMessage();
	    }

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
