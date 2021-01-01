package com.jyt.bitcoinmaster.wallet.entity;

import java.math.BigDecimal;

/**
 * 渠道转币返回参数
 */
public class QueryBalanceResult extends BaseResult {

    //余额
    private BigDecimal balance;

    private String accountId;//coinbase返回


    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
