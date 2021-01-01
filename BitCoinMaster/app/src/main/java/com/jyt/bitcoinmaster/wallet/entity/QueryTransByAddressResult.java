package com.jyt.bitcoinmaster.wallet.entity;

/**
 * 根据地址查询交易返回参数
 */
public class QueryTransByAddressResult extends BaseResult {

    //上游交易订单id
    private String transId;


    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }


}
