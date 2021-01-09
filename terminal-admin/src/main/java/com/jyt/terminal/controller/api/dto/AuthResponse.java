package com.jyt.terminal.controller.api.dto;

import com.jyt.terminal.commom.BaseResponse;
import com.jyt.terminal.commom.enums.ServiceExceptionEnum;

/**
 * 认证的响应结果
 *
 * @author zengcong
 * @Date 2018/12/24 13:58
 */
public class AuthResponse extends BaseResponse{
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 3267242256127002368L;

	/**
     * jwt token
     */
    private final String token;

    /**
     * 用于客户端混淆md5加密
     */
    private final String randomKey;
      

    
    public AuthResponse(ServiceExceptionEnum serviceExceptionEnum,String token, String randomKey) {
    	this.setCode(serviceExceptionEnum.getCode());
    	this.setMessage(serviceExceptionEnum.getMessage());
        this.token = token;
        this.randomKey = randomKey;   
    }

    public String getToken() {
        return this.token;
    }

    public String getRandomKey() {
        return randomKey;
    }

	
}
