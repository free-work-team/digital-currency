package com.jyt.bitcoinmaster.exchange.coinbasePro;

/**
 * coinbasepro参数
 *
 * @author huoChangGuo
 * @since 2020/3/18
 */
public class CoinbaseProParam {

    private String proKey;
    private String proSecret;
    private String proMinSize;
    private String proPassphrase;
    private String coinbaseAccountId;

    public String getProKey() {
        return proKey;
    }

    public void setProKey(String proKey) {
        this.proKey = proKey;
    }

    public String getProSecret() {
        return proSecret;
    }

    public void setProSecret(String proSecret) {
        this.proSecret = proSecret;
    }

    public String getProMinSize() {
        return proMinSize;
    }

    public void setProMinSize(String proMinSize) {
        this.proMinSize = proMinSize;
    }

    public String getProPassphrase() {
        return proPassphrase;
    }

    public void setProPassphrase(String proPassphrase) {
        this.proPassphrase = proPassphrase;
    }

    public String getCoinbaseAccountId() {
        return coinbaseAccountId;
    }

    public void setCoinbaseAccountId(String coinbaseAccountId) {
        this.coinbaseAccountId = coinbaseAccountId;
    }
}
