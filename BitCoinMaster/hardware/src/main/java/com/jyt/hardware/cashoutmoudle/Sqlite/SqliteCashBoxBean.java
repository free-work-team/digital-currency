package com.jyt.hardware.cashoutmoudle.Sqlite;

public class SqliteCashBoxBean {
	/**
	 * 钞箱id
	 */
	private String boxid;
	/**
	 * 面额
	 */
	private int denomination;
	/**
	 * 槽位
	 */
	private int solt;
	/**
	 * 剩余数
	 */
	private int remaining_number;

	/**
	 * 出钞总数量
	 */
	private int send_number;
	/**
	 * 总加钞数量
	 */
	private int all_number;
	/**
	 * 废钞数量（回收钞箱数）
	 *  
	 */
	private int recoveryQuantity;
	
	public int getRecoveryQuantity() {
		return recoveryQuantity;
	}

	public void setRecoveryQuantity(int recoveryQuantity) {
		this.recoveryQuantity = recoveryQuantity;
	}

	public String getBoxid() {
		return boxid;
	}

	public void setBoxid(String boxid) {
		this.boxid = boxid;
	}

	public int getDenomination() {
		return denomination;
	}

	public void setDenomination(int denomination) {
		this.denomination = denomination;
	}

	public int getSolt() {
		return solt;
	}

	public void setSolt(int solt) {
		this.solt = solt;
	}

	public int getRemaining_number() {
		return remaining_number;
	}

	public void setRemaining_number(int remaining_number) {
		this.remaining_number = remaining_number;
	}

	public int getSend_number() {
		return send_number;
	}

	public void setSend_number(int send_number) {
		this.send_number = send_number;
	}

	public int getAll_number() {
		return all_number;
	}

	public void setAll_number(int all_number) {
		this.all_number = all_number;
	}

}
