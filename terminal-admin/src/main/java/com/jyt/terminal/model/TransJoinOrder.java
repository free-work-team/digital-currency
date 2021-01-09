package com.jyt.terminal.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * <p>
 * 比特币购买流水表实体类
 * </p>
 *
 * @author sunshubin
 * @since 2019-04-29
 */

public class TransJoinOrder  {

    private static final long serialVersionUID = 1L;


    /**
     * 加密货币
     */
    private String cryptoCurrency;
    
    /**
     * 交易比特币量
     */
    private String amount;
    
    /**
     * 交易金额（现金）
     */
    private String cash;
    
    /**
     * 商户手续费
     */
    private String fee;
    
    /**
     * 交易时比特币价格
     */
    private String price;
    

    /**
     * 平台手续费
     */
    private String cFee;
    
    private String transId;
    
    /**
     * 状态
     */
    private String status;
    /**
     * 
     */
    private String funds;
    private String executedValue;
    private String fillFees;
    private String tranCurrency;
    private String orderCurrency;
    
	public String getCryptoCurrency() {
		return cryptoCurrency;
	}
	public void setCryptoCurrency(String cryptoCurrency) {
		this.cryptoCurrency = cryptoCurrency;
	}
	public String getTranCurrency() {
		return tranCurrency;
	}
	public void setTranCurrency(String tranCurrency) {
		this.tranCurrency = tranCurrency;
	}
	public String getOrderCurrency() {
		return orderCurrency;
	}
	public void setOrderCurrency(String orderCurrency) {
		this.orderCurrency = orderCurrency;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getCash() {
		return cash;
	}
	public void setCash(String cash) {
		this.cash = cash;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	
	public String getcFee() {
		return cFee;
	}
	public void setcFee(String cFee) {
		this.cFee = cFee;
	}
	public String getTransId() {
		return transId;
	}
	public void setTransId(String transId) {
		this.transId = transId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFunds() {
		return funds;
	}
	public void setFunds(String funds) {
		this.funds = funds;
	}
	public String getExecutedValue() {
		return executedValue;
	}
	public void setExecutedValue(String executedValue) {
		this.executedValue = executedValue;
	}
	public String getFillFees() {
		return fillFees;
	}
	public void setFillFees(String fillFees) {
		this.fillFees = fillFees;
	}
    
	

	

	
}
