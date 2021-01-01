package com.jyt.hardware.cashoutmoudle.enums;


/**
 * 交易策略的枚举
 *
 * @author hcg
 * @date 2019-12-09
 */

public enum ExchangeStrategyEnum {

        NO_EXCHANGE(0, "不使用交易所"),
        WALLET_EXCHANGE(1, "热钱包预存比特币"),
        NO_WALLET(2, "热钱包不预存比特币，直接在交易所买卖比特币");

        private int value;
        private String desc;

       ExchangeStrategyEnum(int value, String desc) {
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
            for (ExchangeStrategyEnum s : ExchangeStrategyEnum.values()) {
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
    public static ExchangeStrategyEnum getEnum(Integer yourValue) {
        if (yourValue != null) {
            for (ExchangeStrategyEnum s : ExchangeStrategyEnum.values()) {
                if (s.getValue() == yourValue) {
                    return s;
                }
            }
        }
        return ExchangeStrategyEnum.NO_EXCHANGE;
    }


}

