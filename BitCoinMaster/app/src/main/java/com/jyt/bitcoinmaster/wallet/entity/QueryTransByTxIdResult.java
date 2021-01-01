package com.jyt.bitcoinmaster.wallet.entity;

/**
 * 根据txid查询交易返回参数
 */
public class QueryTransByTxIdResult extends BaseResult {


    private int status;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
