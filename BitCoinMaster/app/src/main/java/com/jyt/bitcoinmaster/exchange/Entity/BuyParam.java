package com.jyt.bitcoinmaster.exchange.Entity;

/**
 * 交易所买币参数
 *
 * @author huoChangGuo
 * @since 2019/11/20
 */
public class BuyParam {

    String transId;// 买币订单id
    String terminalNo;// 终端号
    String funds;// 交易金额
    String size;// 交易数量
    String price;// 交易汇率
    private String cryptoCurrency;// 币种
    private String currency;// 币种


    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getTerminalNo() {
        return terminalNo;
    }

    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
    }

    public String getFunds() {
        return funds;
    }

    public void setFunds(String funds) {
        this.funds = funds;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCryptoCurrency() {
        return cryptoCurrency;
    }

    public void setCryptoCurrency(String cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
