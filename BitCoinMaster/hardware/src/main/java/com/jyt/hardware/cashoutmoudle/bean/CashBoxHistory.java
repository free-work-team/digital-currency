package com.jyt.hardware.cashoutmoudle.bean;

import java.util.Date;

/**
 * 钞箱取出记录
 *
 * @author huoChangGuo
 * @since 2019/11/8
 */
public class CashBoxHistory {

    private String terminalNo;

    private Integer status;

    private Date createTime;

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
}
