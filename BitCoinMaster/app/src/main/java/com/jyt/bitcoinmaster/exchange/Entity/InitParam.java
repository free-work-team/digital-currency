package com.jyt.bitcoinmaster.exchange.Entity;

/**
 * 交易所初始化参数
 *
 * @author huoChangGuo
 * @since 2019/11/20
 */
public class InitParam {
    private String cryptoCurrency;//加密币种


    public String getCryptoCurrency() {
        return cryptoCurrency;
    }

    public void setCryptoCurrency(String cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
    }
}
