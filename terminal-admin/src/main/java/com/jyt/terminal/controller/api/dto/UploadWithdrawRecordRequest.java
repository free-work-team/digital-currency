package com.jyt.terminal.controller.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.jyt.terminal.commom.BaseRequest;
import com.jyt.terminal.model.Buy;
import com.jyt.terminal.model.Withdraw;

/**
 * 
 * @author zengcong
 * @date 2018年10月18日
 */
public class UploadWithdrawRecordRequest extends BaseRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4919871427818889869L;
	
	private List<Withdraw> withdrawList = new ArrayList<>();

	public List<Withdraw> getWithdrawList() {
		return withdrawList;
	}

	public void setWithdrawList(List<Withdraw> withdrawList) {
		this.withdrawList = withdrawList;
	}


}
