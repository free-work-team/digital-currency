package com.jyt.bitcoinmaster.wallet.entity;


public class QueryTransConfirmRequest {


    private String transId;

    private String cryptoCurrency;//加密币种

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getCryptoCurrency() {
        return cryptoCurrency;
    }

    public void setCryptoCurrency(String cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
    }
}
