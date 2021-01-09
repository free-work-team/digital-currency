package com.jyt.terminal.controller.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.jyt.terminal.commom.BaseRequest;
import com.jyt.terminal.model.Buy;
import com.jyt.terminal.model.Device;
import com.jyt.terminal.model.Withdraw;

/**
 * 
 * @author zengcong
 * @date 2018年10月18日
 */
public class UploadDeviceInfoRequest extends BaseRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4919871427818889869L;
	
	private List<Device> deviceList = new ArrayList<>();

	public List<Device> getDeviceList() {
		return deviceList;
	}

	public void setDeviceList(List<Device> deviceList) {
		this.deviceList = deviceList;
	}

	


}
