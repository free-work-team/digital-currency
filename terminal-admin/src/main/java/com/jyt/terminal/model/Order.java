package com.jyt.terminal.model;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * <p>
 * 比特币订单表表实体类
 * </p>
 *
 * @author sunshubin
 * @since 2019-09-19
 */
@TableName("t_order")
public class Order extends Model<Order> {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@TableId(value = "trans_id", type = IdType.INPUT)
	private String transId;// 终端交易流水id

	@TableField("order_id")
	private String id; // 平台交易id
	/**
     * 加密币种
     */
    @TableField("crypto_currency")
    private String cryptoCurrency;
	
	private String size; // btc数量

	@TableField("product_id")
	private String productId; // 产品id BTC-USD

	private String side; // 买或 卖

	private String type; // limit / market 限价还是实时价格

	@TableField("created_at")
	private String createdAt; // 创建时间（上游返回时间）

	@TableField("fill_fees")
	private String fillFees; // 手续费单位USD

	@TableField("filled_size")
	private String filledSize; // 购买的数量BTC

	@TableField("executed_value")
	private String executedValue; // 购买的实际金额USD

	private String status;// 状态

	private String message; // 错误信息

	private String funds;// 购买的USD数量

	@TableField("terminal_no")
	private String terminalNo;// 终端号

	private String price;

	@TableField("create_time")
	private String createTime;// 创建时间（当地时间）
	/**
	 * 修改时间
	 */
	@TableField("update_time")
	private Date updateTime;
	/**
	 * 法币币种
	 */
	@TableField("currency")
	private String currency;
	public String getCryptoCurrency() {
		return cryptoCurrency;
	}

	public void setCryptoCurrency(String cryptoCurrency) {
		this.cryptoCurrency = cryptoCurrency;
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

	public String getTerminalNo() {
		return terminalNo;
	}

	public void setTerminalNo(String terminalNo) {
		this.terminalNo = terminalNo;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFunds() {
		return funds;
	}

	public void setFunds(String funds) {
		this.funds = funds;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
