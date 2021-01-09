package com.jyt.terminal.controller.api.dto;

import java.io.Serializable;


/**
 * 认证的请求dto
 *
 * @author fengshuonan
 * @Date 2017/8/24 14:00
 */
public class AuthRequest implements Serializable {
   
	/**
	 * 
	 */
	private static final long serialVersionUID = 6441802596342413921L;

	/**
     * 用户名
     */
    private String userName;
    
    /**
     * 密码
     */
    private String password;
    

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

}
