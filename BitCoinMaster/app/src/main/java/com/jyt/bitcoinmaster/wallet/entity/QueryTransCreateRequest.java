package com.jyt.bitcoinmaster.wallet.entity;

import com.jyt.hardware.cashoutmoudle.bean.WithdrawLog;

public class QueryTransCreateRequest {



    private WithdrawLog withdrawLog;

    private String cryptoCurrency;//加密币种

    public WithdrawLog getWithdrawLog() {
        return withdrawLog;
    }

    public void setWithdrawLog(WithdrawLog withdrawLog) {
        this.withdrawLog = withdrawLog;
    }

    public String getCryptoCurrency() {
        return cryptoCurrency;
    }

    public void setCryptoCurrency(String cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
    }
}
