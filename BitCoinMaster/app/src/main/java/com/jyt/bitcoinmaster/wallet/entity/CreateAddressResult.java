package com.jyt.bitcoinmaster.wallet.entity;

/**
 * 渠道转币返回参数
 */
public class CreateAddressResult extends BaseResult {

    //地址
    private String address;

    private String addressId; //coinbase eth需要用到

    //公钥
    private String xpub;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getXpub() {
        return xpub;
    }

    public void setXpub(String xpub) {
        this.xpub = xpub;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }
}
