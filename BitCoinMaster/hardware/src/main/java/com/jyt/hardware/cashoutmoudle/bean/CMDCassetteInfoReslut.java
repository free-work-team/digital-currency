package com.jyt.hardware.cashoutmoudle.bean;


public class CMDCassetteInfoReslut extends CommonBaseReslut {

	public CMDCassetteInfoReslut(boolean flat) {
		super(flat);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 槽位1
	 */
	private int hopper;
	/**
	 * 槽位2
	 */
	private int hopper2;
	/**
	 * 槽位3
	 */
	private int hopper3;

	/**
	 * 钱箱1币种
	 */
	private String acCurrency;
	/**
	 * 钱箱2币种
	 */
	private String acCurrency2;
	/**
	 * 钱箱3币种
	 */
	private String acCurrency3;

	/**
	 * 钱箱1面额
	 */
	private int lDenomination;
	/**
	 * 钱箱2面额
	 */
	private int lDenomination2;
	/**
	 * 钱箱3面额
	 */
	private int lDenomination3;

	/**
	 * 钱箱状态
	 */
	private int cStatus;

	public int getHopper() {
		return hopper;
	}

	public void setHopper(int hopper) {
		this.hopper = hopper;
	}

	public String getAcCurrency() {
		return acCurrency;
	}

	public void setAcCurrency(String acCurrency) {
		this.acCurrency = acCurrency;
	}

	public int getlDenomination() {
		return lDenomination;
	}

	public void setlDenomination(int lDenomination) {
		this.lDenomination = lDenomination;
	}

	public int getcStatus() {
		return cStatus;
	}

	public void setcStatus(int cStatus) {
		this.cStatus = cStatus;
	}

	public int getHopper2() {
		return hopper2;
	}

	public void setHopper2(int hopper2) {
		this.hopper2 = hopper2;
	}

	public int getHopper3() {
		return hopper3;
	}

	public void setHopper3(int hopper3) {
		this.hopper3 = hopper3;
	}

	public String getAcCurrency2() {
		return acCurrency2;
	}

	public void setAcCurrency2(String acCurrency2) {
		this.acCurrency2 = acCurrency2;
	}

	public String getAcCurrency3() {
		return acCurrency3;
	}

	public void setAcCurrency3(String acCurrency3) {
		this.acCurrency3 = acCurrency3;
	}

	public int getlDenomination2() {
		return lDenomination2;
	}

	public void setlDenomination2(int lDenomination2) {
		this.lDenomination2 = lDenomination2;
	}

	public int getlDenomination3() {
		return lDenomination3;
	}

	public void setlDenomination3(int lDenomination3) {
		this.lDenomination3 = lDenomination3;
	}
}
