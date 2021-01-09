package com.jyt.terminal.commom.enums;
/**
 * 状态。1：管理平台、2:终端用户
 *
 */
public enum LoadTypeEnum {
	ADMIN(1, "管理平台"), TERMINAL(2, "终端用户"),;

	private int value;
	private String desc;

	LoadTypeEnum(int value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}