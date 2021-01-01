package com.jyt.hardware.cashoutmoudle.enums;


/**
 * 交易所的枚举
 *
 * @author hcg
 * @date 2019-11-20
 */

public enum ExchangeEnum {

        NO(0, "No"),
        COINBASEPRO(1, "Coinbase Pro"),
        KRAKEN(2, "Kraken");

        private int value;
        private String desc;

       ExchangeEnum(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public static String valueOf(Integer code) {
            if (code == null) {
                return "";
            }
            for (ExchangeEnum s : ExchangeEnum.values()) {
                if (s.getValue() == code) {
                    return s.getDesc();
                }
            }
            return "";
        }

    /**
     * 通过value获取
     * @param yourValue
     * @return
     */
    public static ExchangeEnum getEnum(Integer yourValue) {
        if (yourValue != null) {
            for (ExchangeEnum s : ExchangeEnum.values()) {
                if (s.getValue() == yourValue) {
                    return s;
                }
            }
        }
        return ExchangeEnum.NO;
    }


}

