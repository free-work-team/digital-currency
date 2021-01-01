package com.jyt.hardware.cashoutmoudle.bean;


public class CMDLastDispenseReslut extends CommonBaseReslut {

	public CMDLastDispenseReslut(boolean flat) {
		super(flat);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 上次出钞是否掉电   0未掉电1掉电
	 */
	private int isPowerOff;
	/**
	 * 总出钞张数
	 */
	private int totalOutNotes;
	/**
	 * 总吸钞张数
	 */
	private int totalFedNotes;
	/**
	 * 总回收张数
	 */
	private int totalRejectedNotes;



	public int getTotalOutNotes() {
		return totalOutNotes;
	}

	public void setTotalOutNotes(int totalOutNotes) {
		this.totalOutNotes = totalOutNotes;
	}

	public int getTotalFedNotes() {
		return totalFedNotes;
	}

	public void setTotalFedNotes(int totalFedNotes) {
		this.totalFedNotes = totalFedNotes;
	}

	public int getTotalRejectedNotes() {
		return totalRejectedNotes;
	}

	public void setTotalRejectedNotes(int totalRejectedNotes) {
		this.totalRejectedNotes = totalRejectedNotes;
	}

	public int getIsPowerOff() {
		return isPowerOff;
	}

	public void setIsPowerOff(int isPowerOff) {
		this.isPowerOff = isPowerOff;
	}
	
}
