package com.jyt.terminal.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

/**
 * <p>
 * 比特币交易流水统计表实体类
 * 
 */
@TableName("t_trade_statistics")
public class TradeStatistics extends Model<TradeStatistics> {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    @TableField("date")
    private String date;
    /**
     * 终端机号
     */
    @TableField("terminal_no")
    private String terminalNo;
    /**
     * 加密币种
     */
    @TableField("crypto_currency")
    private String cryptoCurrency;
    /**
     * 交易比特币量
     */
    private BigDecimal amount;
    
    /**
     * 交易比特币量
     */
    private BigDecimal cash;
    
    /**
     * 商户手续费
     */
    private BigDecimal fee;
    
    /**
     * 平台手续费
     */
    @TableField("c_fee")
    private BigDecimal cFee;
    
    
    /**
     * 交易方式(1:买，2：卖)
     */
    @TableField("trade_type")
    private Integer tradeType;

    /**
     * 利润
     */
    private BigDecimal profit;
    
    /**
     * 交易数
     */
    private Integer count;
    /**
     * 法币币种
     */
    @TableField("currency")
    private String currency;
    
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCryptoCurrency() {
		return cryptoCurrency;
	}

	public void setCryptoCurrency(String cryptoCurrency) {
		this.cryptoCurrency = cryptoCurrency;
	}

	public BigDecimal getCash() {
		return cash;
	}


	public void setCash(BigDecimal cash) {
		this.cash = cash;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public String getTerminalNo() {
		return terminalNo;
	}


	public void setTerminalNo(String terminalNo) {
		this.terminalNo = terminalNo;
	}


	public BigDecimal getAmount() {
		return amount;
	}


	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}


	public BigDecimal getFee() {
		return fee;
	}


	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}


	public BigDecimal getcFee() {
		return cFee;
	}


	public void setcFee(BigDecimal cFee) {
		this.cFee = cFee;
	}


	public Integer getTradeType() {
		return tradeType;
	}


	public void setTradeType(Integer tradeType) {
		this.tradeType = tradeType;
	}


	public BigDecimal getProfit() {
		return profit;
	}


	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}


	public Integer getCount() {
		return count;
	}


	public void setCount(Integer count) {
		this.count = count;
	}


	@Override
    protected Serializable pkVal() {
        return this.date;
    }

	
}
