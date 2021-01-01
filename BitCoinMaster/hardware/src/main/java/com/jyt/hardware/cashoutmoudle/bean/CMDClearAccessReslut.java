package com.jyt.hardware.cashoutmoudle.bean;


public class CMDClearAccessReslut extends CommonBaseReslut{

	public CMDClearAccessReslut(boolean flat) {
		super(flat);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 送出的钞票
	 */
	private int sendCash;
	/**
	 * 回收的钞票
	 */
	private int returnCash;

	public int getSendCash() {
		return sendCash;
	}
	public void setSendCash(int sendCash) {
		this.sendCash = sendCash;
	}
	public int getReturnCash() {
		return returnCash;
	}
	public void setReturnCash(int returnCash) {
		this.returnCash = returnCash;
	}
	
	
}
