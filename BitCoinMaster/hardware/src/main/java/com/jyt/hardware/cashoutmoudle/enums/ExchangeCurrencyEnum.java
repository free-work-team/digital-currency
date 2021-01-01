package com.jyt.hardware.cashoutmoudle.enums;


import org.apache.commons.lang.StringUtils;

/**
 * 交易所币种枚举 一定对应交易所的交易对 ,查交易所汇率用
 *
 * @author hcg
 * @date 2019-11-29
 */

public enum ExchangeCurrencyEnum {

    /*注意 以下一定保持一致
    * ExchangeCurrencyEnum
    * ExchangeKrakenEnum.PairEnum
    * ExchangeProEnum.PairEnum
    * ProductIdEnum
    * */
    USD("1", "USD"),
    EUR("2", "EUR"),
    GBP("3", "GBP"),
    BTC("4", "BTC"),
    ETHUSD("5", "USD"),
    ETHEUR("6", "EUR"),
    ETHGBP("7", "GBP");


    private String value;
    private String desc;

    ExchangeCurrencyEnum(String value, String desc) {
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
        for (ExchangeCurrencyEnum s : ExchangeCurrencyEnum.values()) {
            if (s.getValue().equals(code)) {
                return s.getDesc();
            }
        }
        return "";
    }

}

