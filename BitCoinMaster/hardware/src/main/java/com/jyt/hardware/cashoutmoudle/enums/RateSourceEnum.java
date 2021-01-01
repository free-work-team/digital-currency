package com.jyt.hardware.cashoutmoudle.enums;


/**
 * 币汇率来源
 *
 * @author hcg
 * @date 2019-12-12
 */

public enum RateSourceEnum {

        COINBASE(1, "Coinbase"),
        OTHER(2, "Other");

        private int value;
        private String desc;

       RateSourceEnum(int value, String desc) {
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
            for (RateSourceEnum s : RateSourceEnum.values()) {
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
    public static RateSourceEnum getEnum(Integer yourValue) {
        if (yourValue != null) {
            for (RateSourceEnum s : RateSourceEnum.values()) {
                if (s.getValue() == yourValue) {
                    return s;
                }
            }
        }
        return RateSourceEnum.COINBASE;
    }


}

