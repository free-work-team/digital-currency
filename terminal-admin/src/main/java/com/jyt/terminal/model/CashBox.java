package com.jyt.terminal.model;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
/**
 * 钞箱(拿出、放回)实体类
 * @className CashBox
 * @author wangwei
 * @date 2019年11月8日
 *
 */
@TableName("t_cash_box")
public class CashBox extends Model<CashBox>{
	
	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.AUTO)
	private int id;
	
	@TableField("terminal_no")
	private String terminalNo;
	
	private Integer status;
	
	@TableField("create_time")
	private Date createTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTerminalNo() {
		return terminalNo;
	}

	public void setTerminalNo(String terminalNo) {
		this.terminalNo = terminalNo;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
