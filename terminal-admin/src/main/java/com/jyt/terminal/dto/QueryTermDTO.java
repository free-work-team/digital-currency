package com.jyt.terminal.dto;

import com.jyt.terminal.model.TerminalSetting;

public class QueryTermDTO extends TerminalSetting{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String beginTime;
	private String endTime;
	public String getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	
}
