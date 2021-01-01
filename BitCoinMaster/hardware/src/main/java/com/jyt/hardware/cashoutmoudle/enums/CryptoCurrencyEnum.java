package com.jyt.hardware.cashoutmoudle.enums;


import org.apache.commons.lang.StringUtils;

/**
 * 虚拟币种枚举
 *
 * @author ssb
 * @date 2020-3-12
 */

public enum CryptoCurrencyEnum {

    BTC("btc", "BTC"),
    ETH("eth", "ETH");

    private String value;
    private String desc;

   CryptoCurrencyEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static String getValue(String desc) {
        if (StringUtils.isEmpty(desc)) {
            return "";
        }
        for (CryptoCurrencyEnum s : CryptoCurrencyEnum.values()) {
            if (s.getDesc().equals(desc)) {
                return s.getValue();
            }
        }
        return "";
    }

    public static String getDesc(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        for (CryptoCurrencyEnum s : CryptoCurrencyEnum.values()) {
            if (s.getValue().equals(value)) {
                return s.getDesc();
            }
        }
        return "";
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

