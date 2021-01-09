package com.jyt.terminal.model;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * <p>
 * 终端机硬件状态实体类
 * </p>
 *
 * @author sunshubin
 * @since 2019-04-29
 */
@TableName("t_device")
public class Device extends Model<Device> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
 
    /**
     * 终端机号
     */
    @TableField("terminal_no")
    private String terminalNo;
    
    /**
     * 设备名称
     */
    @TableField("device_name")
    private String deviceName;
    
    /**
     * 状态
     */
    private int status;
    
    /**
     * 描述
     */
    private String desc;
    
    /**
     * 描述
     */
    @TableField("upload_time")
    private String uploadTime;
    
    
	public String getUploadTime() {
		return uploadTime;
	}



	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}



	public Integer getId() {
		return id;
	}



	public void setId(Integer id) {
		this.id = id;
	}



	public String getTerminalNo() {
		return terminalNo;
	}



	public void setTerminalNo(String terminalNo) {
		this.terminalNo = terminalNo;
	}



	public String getDeviceName() {
		return deviceName;
	}



	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}



	public int getStatus() {
		return status;
	}



	public void setStatus(int status) {
		this.status = status;
	}



	public String getDesc() {
		return desc;
	}



	public void setDesc(String desc) {
		this.desc = desc;
	}



	@Override
	protected Serializable pkVal() {
		return this.id;
	}
    

	
}
