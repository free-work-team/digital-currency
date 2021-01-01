package com.jyt.hardware.cashoutmoudle.bean;

public class AddNotes {

	private Integer id ;

	private String amount ;

	private String createTime;
	


	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getAmount() {
		return amount;
	}
}
