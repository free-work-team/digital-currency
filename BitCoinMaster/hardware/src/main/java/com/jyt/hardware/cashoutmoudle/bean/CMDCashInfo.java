package com.jyt.hardware.cashoutmoudle.bean;

public class CMDCashInfo {
	private boolean Success;
	/**
	 * 错误码(失败原因)  995 服务器未连接  996 UnknownHostException   998 crc校验出错   999 连接超时  994 消息头校验错
	 */
	private int ErrorCode;

	/**
	 * 物理代码
	 */

	private int PhyCode;
	/**
	 * 逻辑代码
	 */
	private int LogicCode;
	/**
	 * 钞箱槽位
	 */
	private int hopper;
	/**
	 * 钞箱币种
	 */
	private String acCurrency;
	/**
	 * 面额
	 */
	private int CNY;
	/**
	 * 钞箱转态
	 */
	private String status;



	public boolean isSuccess() {
		return Success;
	}

	public void setSuccess(boolean success) {
		Success = success;
	}

	public int getPhyCode() {
		return PhyCode;
	}

	public void setPhyCode(int phyCode) {
		PhyCode = phyCode;
	}

	public int getLogicCode() {
		return LogicCode;
	}

	public void setLogicCode(int logicCode) {
		LogicCode = logicCode;
	}

	public int getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(int errorCode) {
		ErrorCode = errorCode;
	}
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

	public int getCNY() {
		return CNY;
	}

	public void setCNY(int cNY) {
		CNY = cNY;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Success="+Success+",ErrorCode="+ErrorCode +",PhyCode="+PhyCode+",LogicCode="+LogicCode+
				",hopper="+hopper+",acCurrency="+acCurrency +",CNY="+CNY+",status="+status;
	}
}
