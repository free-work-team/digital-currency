package com.jyt.hardware.cashoutmoudle.bean;


public class CMDTypeReslut extends CommonBaseReslut {

	public CMDTypeReslut(boolean flat) {
		super(flat);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 出钞模块类型
	 */
	private String devType;
	/**
	 * 程序版本号
	 */
	private String CharversionC;
	/**
	 * 机芯版本号
	 */
	private String versionGD;



	public String getDevType() {
		return devType;
	}
	public void setDevType(String devType) {
		this.devType = devType;
	}
	public String getCharversionC() {
		return CharversionC;
	}
	public void setCharversionC(String charversionC) {
		CharversionC = charversionC;
	}
	public String getVersionGD() {
		return versionGD;
	}
	public void setVersionGD(String versionGD) {
		this.versionGD = versionGD;
	}
	
	
}
