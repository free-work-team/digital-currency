package com.jyt.bitcoinmaster.wallet.entity;

/**
 * 根据id查询交易返回参数
 */
public class QueryTransByIdResult extends BaseResult {

    //上游交易订单id
    private String transId;

    private int status;

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
