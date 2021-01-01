package com.jyt.bitcoinmaster.rate.entity;

/**
 * 渠道转币返回参数
 */
public class QueryPriceResult {

    //市场价格
    private String price;
    // 交易所价格
    private String exchangeRate;

    private String code;

    private String message;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(String exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}
