package com.jyt.terminal.controller.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.jyt.terminal.commom.BaseRequest;
import com.jyt.terminal.model.Order;

/**
 * 
 * @author zengcong
 * @date 2018年10月18日
 */
public class UploadOrderRecordRequest extends BaseRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4919871427818889869L;
	
	private List<Order> orderList = new ArrayList<>();

	public List<Order> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<Order> orderList) {
		this.orderList = orderList;
	}

	
	
	

}
