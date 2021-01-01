package com.jyt.hardware.cashoutmoudle.bean;


public class CMDMoneyTWReslut extends CommonBaseReslut {

	public CMDMoneyTWReslut(boolean flat) {
		super(flat);
		// TODO Auto-generated constructor stub
	}
	public int getIsTakenaway() {
		return isTakenaway;
	}
	public void setIsTakenaway(int isTakenaway) {
		this.isTakenaway = isTakenaway;
	}
	/**
	 * 钱是否被拿走 0拿走 1没有 -1失败未知
	 */
	private int isTakenaway;

}
