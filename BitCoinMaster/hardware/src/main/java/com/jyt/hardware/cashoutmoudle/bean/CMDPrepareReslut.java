package com.jyt.hardware.cashoutmoudle.bean;


public class CMDPrepareReslut extends CommonBaseReslut {

	public CMDPrepareReslut(boolean flat) {
		super(flat);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 获取到合格钞票的张数
	 */

	private int CrownNum;
	/**
	 * 钞箱1冠字码
	 */
	private String CrownList1;
	/**
	 * 钞箱1冠字码图片列表（以逗号隔开）
	 */
	private String CrowImageList1;
	/**
	 * 钞箱2冠字码
	 */
	private String CrownList2;
	/**
	 * 钞箱2冠字码图片列表（以逗号隔开）
	 */
	private String CrowImageList2;
	/**
	 * 钞箱一出钞张数
	 */
	private int firstCashNumber;
	/***
	 * 钞箱2出钞张数
	 */
	private int secondCashNumber;
	/***
	 * 钞箱3出钞张数
	 */
	private int threeCashNumber;
	/**
	 * 钞箱一回收张数
	 */
	private int firstCashRecycle;
	/***
	 * 钞箱2回收张数
	 */
	private int secondCashRecycle;
	/***
	 * 钞箱2回收张数
	 */
	private int threeCashRecycle;


	public int getCrownNum(){
		return CrownNum;
	}
	public void setCrownNum(int crownNum){
		CrownNum = crownNum;
	}
	public int getFirstCashNumber() {
		return firstCashNumber;
	}
	public void setFirstCashNumber(int firstCashNumber) {
		this.firstCashNumber = firstCashNumber;
	}
	public int getSecondCashNumber() {
		return secondCashNumber;
	}
	public void setSecondCashNumber(int secondCashNumber) {
		this.secondCashNumber = secondCashNumber;
	}
	public int getFirstCashRecycle() {
		return firstCashRecycle;
	}
	public void setFirstCashRecycle(int firstCashRecycle) {
		this.firstCashRecycle = firstCashRecycle;
	}
	public int getSecondCashRecycle() {
		return secondCashRecycle;
	}
	public void setSecondCashRecycle(int secondCashRecycle) {
		this.secondCashRecycle = secondCashRecycle;
	}
	public String getCrownList1() {
		return CrownList1;
	}
	public void setCrownList1(String crownList1) {
		CrownList1 = crownList1;
	}
	public String getCrowImageList1() {
		return CrowImageList1;
	}
	public void setCrowImageList1(String crowImageList1) {
		CrowImageList1 = crowImageList1;
	}
	public String getCrownList2() {
		return CrownList2;
	}
	public void setCrownList2(String crownList2) {
		CrownList2 = crownList2;
	}
	public String getCrowImageList2() {
		return CrowImageList2;
	}
	public void setCrowImageList2(String crowImageList2) {
		CrowImageList2 = crowImageList2;
	}

	public int getThreeCashNumber() {
		return threeCashNumber;
	}

	public void setThreeCashNumber(int threeCashNumber) {
		this.threeCashNumber = threeCashNumber;
	}

	public int getThreeCashRecycle() {
		return threeCashRecycle;
	}

	public void setThreeCashRecycle(int threeCashRecycle) {
		this.threeCashRecycle = threeCashRecycle;
	}
}
