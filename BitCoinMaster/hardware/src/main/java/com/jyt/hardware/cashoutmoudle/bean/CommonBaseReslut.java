package com.jyt.hardware.cashoutmoudle.bean;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class CommonBaseReslut {
	private boolean Success;
	/**
	 * 错误码(失败原因)991 解析返回数据异常    995 服务器未连接  996 UnknownHostException   998 crc校验出错   999 连接超时  994 消息头校验错
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
	 *返回字段
	 */
	private String ReturnField;

	/**
	 * 错误提示
	 */
	private String ErrorMessage;

	public CommonBaseReslut(boolean flat){
		this.Success=flat;
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
	public boolean isSuccess() {
		return Success;
	}
	public void setSuccess(boolean success) {
		Success = success;
	}
	public int getErrorCode() {
		return ErrorCode;
	}
	public void setErrorCode(int errorCode) {
		ErrorCode = errorCode;
	}


	public String getReturnField() {
		return ReturnField;
	}


	public void setReturnField(String returnField) {
		ReturnField = returnField;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}


	public String getErrorMessage() {
		return ErrorMessage;
	}


	public void setErrorMessage(String errorMessage) {
		ErrorMessage = errorMessage;
	}

}
