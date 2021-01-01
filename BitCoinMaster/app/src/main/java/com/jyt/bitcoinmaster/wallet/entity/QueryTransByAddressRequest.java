package com.jyt.bitcoinmaster.wallet.entity;

public class QueryTransByAddressRequest {

    //接收地址
    private String address;

    //金额
    private String amount;


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
