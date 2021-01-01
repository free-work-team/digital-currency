package com.jyt.hardware.cashoutmoudle.enums;


import org.apache.commons.lang.StringUtils;

/**
 * 钱包的枚举
 *
 * @author hcg
 * @date 2019-11-20
 */

public enum WalletEnum {
        OTHER(0, "other"),
        BITGO(1, "bitgo"),
        COINBASE(2, "coinbase"),
        BLOCKCHAIN(3, "blockchain");

        private int value;
        private String desc;

       WalletEnum(int value, String desc) {
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
            for (WalletEnum s : WalletEnum.values()) {
                if (s.getValue() == code) {
                    return s.getDesc();
                }
            }
            return "";
        }

        /**
         * 通过edsc获取
         * @param yourValue
         * @return
         */
        public static WalletEnum getEnum(Integer yourValue) {
        if (yourValue != null ) {
            for (WalletEnum s : WalletEnum.values()) {
                if (s.getValue() == yourValue ) {
                    return s;
                }
            }
        }
        return WalletEnum.OTHER;
        }


    }

