package com.jyt.hardware.cashoutmoudle.enums;


import org.apache.commons.lang.StringUtils;

/**
 * coinbasepro的枚举
 *
 * @author hcg
 * @date 2019-11-29
 */

public class ExchangeProEnum {

    /*注意 以下一定保持一致
     * ExchangeCurrencyEnum
     * ExchangeKrakenEnum.PairEnum
     * ExchangeProEnum.PairEnum
     * ProductIdEnum
     * */
    public enum PairEnum {
        BTC_USD("1", "BTC-USD"),
        BTC_EUR("2", "BTC-EUR"),
        BTC_GBP("3", "BTC-GBP"),

        ETH_BTC("4", "ETH-BTC"),
        ETH_USD("5", "ETH-USD"),
        ETH_EUR("6", "ETH-EUR"),
        ETH_GBP("7", "ETH-GBP");

        private String value;
        private String desc;

        PairEnum(String value, String desc) {
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

        public static String getDesc(String code) {
            if (StringUtils.isEmpty(code)) {
                return "";
            }
            for (PairEnum s : PairEnum.values()) {
                if (s.getValue().equals(code)) {
                    return s.getDesc();
                }
            }
            return "";
        }
    }


    /*购买方式*/
    public enum OrderTypeEnum {
        MARKET("1", "market"),
        LIMIT("2", "limit");

        private String value;
        private String desc;

        OrderTypeEnum(String value, String desc) {
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

        public static String getDesc(String code) {
            if (StringUtils.isEmpty(code)) {
                return "";
            }
            for (OrderTypeEnum s : OrderTypeEnum.values()) {
                if (s.getValue().equals(code)) {
                    return s.getDesc();
                }
            }
            return "";
        }
    }

    public enum proPair {
        BTC("btc", "BTC"),
        ETH("eth", "ETH");

        private String value;
        private String desc;

        proPair(String value, String desc) {
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

        public static String getDesc(String code) {
            if (code == null) {
                return "";
            }
            for (ExchangeProEnum.proPair s : ExchangeProEnum.proPair.values()) {
                if (s.getValue().equals(code) ) {
                    return s.getDesc();
                }
            }
            return "";
        }
    }

}

