package com.jyt.hardware.cashoutmoudle.bean;


public class CMDGetDoorReslut extends CommonBaseReslut {

	public CMDGetDoorReslut(boolean flat) {
		super(flat);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 动态库版本
	 */
	private String soVersion;
	/**
	 * 硬件版本号
	 */
	private String hardwearVersion;
	/**
	 * 硬件序列号
	 */
	private String hardwearSN;


	public String getSoVersion() {
		return soVersion;
	}

	public void setSoVersion(String soVersion) {
		this.soVersion = soVersion;
	}

	public String getHardwearVersion() {
		return hardwearVersion;
	}

	public void setHardwearVersion(String hardwearVersion) {
		this.hardwearVersion = hardwearVersion;
	}

	public String getHardwearSN() {
		return hardwearSN;
	}

	public void setHardwearSN(String hardwearSN) {
		this.hardwearSN = hardwearSN;
	}

}
