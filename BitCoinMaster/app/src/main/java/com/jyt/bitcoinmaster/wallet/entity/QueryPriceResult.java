package com.jyt.bitcoinmaster.wallet.entity;

/**
 * 渠道转币返回参数
 */
public class QueryPriceResult extends BaseResult {

    //市场价格
    private String price;
    // 交易所价格
    private String exchangeRate;

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
