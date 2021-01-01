package com.jyt.hardware.utils;


public class ByteUtilException extends Exception {

	private static final long serialVersionUID = -229660132589990139L;
	
	private String message;

	public ByteUtilException(String message){
		super(message);
		this.message = message;
	}
	
	
	public ByteUtilException(String message, Throwable t){
		super(message,t);
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
}
