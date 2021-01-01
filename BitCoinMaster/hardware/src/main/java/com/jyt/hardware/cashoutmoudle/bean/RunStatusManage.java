package com.jyt.hardware.cashoutmoudle.bean;

public class RunStatusManage {

	private String serialId;
	
	private String terminalNo;
	
	private String hardwareModularName;
	
	private String status;
	
	private String updateTime;

	public String getSerialId() {
		return serialId;
	}

	public void setSerialId(String serialId) {
		this.serialId = serialId;
	}

	public String getTerminalNo() {
		return terminalNo;
	}

	public void setTerminalNo(String terminalNo) {
		this.terminalNo = terminalNo;
	}

	public String getHardwareModularName() {
		return hardwareModularName;
	}

	public void setHardwareModularName(String hardwareModularName) {
		this.hardwareModularName = hardwareModularName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "{" +
				"serial_id:'" + serialId + '\'' +
				", terminal_no:'" + terminalNo + '\'' +
				", hardware_modular_name:'" + hardwareModularName + '\'' +
				", status:'" + status + '\'' +
				", update_time:'" + updateTime + '\'' +
				'}';
	}
}
