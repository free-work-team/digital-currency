package com.jyt.bitcoinmaster.wallet.entity;

public class QueryPriceRequest {

    private String currency;

    private String exchangeCurrency;//交易所币种

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getExchangeCurrency() {
        return exchangeCurrency;
    }

    public void setExchangeCurrency(String exchangeCurrency) {
        this.exchangeCurrency = exchangeCurrency;
    }
}
