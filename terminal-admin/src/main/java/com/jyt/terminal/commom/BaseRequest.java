package com.jyt.terminal.commom;

import java.io.Serializable;


/**
 * 请求基类
 * @author zengcong
 * @date 2019年4月29日
 */
public class BaseRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7891855144063370582L;

	/**
     * 终端号（用户名）
     */
    private String termNo;

	public String getTermNo() {
		return termNo;
	}

	public void setTermNo(String termNo) {
		this.termNo = termNo;
	}
    

	

	
    

}
