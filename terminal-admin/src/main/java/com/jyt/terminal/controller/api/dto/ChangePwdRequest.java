package com.jyt.terminal.controller.api.dto;

import com.jyt.terminal.commom.BaseRequest;

/**
 * 修改密码请求参数
 * @author zengcong
 * @date 2018年10月18日
 */
public class ChangePwdRequest extends BaseRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4919871427818889869L;
	
	   
    /**
     * 旧密码
     */
    private String oldPwd;

    /**
     * 新密码
     */
    private String newPwd;
    
    
	public String getOldPwd() {
		return oldPwd;
	}

	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
	}

	public String getNewPwd() {
		return newPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}


}
