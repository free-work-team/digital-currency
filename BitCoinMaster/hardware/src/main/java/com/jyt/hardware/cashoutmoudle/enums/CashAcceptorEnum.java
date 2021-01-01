package com.jyt.hardware.cashoutmoudle.enums;


/**
 * 纸币接收模块
 *
 * @author hcg
 * @date 2020-6-15
 */

public enum CashAcceptorEnum {

    ITL("ITL", "ITL"),
    CPI("CPI", "CPI"),
    DEFAULT("DEFAULT", "未设置");

    private String value;
    private String desc;

    CashAcceptorEnum(String value, String desc) {
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
    /**
     * 通过value获取
     *
     * @param yourValue
     * @return
     */
    public static CashAcceptorEnum getEnum(String yourValue) {
        if (yourValue != null) {
            for (CashAcceptorEnum s : CashAcceptorEnum.values()) {
                if (s.getValue().equals(yourValue)) {
                    return s;
                }
            }
        }
        return DEFAULT;
    }


}

