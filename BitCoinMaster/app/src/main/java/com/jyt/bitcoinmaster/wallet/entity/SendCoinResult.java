package com.jyt.bitcoinmaster.wallet.entity;

/**
 * 渠道转币返回参数
 */
public class SendCoinResult extends BaseResult {

    //上游交易订单id
    private String transId;

    //上游交易订单hash
    private String txId;

    //上游交易手续费
    private String fee;


    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }
}
