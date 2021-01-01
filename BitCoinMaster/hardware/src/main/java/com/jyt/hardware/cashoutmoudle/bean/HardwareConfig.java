package com.jyt.hardware.cashoutmoudle.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author huoChangGuo
 * @since 2019/9/11
 */
public class HardwareConfig {
    private String hwKey;
    private String hwValue;
    private String createTime;

    public String getHwKey() {
        return hwKey;
    }

    public void setHwKey(String hwKey) {
        this.hwKey = hwKey;
    }

    public String getHwValue() {
        return hwValue;
    }

    public void setHwValue(String hwValue) {
        this.hwValue = hwValue;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "{" +
                "hw_key:'" + hwKey + '\'' +
                ", hw_value:'" + hwValue + '\'' +
                ", create_time:'" + dateFormat(new Date()) + '\'' +
                '}';
    }

    private String dateFormat(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }
}
