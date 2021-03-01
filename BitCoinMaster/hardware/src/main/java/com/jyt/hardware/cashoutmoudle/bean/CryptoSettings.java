package com.jyt.hardware.cashoutmoudle.bean;



public class CryptoSettings {

    private Integer exchange; //交易所

    private Integer hotWallet; //钱包

    private Integer exchangeStrategy; //交易策略

    private Integer confirmations; //确认方式

    private Integer rateSource; //币汇率来源

    private String price; //价格

    private String buyTransactionFee; //购买手续费

    private String buySingleFee; //购买单笔费用

    private String sellTransactionFee; //出售手续费

    private String sellSingleFee; //出售单笔费用

    private String minNeedCash; //最小交易金额

    private String channelParam; //渠道参数

    public Integer getExchange() {
        return exchange;
    }

    public void setExchange(Integer exchange) {
        this.exchange = exchange;
    }

    public Integer getHotWallet() {
        return hotWallet;
    }

    public void setHotWallet(Integer hotWallet) {
        this.hotWallet = hotWallet;
    }

    public Integer getExchangeStrategy() {
        return exchangeStrategy;
    }

    public void setExchangeStrategy(Integer exchangeStrategy) {
        this.exchangeStrategy = exchangeStrategy;
    }

    public Integer getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(Integer confirmations) {
        this.confirmations = confirmations;
    }

    public Integer getRateSource() {
        return rateSource;
    }

    public void setRateSource(Integer rateSource) {
        this.rateSource = rateSource;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        if ("0".equals(price)) {
            this.price = "";
        } else {
            this.price = price;
        }
    }

    public String getBuyTransactionFee() {
        return buyTransactionFee;
    }

    public void setBuyTransactionFee(String buyTransactionFee) {
        this.buyTransactionFee = buyTransactionFee;
    }

    public String getBuySingleFee() {
        return buySingleFee;
    }

    public void setBuySingleFee(String buySingleFee) {
        this.buySingleFee = buySingleFee;
    }

    public String getSellTransactionFee() {
        return sellTransactionFee;
    }

    public void setSellTransactionFee(String sellTransactionFee) {
        this.sellTransactionFee = sellTransactionFee;
    }

    public String getSellSingleFee() {
        return sellSingleFee;
    }

    public void setSellSingleFee(String sellSingleFee) {
        this.sellSingleFee = sellSingleFee;
    }

    public String getMinNeedCash() {
        return minNeedCash;
    }

    public void setMinNeedCash(String minNeedCash) {
        this.minNeedCash = minNeedCash;
    }

    public String getChannelParam() {
        return channelParam;
    }

    public void setChannelParam(String channelParam) {
        this.channelParam = channelParam;
    }
}
