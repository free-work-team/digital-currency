package com.jyt.hardware.cashoutmoudle.enums;


/**
 * 存取状态的枚举
 *
 * @author hcg
 * @date 2019-12-02
 */

public enum TransferStatusEnum {

    PENDING("1", "PENDING"),
    CONFIRM("2", "CONFIRM"),
    FAIL("3", "FAIL");


    private String value;
    private String desc;

    TransferStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}

