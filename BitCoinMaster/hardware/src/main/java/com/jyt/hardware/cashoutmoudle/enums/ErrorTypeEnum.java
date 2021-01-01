package com.jyt.hardware.cashoutmoudle.enums;


/**
 * 错误的枚举
 *
 * @author hcg
 * @date 2019-12-02
 */

public enum ErrorTypeEnum {

    TO_WALLET("SEND WALLET, ", "WITHDRAWAL WALLET, "),
    TO_EXCHANGE("SEND EXCHANGE, ", "WITHDRAWAL EXCHANGE, "),
    TO_ORDER("EXCHANGE ORDER, ", "EXCHANGE ORDER, "),
    TO_TRAN("WALLET ORDER, ", "WALLET ORDER, "),
    CANCEL_ORDER("CANCEL ORDER, ", "CANCEL ORDER, ");

    private String value;
    private String desc;

    ErrorTypeEnum(String value, String desc) {
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

