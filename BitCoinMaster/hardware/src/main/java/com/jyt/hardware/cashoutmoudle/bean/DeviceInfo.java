package com.jyt.hardware.cashoutmoudle.bean;

import java.io.Serializable;

/**
 * 设备信息状态
 *
 * @author huoChangGuo
 * @since 2019/5/5
 */
public class DeviceInfo {
    /**
     * 终端机号
     */
    private String terminalNo;

    /**
     * 设备名称
     */
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
     * 时间
     */
    private String uploadTime;

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
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
}
