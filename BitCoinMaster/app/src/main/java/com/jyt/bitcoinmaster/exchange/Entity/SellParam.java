package com.jyt.bitcoinmaster.exchange.Entity;

/**
 * 交易所卖币参数
 *
 * @author huoChangGuo
 * @since 2019/11/20
 */
public class SellParam {

    /*pro start*/
    private String transId;// 钱包交易订单号
    private String terminalNo;// 终端号
    private String funds;// 交易金额
    private String size;// 卖币数量
    private String price;//当前币汇率
    private String cryptoCurrency;// 币种
    private String currency;// 币种
    /*pro end*/

    public SellParam(String transId, String terminalNo, String funds, String size, String price, String cryptoCurrency, String currency) {
        this.transId = transId;
        this.terminalNo=terminalNo;
        this.funds = funds;
        this.size = size;
        this.price = price;
        this.cryptoCurrency = cryptoCurrency;
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

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
}
