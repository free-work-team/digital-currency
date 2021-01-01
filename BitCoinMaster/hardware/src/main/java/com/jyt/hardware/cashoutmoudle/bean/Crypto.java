package com.jyt.hardware.cashoutmoudle.bean;

import com.jyt.hardware.cashoutmoudle.enums.CryptoCurrencyEnum;

public class Crypto {

    private CryptoSettings btc;
    private CryptoSettings eth;


    public CryptoSettings getCryptoSetting(String cryptoCurrency){
        if(CryptoCurrencyEnum.BTC.getValue().equals(cryptoCurrency)){
            return btc;
        }else if(CryptoCurrencyEnum.ETH.getValue().equals(cryptoCurrency)){
            return eth;
        }
        return null;
    }
    public CryptoSettings getBtc() {
        return btc;
    }

    public void setBtc(CryptoSettings btc) {
        this.btc = btc;
    }

    public CryptoSettings getEth() {
        return eth;
    }

    public void setEth(CryptoSettings eth) {
        this.eth = eth;
    }
}
