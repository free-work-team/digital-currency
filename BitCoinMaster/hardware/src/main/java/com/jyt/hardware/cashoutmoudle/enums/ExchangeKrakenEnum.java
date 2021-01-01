package com.jyt.hardware.cashoutmoudle.enums;


/**
 * kraken的枚举
 *
 * @author hcg
 * @date 2019-11-29
 */

public class ExchangeKrakenEnum {

    /*注意 以下一定保持一致
     * ExchangeCurrencyEnum
     * ExchangeKrakenEnum.PairEnum
     * ExchangeProEnum.PairEnum
     * ProductIdEnum
     * */
    public enum PairEnum {
        BTC_USD("1", "XBTUSD"),
        BTC_EUR("2", "XBTEUR"),
        BTC_GBP("3", "XBTGBP"),

        ETH_BTC("4", "ETHXBT"),
        ETH_USD("5", "ETHUSD"),
        ETH_EUR("6", "ETHEUR"),
        ETH_GBP("7", "ETHGBP");


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
            if (code == null) {
                return "";
            }
            for (PairEnum s : PairEnum.values()) {
                if (s.getValue().equals(code) ) {
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
            if (code == null) {
                return "";
            }
            for (OrderTypeEnum s : OrderTypeEnum.values()) {
                if (s.getValue().equals(code) ) {
                    return s.getDesc();
                }
            }
            return "";
        }
    }

    /*kranken根据币种不同的参数*/
    public enum OrderMinSizeEnum {
        MINSIZEBTC("btc", "0.005"),
        MINSIZEETH("eth", "0.02");

        private String value;
        private String desc;

        OrderMinSizeEnum(String value, String desc) {
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
            for (OrderMinSizeEnum s : OrderMinSizeEnum.values()) {
                if (s.getValue().equals(code) ) {
                    return s.getDesc();
                }
            }
            return "";
        }
    }

    /*kranken根据币种不同的参数,存款的手续费，少卖点*/
    public enum OrderDepositFee {
        FEEBTC("btc", "0"),
        FEEETH("eth", "0.0005");

        private String value;
        private String desc;

        OrderDepositFee(String value, String desc) {
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
            for (OrderDepositFee s : OrderDepositFee.values()) {
                if (s.getValue().equals(code) ) {
                    return s.getDesc();
                }
            }
            return "";
        }
    }

    public enum OrderWithdrawalsFee {
        FEEBTC("btc", "0.0005"),
        FEEETH("eth", "0.005");

        private String value;
        private String desc;

        OrderWithdrawalsFee(String value, String desc) {
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
            for (OrderWithdrawalsFee s : OrderWithdrawalsFee.values()) {
                if (s.getValue().equals(code) ) {
                    return s.getDesc();
                }
            }
            return "";
        }
    }

    public enum krakenPair {
         BTC("btc", "XBT"),
         ETH("eth", "XETH");

        private String value;
        private String desc;

        krakenPair(String value, String desc) {
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
            for (krakenPair s : krakenPair.values()) {
                if (s.getValue().equals(code) ) {
                    return s.getDesc();
                }
            }
            return "";
        }
    }
    public enum krakenMethod {
         BTC("btc", "Bitcoin"),
         ETH("eth", "Ether (Hex)");

        private String value;
        private String desc;

        krakenMethod(String value, String desc) {
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
            for (krakenMethod s : krakenMethod.values()) {
                if (s.getValue().equals(code) ) {
                    return s.getDesc();
                }
            }
            return "";
        }
    }

    public enum krakenWithdrawMethod {
         BTC("btc", "Bitcoin"),
         ETH("eth", "Ether");

        private String value;
        private String desc;

        krakenWithdrawMethod(String value, String desc) {
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
            for (krakenWithdrawMethod s : krakenWithdrawMethod.values()) {
                if (s.getValue().equals(code) ) {
                    return s.getDesc();
                }
            }
            return "";
        }
    }

}

