package com.jyt.bitcoinmaster.wallet.entity;

public class CreateAddressRequest {

    private String cryptoCurrency;//加密币种

    public String getCryptoCurrency() {
        return cryptoCurrency;
    }

    public void setCryptoCurrency(String cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
    }
}
