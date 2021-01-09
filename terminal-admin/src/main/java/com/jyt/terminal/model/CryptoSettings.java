package com.jyt.terminal.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * 加密设置
 * @className CryptoSettings
 * @author wangwei
 * @date 2020年1月16日
 *
 */
@TableName("t_crypto_settings")
public class CryptoSettings extends Model<CryptoSettings> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    /**
     *  名称
     */
    @TableField(value = "name")
    private String name;
    /**
     *  虚拟币
     */
    @TableField(value = "virtual_currency")
    private Integer virtualCurrency;
    /**
     *  状态
     */
    @TableField(value = "status")
    private Integer status;
    /**
     * 渠道参数
     */
    @TableField(value="channel_param")
    private String channelParam;
    /**
     * 交易策略
     */
    @TableField(value="exchange_strategy")
    private Integer exchangeStrategy;
    /**
     * 交易所
     */
    @TableField(value="exchange")
    private Integer exchange;
    /**
     * 钱包
     */
    @TableField(value="hot_wallet")
    private Integer hotWallet;
    /**
     * 确认方式
     */
    @TableField(value="confirmations")
    private Integer confirmations;
    /**
     * 币费率来源
     */
    @TableField(value="rate_source")
    private Integer rateSource;
    /**
     * 比特币汇率
     */
    private BigDecimal price;
    /**
     * 最低交易金额
     */
    @TableField("min_need_cash")
    private BigDecimal minNeedCash;
    /**
     * 购买手续费
     */
    @TableField("buy_transaction_fee")
    private BigDecimal buyTransactionFee;
    /**
     * 购买手续费(单笔)
     */
    @TableField("buy_single_fee")
    private BigDecimal buySingleFee;
    /**
     * 出售手续费
     */
    @TableField("sell_transaction_fee")
    private BigDecimal sellTransactionFee;
    /**
     * 出售手续费(单笔)
     */
    @TableField("sell_single_fee")
    private BigDecimal sellSingleFee;
    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 修改人
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getVirtualCurrency() {
		return virtualCurrency;
	}

	public void setVirtualCurrency(Integer virtualCurrency) {
		this.virtualCurrency = virtualCurrency;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getChannelParam() {
		return channelParam;
	}

	public void setChannelParam(String channelParam) {
		this.channelParam = channelParam;
	}

	public Integer getExchangeStrategy() {
		return exchangeStrategy;
	}

	public void setExchangeStrategy(Integer exchangeStrategy) {
		this.exchangeStrategy = exchangeStrategy;
	}

	public Integer getExchange() {
		return exchange;
	}

	public void setExchange(Integer exchange) {
		this.exchange = exchange;
	}

	public Integer getHotWallet() {
		return hotWallet;
	}

	public void setHotWallet(Integer hotWallet) {
		this.hotWallet = hotWallet;
	}

	public Integer getConfirmations() {
		return confirmations;
	}

	public void setConfirmations(Integer confirmations) {
		this.confirmations = confirmations;
	}

	public Integer getRateSource() {
		return rateSource;
	}

	public void setRateSource(Integer rateSource) {
		this.rateSource = rateSource;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getMinNeedCash() {
		return minNeedCash;
	}

	public void setMinNeedCash(BigDecimal minNeedCash) {
		this.minNeedCash = minNeedCash;
	}

	public BigDecimal getBuyTransactionFee() {
		return buyTransactionFee;
	}

	public void setBuyTransactionFee(BigDecimal buyTransactionFee) {
		this.buyTransactionFee = buyTransactionFee;
	}

	public BigDecimal getBuySingleFee() {
		return buySingleFee;
	}

	public void setBuySingleFee(BigDecimal buySingleFee) {
		this.buySingleFee = buySingleFee;
	}

	public BigDecimal getSellTransactionFee() {
		return sellTransactionFee;
	}

	public void setSellTransactionFee(BigDecimal sellTransactionFee) {
		this.sellTransactionFee = sellTransactionFee;
	}

	public BigDecimal getSellSingleFee() {
		return sellSingleFee;
	}

	public void setSellSingleFee(BigDecimal sellSingleFee) {
		this.sellSingleFee = sellSingleFee;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
