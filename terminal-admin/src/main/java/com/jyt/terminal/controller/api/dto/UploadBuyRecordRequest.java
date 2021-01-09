package com.jyt.terminal.controller.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.jyt.terminal.commom.BaseRequest;
import com.jyt.terminal.model.Buy;

/**
 * 
 * @author zengcong
 * @date 2018年10月18日
 */
public class UploadBuyRecordRequest extends BaseRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4919871427818889869L;
	
	private List<Buy> buyList = new ArrayList<>();

	public List<Buy> getBuyList() {
		return buyList;
	}

	public void setBuyList(List<Buy> buyList) {
		this.buyList = buyList;
	}
	
	

}
