package com.jyt.hardware.cashoutmoudle.bean;

public class ReceiveDateInfo {
	/**
	 * ���У���Ƿ�ɹ�
	 */
	private boolean isSuccess;
	/**
	 * ���յ������
	 */
	private String receiveDate;
	/**
	 * ������
	 */
	private int ErrorCode;
	/**
	 * ���յ������
	 */
	private String rDate;

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(String receiveDate) {
		this.receiveDate = receiveDate;
	}

	/**
	 * ��ȡ������Ϣ
	 * 
	 * @return ErrorCode 999-���ճ�ʱ 998-���У����� 997����ģ����Ӧ�� 996 UnknownHostException 995 SocketException IOException
	 */
	public int getErrorCode() {
		return ErrorCode;
	}

	/**
	 * ���ô�����Ϣ
	 * 
	 * @param errorCode
	 *            ErrorCode 999-���ճ�ʱ 998-���У����� 997����ģ����Ӧ�� 996 UnknownHostException 995 SocketException IOException
	 */
	public void setErrorCode(int errorCode) {
		ErrorCode = errorCode;
	}

	public String getrDate() {
		return rDate;
	}

	public void setrDate(String rDate) {
		this.rDate = rDate;
	}


	
}
