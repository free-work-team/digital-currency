package com.jyt.bitcoinmaster.wallet.entity;

/**
 * 根据id查询交易返回参数
 */
public class QueryTransConfirmResult extends BaseResult {

    private int status;

    private String txId;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }
}
