package com.jyt.hardware.cashoutmoudle.enums;


/**
 * 交易状态的枚举
 *
 * @author hcg
 * @date 2019-11-20
 */

public enum ConfirmStatusEnum {

    NO_CONFIRM("0", "NO_CONFIRM"),
    CONFIRMED("1", "CONFIRMED");


    private String value;
    private String desc;

    ConfirmStatusEnum(String value, String desc) {
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

