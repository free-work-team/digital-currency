package com.jyt.bitcoinmaster.wallet.entity;

public class QueryBalanceRequest {

    private String type;//1.买 2.卖

    private boolean subReserveFee;//是否需要减掉预留旷工费

    private String cryptoCurrency;//加密币种

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSubReserveFee() {
        return subReserveFee;
    }

    public void setSubReserveFee(boolean subReserveFee) {
        this.subReserveFee = subReserveFee;
    }

    public String getCryptoCurrency() {
        return cryptoCurrency;
    }

    public void setCryptoCurrency(String cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
    }
}
