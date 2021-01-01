package com.jyt.bitcoinmaster.wallet.entity;

import java.math.BigDecimal;

/**
 * 渠道查询账户返回参数
 */
public class QueryAccountResult extends BaseResult {

    //账户索引
    private String  index;


    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}
