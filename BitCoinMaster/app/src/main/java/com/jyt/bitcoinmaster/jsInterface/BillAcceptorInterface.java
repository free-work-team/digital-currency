package com.jyt.bitcoinmaster.jsInterface;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.alibaba.fastjson.JSONObject;
import com.jyt.bitcoinmaster.cashAcceptor.CashAcceptorFactory;
import com.jyt.bitcoinmaster.cashAcceptor.ICashAcceptor;
import com.jyt.bitcoinmaster.email.SendMailUtil;
import com.jyt.bitcoinmaster.statics.Setting;
import com.jyt.bitcoinmaster.timer.UploadTimer;
import com.jyt.hardware.billacceptor.listener.DevEventListener;
import com.jyt.hardware.cashoutmoudle.Sqlite.DBHelper;
import com.jyt.hardware.cashoutmoudle.bean.CashBoxHistory;
import com.jyt.hardware.cashoutmoudle.bean.HardwareConfig;
import com.jyt.hardware.cashoutmoudle.bean.ParamSetting;
import com.jyt.hardware.cashoutmoudle.bean.WithdrawLog;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class BillAcceptorInterface implements DevEventListener {

    private static Logger log = Logger.getLogger("BitCoinMaster");


    private Context context;
    private WebView webView;
    private ICashAcceptor iCashAcceptor;
    private static final int SETDEVICEENABLE = 1;
    private static final int SETDEVICEEPAY = 8;

    // 钞箱状态
    private static final int CASHBOX_REMOVED = 0;
    private static final int CASHBOX_REPLACED = 1;

    private final int TIMEOUT_RANGE = 2;//h

    // 钱包余额不足，发送邮件
    private static final String INSUFFICIENT_BALANCE_TITLE = "Insufficient Balance";
    private static final String INSUFFICIENT_BALANCE_CONTENT = " Wallet balance insufficient.Please deal with it in time";


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SETDEVICEENABLE:
                    String data = (String) msg.obj;
//                    log.info("[BillAcceptorInterface]: callResult:"+data);
                    webView.evaluateJavascript("javascript:setDeviceEnableCallBack('" + data + "')",null);
                    break;
                case SETDEVICEEPAY:
                    String dealResult = (String) msg.obj;
//                    log.info("[PayoutInterface]: dealResult:" + dealResult);
                    webView.evaluateJavascript("javascript:getDealResult('" + dealResult + "')",null);
                    break;
                default:
                    break;
            }
        }
    };

    public BillAcceptorInterface(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
    }

    /**
     * connnect devices
     */
    @JavascriptInterface
    public void connDevices() {
        log.info("----------------正在连接纸币接收模块");
        this.iCashAcceptor = CashAcceptorFactory.getCashAcceptor(context, this);
        String res = iCashAcceptor.connDevices();
        if (StringUtils.isNotEmpty(res)) {
            if ("success".equals(res)) {
                devEventResult(SETDEVICEENABLE, 0, "already", "");
            } else if ("fail".equals(res)) {
                devEventResult(SETDEVICEENABLE, -1, "No USB connection detected!", "");
            }
        }
    }

    /**
     * init devices
     */
    @JavascriptInterface
    public void initDevices() {
        log.info("----------------初始化纸币接收模块参数");
        iCashAcceptor.initDevices();
    }

    /**
     * 入钞页面前端处理
     */
    @JavascriptInterface
    public void dealPutMoney(int addCount, int currentCount, boolean noLimit, final int maxTrade, final boolean haveSent,final String cryptoCurrency) {
        log.info("处理放钱页面------------start:addCount=" + addCount + ",currentCount=" + currentCount + ",cryptoCurrency=" + cryptoCurrency);
        try {
            if (addCount > 0) {
                currentCount = currentCount + addCount;
                log.info("识别区入钞=" + addCount + "，总入钞=" + currentCount);
                if (!noLimit) {
                    int limitCash = Integer.valueOf(Setting.limitCash);
                    if (currentCount > limitCash) {
                        //超出退钱
                        setEscrowAction(false);
                        log.info("KYC The maximum transaction amount is " + limitCash);
                        return;
                    }
                }
                // 当热钱包预存比特币时，判断数量
                if (DBHelper.getHelper().queryParamInfo().getCryptoSettings().getCryptoSetting(cryptoCurrency).getExchangeStrategy() != 2) {
                    if (new BigDecimal(currentCount).compareTo(new BigDecimal(maxTrade)) == 1) {
                        //超出退钱
                        setEscrowAction(false);
                        log.info(currentCount + " 超出钱包余额允许的最大交易金额 " + maxTrade);
                        // 发邮件提醒商户
                        final int finalCurrentCount = currentCount;
                        if (!haveSent) {
                            if (StringUtils.isNotEmpty(Setting.email) && StringUtils.isNotEmpty(Setting.terminalNo)) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String emailContent = "Current:" + finalCurrentCount + ",Max:" + maxTrade + ",No." + Setting.terminalNo +" "+cryptoCurrency+ INSUFFICIENT_BALANCE_CONTENT;
                                        SendMailUtil.send(Setting.email, INSUFFICIENT_BALANCE_TITLE, emailContent);
                                    }
                                }).start();
                            } else {
                                log.info("钱包余额不足 发送邮件失败,参数 email 或 terminalNo 为空");
                            }
                        }
                        return;
                    }
                }
                //没有限制，直接入钞
                setEscrowAction(true);
            }
        } catch (Exception e) {
            setEscrowAction(false);
            log.error("处理放钱页面------------fail", e);
        }
    }
    @JavascriptInterface
    public boolean checkFeeAndMaxTrade(final String maxTrade,final String minFee,final String cryptoCurrency){
        boolean res = true;
        // 当热钱包预存比特币时，判断持有余额是否满足保底手续费
        if (DBHelper.getHelper().queryParamInfo().getCryptoSettings().getCryptoSetting(cryptoCurrency).getExchangeStrategy() != 2) {
            if ( new BigDecimal(maxTrade).compareTo(new BigDecimal(minFee)) != 1 ) {
                res = false;
                log.info(" 商户持有虚拟币余额"+maxTrade+"不满足保底手续费 " + minFee);
                // 发邮件提醒商户
                    if (StringUtils.isNotEmpty(Setting.email) && StringUtils.isNotEmpty(Setting.terminalNo)) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String emailContent = "MaxTrade:" + maxTrade + ",MinFee:" + minFee + ",No." + Setting.terminalNo +" "+cryptoCurrency+ INSUFFICIENT_BALANCE_CONTENT;
                                SendMailUtil.send(Setting.email, INSUFFICIENT_BALANCE_TITLE, emailContent);
                            }
                        }).start();
                    } else {
                        log.info("钱包余额不足 发送邮件失败,参数 email 或 terminalNo 为空");
                    }
            }
        }
        return res;
    }



    /**
     * Device enable/disable toggle
     *
     * @param enable
     */
    @JavascriptInterface
    public void setDeviceEnable(boolean enable) {
        log.info("============纸币接收模块: " + (enable?"开灯":"关灯"));
        iCashAcceptor.setDeviceEnable(enable);
    }

    /**
     * Set Accept a bill from escrow
     */
    @JavascriptInterface
    public void setEscrowAction(boolean isAction) {
        log.info("============纸币接收模块: " + (isAction?"入钞":"拒钞"));
        iCashAcceptor.setEscrowAction(isAction);
    }

    /**
     * 获取缓存余额
     *
     * @return
     */
    @JavascriptInterface
    public int getEscrowLast() {
        return iCashAcceptor.getEscrowLast();
    }

    /**
     * 获取面额
     */
    @JavascriptInterface
    public String getCurrencyList() {
        return iCashAcceptor.getCurrencyList();
    }

    /**
     * 获取面额
     */
    @JavascriptInterface
    public int getMaxInsert() {
        return iCashAcceptor.getMaxInsert();
    }

    /**
     * 设置入钞Cashbox
     */
    @JavascriptInterface
    public void setCB(String psCount) {
        iCashAcceptor.setCB(psCount);
        //跟数据库PayoutStore同步
        HardwareConfig psConfig = DBHelper.getHelper().queryKeyEixst("PayoutStore");
        if (psConfig != null) {
            if (StringUtils.isNotEmpty(psConfig.getHwValue()) && psConfig.getHwValue().equals(psCount)) {
                // 存储PS面额到数据库
                List<HardwareConfig> hardwareConfigs = new ArrayList<>();
                HardwareConfig hardwareConfig = new HardwareConfig();
                hardwareConfig.setHwKey("PayoutStore");
                hardwareConfig.setHwValue("");
                hardwareConfigs.add(hardwareConfig);
                DBHelper.getHelper().saveHardwareConfig(hardwareConfigs);
            }
        }
    }

    /**
     * 设置入钞PayoutStore
     */
    @JavascriptInterface
    public void setPS(String psCount,int index) {
        iCashAcceptor.setPS(psCount,index);
    }

    /**
     * 清机，清除缓存中到钞箱内a
     */
    @JavascriptInterface
    public void payEmpty() {
        iCashAcceptor.payEmpty();
    }

    /**
     * 通用处理扫描结果
     * 1.解析扫描结果
     * 2.判断卖币方式
     * 3.通过地址和code匹配到订单
     *
     * @param result
     */
    @JavascriptInterface
    public void commonDealSacllerResult(String result) {
        // 不是我们的码，无法提现
        if (!(result.contains("bitcoin:") || result.contains("ethereum:")) || !result.contains("?amount=")) {
            devEventResult(false, "The QR code does not exist");
            return;
        }
        // 扫描结果解析
        String address = "";
        if (result.contains("bitcoin:")) {
            address = (result.split("bitcoin:")[1]).split("[?]")[0];
        }
        if (result.contains("ethereum:")) {
            address = (result.split("ethereum:")[1]).split("[?]")[0];
        }
        String amount = result.split("amount=")[1];
        amount = BigDecimal.valueOf(Double.valueOf(amount)).multiply(BigDecimal.valueOf(100000000)).setScale(0).toString();
        synchronized (this) {
            WithdrawLog withdrawlog = DBHelper.getHelper().queryWithdrawByAddress(address, amount);
            dealScanllerTransId(withdrawlog);
        }
    }

    /**
     * 处理扫描结果
     * 1.判断数据库交易信息是否存在
     * 2.判断订单是否已经取现
     * 3.查看确认状态，是否可取现
     * 4.是否能出钞，更新数据库交易信息的取现状态并出钞
     *
     * @param tranObj
     */
    private void dealScanllerTransId(WithdrawLog tranObj) {
        String message = "";
        Boolean isSuccess = false;
        // 判断数据库交易信息是否存在
        if (tranObj != null) {
            // 判断订单是否已经取现
            if (tranObj.getRedeemStatus() == 0) {
                int tranCash = Integer.parseInt(tranObj.getCash());
                // 改版通过确认状态直接判断
                boolean isFinish = tranObj.getConfirmStatus() == 1;
                if (isFinish) {
                    //判断是否出钞
                    if (iCashAcceptor.checkOutCash(tranCash)) {
                        // 后出钞，先更新数据库，避免损失
                        boolean updateResult = updateWithdraw(tranObj.getTransId(), 1, tranCash);
                        // 出钞
                        if (updateResult) {
                            try {
                                iCashAcceptor.pay(tranObj.getCash());
                                isSuccess = true;
                                log.info("出钞指令已发起,总计:" + tranObj.getCash());
                            } catch (Exception e) {
                                isSuccess = false;
                                message = e.getMessage();
                                log.error("出钞异常,", e);
                            }
                        } else {
                            message = "Database update exception";
                        }
                    } else {
                        message = "Insufficient cashBox balance,Please contact the administrator!";
                    }
                } else {
                    message = "The transaction was not confirmed, try again later !";
                    if (tranObj.getTransStatus() == 0) {
                        message = "The transaction was not paid !";
                    }
                }
            } else {
                message = "The cash has been taken";
            }
        } else {
            message = "The transaction does not exist";
        }
        devEventResult(isSuccess, message);
    }

    @Override
    public void devEventResult(int what, int code, String eventValues, String realValues) {
//        log.info("[devEventResult]:" + "code=" + code + ",eventValues=" + eventValues + ",realValues=" + realValues);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("eventValues", eventValues);
        jsonObject.put("realValues", realValues);
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = JSONObject.toJSONString(jsonObject);
        handler.sendMessage(msg);

        // 钞箱变动
        if ("Cashbox replaced".equals(eventValues)) {
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            uploadCashBoxHistory(CASHBOX_REPLACED);
                        }
                    }
            ).start();
        }
        if ("Cashbox removed".equals(eventValues)) {
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            uploadCashBoxHistory(CASHBOX_REMOVED);
                        }
                    }
            ).start();
        }
    }

    @Override
    public void devEventResult(boolean success, String message) {
//        log.info("[devEventResult]:" + "success=" + success + ",msg=" + message);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", success);
        jsonObject.put("message", message);
        Message msg = Message.obtain();
        msg.what = SETDEVICEEPAY;
        msg.obj = JSONObject.toJSONString(jsonObject);
        handler.sendMessage(msg);
    }

    // 更细提现记录
    private boolean updateWithdraw(String transId, int redeemStatus, int outCount) {
        return DBHelper.getHelper().updateWithdraw(transId, redeemStatus, outCount);
    }

    /**
     * 上传钞箱动作
     *
     * @param cashBoxAction
     */
    private void uploadCashBoxHistory(int cashBoxAction) {
        CashBoxHistory cashBoxHistory = new CashBoxHistory();
        try {
            ParamSetting paramSetting = DBHelper.getHelper().queryParamInfo();
            cashBoxHistory.setStatus(cashBoxAction);
            cashBoxHistory.setTerminalNo(paramSetting.getTerminalNo());
            cashBoxHistory.setCreateTime(new Date());
            UploadTimer.uploadCashBoxInfo(cashBoxHistory);
        } catch (Exception e) {
            log.error("上传钞箱操作(取出/替换)信息失败", e);
        }
    }

    /**
     * 判断数据库的交易时间是否过期
     *
     * @param transTime
     * @return
     */
    private boolean isTimeout(String transTime) {
        Date transDateTime = null;
        try {
            transDateTime = stringToDate(transTime);
        } catch (ParseException e) {
            e.printStackTrace();
            log.info("订单过期时间str转换失败");
            return true;
        }
        Date transTimeOutDate = getTimeOutDateTime(transDateTime);
        Date currentDateTime = new Date();
        return currentDateTime.after(transTimeOutDate);
    }

    /**
     * str转date "2019-04-30 09:24:28"
     *
     * @param dateStr
     */
    private Date stringToDate(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(dateStr);
    }

    /**
     * 获取过期时间
     *
     * @param transTime
     * @return
     */
    private Date getTimeOutDateTime(Date transTime) {
        Calendar c = Calendar.getInstance();
        c.setTime(transTime);
        c.add(Calendar.HOUR_OF_DAY, TIMEOUT_RANGE);
        Date newDate = c.getTime();
        log.info("该订单过期时间为：" + newDate);
        return newDate;
    }
}
