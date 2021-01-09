package com.jyt.terminal.commom.enums;


/**
 * 
 * @author      tangfq
 * @description 
 * @date        2020年11月24日 下午11:58:22
 * @version     V1.0
 */
public class StripeNumber implements ServiceExceptionEnum{

	public static String apiKey;
	
	StripeNumber(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private Integer code;

    private String message;

    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
