package com.jyt.bitcoinmaster.wallet.entity;

/**
 * 钱包转币到交易所返回参数
 */
public class SendExchangeResult extends BaseResult {

    //上游交易订单hash
    private String txId;

    //上游交易手续费
    private String fee;

    //实际转移比特币数量
    private String actualAmount;

    //上游订单号
    private String transId;




    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(String actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }
}
