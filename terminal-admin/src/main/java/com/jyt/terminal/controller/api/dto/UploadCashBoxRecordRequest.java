package com.jyt.terminal.controller.api.dto;

import com.jyt.terminal.commom.BaseRequest;
import com.jyt.terminal.model.CashBox;

/**
 * @className UploadCashBoxRecordRequest
 * @author wangwei
 * @date 2019年11月8日
 *
 */
public class UploadCashBoxRecordRequest extends BaseRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4919871427818889869L;
	
	private CashBox cashBox;

	public CashBox getCashBox() {
		return cashBox;
	}

	public void setCashBox(CashBox cashBox) {
		this.cashBox = cashBox;
	}

}
