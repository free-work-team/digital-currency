package com.jyt.terminal.model;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * 终端机管理实体类
 * @className Hardware
 * @author wangwei
 * @date 2019年5月5日
 *
 */
@TableName("t_hardware")
public class Hardware extends Model<Hardware> {

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
     * 硬件模块名称
     */
    @TableField("modular_name")
    private String modularName;
    
    /**
     * 运行状态
     */
    @TableField("status")
    private Integer status;
    
    /**
     * 更新时间
     */
    @TableField("update_time")
    private String updateTime;
    
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

	public String getModularName() {
		return modularName;
	}

	public void setModularName(String modularName) {
		this.modularName = modularName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
