package com.jyt.terminal.model;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 短信发送表
 * </p>
 *
 * @author stylefeng
 * @since 2018-11-06
 */
@TableName("T_SMS_SEND")
public class SmsSend extends Model<SmsSend> {

    private static final long serialVersionUID = 1L;

    /**
     * 短信编号
     */
    @TableId(value = "SMS_ID", type = IdType.AUTO)
    private Long smsId;
    /**
     * 商户id
     */
    @TableField("MERCHANT_ID")
    private String merchantId;
    /**
     * 订单号
     */
    @TableField("ORDER_NO")
    private String orderNo;
    /**
     * 短信验证编号
     */
    @TableField("SMS_AUTH_ID")
    private Long smsAuthId;
    /**
     * 手机号
     */
    @TableField("MOBILE")
    private String mobile;
    /**
     * 渠道编号
     */
    @TableField("CHANNEL_NO")
    private String channelNo;
    /**
     * 短信内容
     */
    @TableField("SMS_CONTENT")
    private String smsContent;
    /**
     * 申请时间
     */
    @TableField("SEND_TIME")
    private Date sendTime;
    /**
     * 验证确认时间
     */
    @TableField("CONFIRM_TIME")
    private Date confirmTime;
    /**
     * 发送状态：1：成功 ; 2：失败;3：发送中
     */
    @TableField("TRADE_STATUS")
    private Integer tradeStatus;
    /**
     * 返回码
     */
    @TableField("RET_CODE")
    private Integer retCode;
    /**
     * 返回详情
     */
    @TableField("RET_INFO")
    private String retInfo;
    /**
     * 渠道商户ID
     */
    @TableField("CH_MERCHANT_ID")
    private String chMerchantId;
    /**
     * 渠道返回的订单号
     */
    @TableField("CH_TRADE_NO")
    private String chTradeNo;
    /**
     * 上游返回状态
     */
    @TableField("CH_STATE")
    private String chState;
    /**
     * 渠道返回码
     */
    @TableField("CH_RET_CODE")
    private String chRetCode;
    /**
     * 渠道返回详情
     */
    @TableField("CH_RET_INFO")
    private String chRetInfo;
    /**
     * 发往上游的流水号
     */
    @TableField("CH_SEND_SN")
    private String chSendSn;


    public Long getSmsId() {
        return smsId;
    }

    public void setSmsId(Long smsId) {
        this.smsId = smsId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getSmsAuthId() {
        return smsAuthId;
    }

    public void setSmsAuthId(Long smsAuthId) {
        this.smsAuthId = smsAuthId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public String getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(String smsContent) {
        this.smsContent = smsContent;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

    public Integer getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(Integer tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public Integer getRetCode() {
        return retCode;
    }

    public void setRetCode(Integer retCode) {
        this.retCode = retCode;
    }

    public String getRetInfo() {
        return retInfo;
    }

    public void setRetInfo(String retInfo) {
        this.retInfo = retInfo;
    }

    public String getChMerchantId() {
        return chMerchantId;
    }

    public void setChMerchantId(String chMerchantId) {
        this.chMerchantId = chMerchantId;
    }

    public String getChTradeNo() {
        return chTradeNo;
    }

    public void setChTradeNo(String chTradeNo) {
        this.chTradeNo = chTradeNo;
    }

    public String getChState() {
        return chState;
    }

    public void setChState(String chState) {
        this.chState = chState;
    }

    public String getChRetCode() {
        return chRetCode;
    }

    public void setChRetCode(String chRetCode) {
        this.chRetCode = chRetCode;
    }

    public String getChRetInfo() {
        return chRetInfo;
    }

    public void setChRetInfo(String chRetInfo) {
        this.chRetInfo = chRetInfo;
    }

    public String getChSendSn() {
        return chSendSn;
    }

    public void setChSendSn(String chSendSn) {
        this.chSendSn = chSendSn;
    }

    @Override
    protected Serializable pkVal() {
        return this.smsId;
    }

    @Override
    public String toString() {
        return "SmsSend{" +
        "smsId=" + smsId +
        ", merchantId=" + merchantId +
        ", orderNo=" + orderNo +
        ", smsAuthId=" + smsAuthId +
        ", mobile=" + mobile +
        ", channelNo=" + channelNo +
        ", smsContent=" + smsContent +
        ", sendTime=" + sendTime +
        ", confirmTime=" + confirmTime +
        ", tradeStatus=" + tradeStatus +
        ", retCode=" + retCode +
        ", retInfo=" + retInfo +
        ", chMerchantId=" + chMerchantId +
        ", chTradeNo=" + chTradeNo +
        ", chState=" + chState +
        ", chRetCode=" + chRetCode +
        ", chRetInfo=" + chRetInfo +
        ", chSendSn=" + chSendSn +
        "}";
    }
}
