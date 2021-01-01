package com.jyt.hardware.cashoutmoudle.enums;


/**
 * 自定义交易对
 *
 * @author hcg
 * @date 2019-11-29
 */

public enum ProductIdEnum {

    /*注意 以下一定保持一致
     * ExchangeCurrencyEnum
     * ExchangeKrakenEnum.PairEnum
     * ExchangeProEnum.PairEnum
     * ProductIdEnum
     * */
    // BTC
    BTC_USD("1", "BTC-USD"),
    BTC_EUR("2", "BTC-EUR"),
    BTC_GBP("3", "BTC-GBP"),

    //ETH
    ETH_BTC("4", "ETH-BTC"),
    ETH_USD("5", "ETH-USD"),
    ETH_EUR("6", "ETH-EUR"),
    ETH_GBP("7", "ETH-GBP");

    private String value;
    private String desc;

    ProductIdEnum(String value, String desc) {
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
        for (ProductIdEnum s : ProductIdEnum.values()) {
            if (s.getValue().equals(code)) {
                return s.getDesc();
            }
        }
        return "";
    }
}

