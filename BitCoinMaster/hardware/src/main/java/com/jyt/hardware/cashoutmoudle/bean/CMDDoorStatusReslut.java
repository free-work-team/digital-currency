package com.jyt.hardware.cashoutmoudle.bean;


public class CMDDoorStatusReslut  extends CommonBaseReslut {

	public CMDDoorStatusReslut(boolean flat) {
		super(flat);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 开门传感器，1-门开，0-门关
	 */
	private int openDoorI;
	/**
	 * 关门传感器，1-门开，0-门关
	 */
	private int closeDoorI;
	/**
	 * 回位传感器，1-开，0-关
	 */
	private int returnI;


	public int getOpenDoorI() {
		return openDoorI;
	}
	public void setOpenDoorI(int openDoorI) {
		this.openDoorI = openDoorI;
	}
	public int getCloseDoorI() {
		return closeDoorI;
	}
	public void setCloseDoorI(int closeDoorI) {
		this.closeDoorI = closeDoorI;
	}
	public int getReturnI() {
		return returnI;
	}
	public void setReturnI(int returnI) {
		this.returnI = returnI;
	}
	
	
	
	
}
