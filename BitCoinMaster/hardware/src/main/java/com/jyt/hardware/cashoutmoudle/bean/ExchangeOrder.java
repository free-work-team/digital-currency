package com.jyt.hardware.cashoutmoudle.bean;

/**
 * coinbase下单记录
 *
 * @author huoChangGuo
 * @since 2019/9/3
 */
public class ExchangeOrder {
    private String transId;
    private String id;
    private String price;
    private String size;
    private String productId;
    private String side;
    private String type;
    private String timeInForce;
    private String createdAt;
    private String createTime;
    private String fillFees;
    private String filledSize;
    private String executedValue;
    private String status;
    private String settled;
    private String message;
    private String funds;
    private Integer isUpload;
    private String terminalNo;
    private String cryptoCurrency;//加密币种
    private String currency;//币种

    public ExchangeOrder() {
        this.transId = "";
        this.id = "";
        this.size = "";
        this.price = "";
        this.productId = "";
        this.side = "";
        this.type = "";
        this.timeInForce = "";
        this.createdAt = "";
        this.createTime = "";
        this.fillFees = "";
        this.filledSize = "";
        this.executedValue = "";
        this.status = "";
        this.settled = "";
        this.message = "";
        this.funds = "";
        this.terminalNo = "";
        this.isUpload = 0;
        this.cryptoCurrency = "";
        this.currency = "";
    }

    public String getFunds() {
        return funds;
    }

    public void setFunds(String funds) {
        this.funds = funds;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(String timeInForce) {
        this.timeInForce = timeInForce;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFillFees() {
        return fillFees;
    }

    public void setFillFees(String fillFees) {
        this.fillFees = fillFees;
    }

    public String getFilledSize() {
        return filledSize;
    }

    public void setFilledSize(String filledSize) {
        this.filledSize = filledSize;
    }

    public String getExecutedValue() {
        return executedValue;
    }

    public void setExecutedValue(String executedValue) {
        this.executedValue = executedValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSettled() {
        return settled;
    }

    public void setSettled(String settled) {
        this.settled = settled;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(Integer isUpload) {
        this.isUpload = isUpload;
    }

    public String getTerminalNo() {
        return terminalNo;
    }

    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "{" +
                "  trans_id:'" + transId + '\'' +
                ", terminal_no:'" + terminalNo + '\'' +
                ", id:'" + id + '\'' +
                ", price:'" + price + '\'' +
                ", size:'" + size + '\'' +
                ", funds:'" + funds + '\'' +
                ", product_id:'" + productId + '\'' +
                ", side:'" + side + '\'' +
                ", type:'" + type + '\'' +
                ", time_in_force:'" + timeInForce + '\'' +
                ", created_at:'" + createdAt + '\'' +
                ", create_time:'" + createTime + '\'' +
                ", fill_fees:'" + fillFees + '\'' +
                ", filled_size:'" + filledSize + '\'' +
                ", executed_value:'" + executedValue + '\'' +
                ", status:'" + status + '\'' +
                ", settled:'" + settled + '\'' +
                ", message:'" + message + '\'' +
                ", crypto_currency:'" + cryptoCurrency + '\'' +
                ", currency:'" + currency + '\'' +
                ", is_upload:'" + isUpload + '\'' +
                '}';
    }

    public String getCryptoCurrency() {
        return cryptoCurrency;
    }

    public void setCryptoCurrency(String cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
    }
}
