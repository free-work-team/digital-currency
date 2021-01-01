package com.jyt.bitcoinmaster.exchange.kraken;

import java.math.BigDecimal;

/**
 * kraken参数
 *
 * @author huoChangGuo
 * @since 2020/3/18
 */
public class KrakenParam {
    private String krakenApiKey;
    private String krakenSecret;
    private BigDecimal krakenMinSize;
    private BigDecimal krakenWithdrawalsFee;// 提现手续费
    private String krakenAddress;//kraken的地址,用于存款
    private String krakenWithdrawalsAddressName;//对应钱包的地址,用于提款

    public String getKrakenApiKey() {
        return krakenApiKey;
    }

    public void setKrakenApiKey(String krakenApiKey) {
        this.krakenApiKey = krakenApiKey;
    }

    public String getKrakenSecret() {
        return krakenSecret;
    }

    public void setKrakenSecret(String krakenSecret) {
        this.krakenSecret = krakenSecret;
    }

    public BigDecimal getKrakenMinSize() {
        return krakenMinSize;
    }

    public void setKrakenMinSize(BigDecimal krakenMinSize) {
        this.krakenMinSize = krakenMinSize;
    }

    public BigDecimal getKrakenWithdrawalsFee() {
        return krakenWithdrawalsFee;
    }

    public void setKrakenWithdrawalsFee(BigDecimal krakenWithdrawalsFee) {
        this.krakenWithdrawalsFee = krakenWithdrawalsFee;
    }

    public String getKrakenAddress() {
        return krakenAddress;
    }

    public void setKrakenAddress(String krakenAddress) {
        this.krakenAddress = krakenAddress;
    }

    public String getKrakenWithdrawalsAddressName() {
        return krakenWithdrawalsAddressName;
    }

    public void setKrakenWithdrawalsAddressName(String krakenWithdrawalsAddressName) {
        this.krakenWithdrawalsAddressName = krakenWithdrawalsAddressName;
    }
}
