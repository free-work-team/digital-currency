package com.jyt.hardware.cashoutmoudle.enums;


/**
 * 存取状态的枚举
 *
 * @author hcg
 * @date 2019-12-02
 */

public enum TransferTypeEnum {

    TO_WALLET("1", "TO_WALLET"),
    TO_EXCHANGE("2", "TO_EXCHANGE");


    private String value;
    private String desc;

    TransferTypeEnum(String value, String desc) {
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

