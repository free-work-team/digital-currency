package com.jyt.hardware.cashoutmoudle.bean;


public class CMDLedReslut extends  CommonBaseReslut{

	public CMDLedReslut(boolean flat) {
		super(flat);
		// TODO Auto-generated constructor stub
	}
	/**
	 * LED下标id
	 */
	private int ledId;
	/**
	 * LED状态
	 */
	private int ledStatus;

	public int getLedId() {
		return ledId;
	}
	public void setLedId(int ledId) {
		this.ledId = ledId;
	}
	public int getLedStatus() {
		return ledStatus;
	}
	public void setLedStatus(int ledStatus) {
		this.ledStatus = ledStatus;
	}
	
}
