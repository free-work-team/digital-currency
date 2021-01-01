package com.jyt.hardware.cashoutmoudle.bean;

public class EmpytNotes {

	private Integer id ;

	private String addCash ;

	private String buyCash;
	
	private String sellCash;
	
	private String createTime;

	private String lastTime;


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

	public void setAddCash(String addCash) {
		this.addCash = addCash;
	}

	public String getAddCash() {
		return addCash;
	}

	public void setBuyCash(String buyCash) {
		this.buyCash = buyCash;
	}

	public String getBuyCash() {
		return buyCash;
	}

	public void setSellCash(String sellCash) {
		this.sellCash = sellCash;
	}

	public String getSellCash() {
		return sellCash;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public String getLastTime() {
		return lastTime;
	}

	@Override
	public String toString() {
		return "{" +
				"\"id\":\"" + id +"\","+
				"\"addCash\":\"" + addCash + "\","+
				"\"buyCash\":\"" + buyCash + "\"," +
				"\"sellCash\":\"" + sellCash + "\"," +
				"\"createTime\":\"" + createTime + "\"," +
				"\"lastTime\":\"" + lastTime + "\"" +
				'}';
	}

}
