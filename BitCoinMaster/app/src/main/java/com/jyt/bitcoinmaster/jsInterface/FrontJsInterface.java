package com.jyt.bitcoinmaster.jsInterface;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jyt.bitcoinmaster.email.SendMailUtil;
import com.jyt.bitcoinmaster.exchange.Entity.MyResponse;
import com.jyt.bitcoinmaster.rate.IRate;
import com.jyt.bitcoinmaster.rate.entity.QueryPriceRequest;
import com.jyt.bitcoinmaster.rate.entity.QueryPriceResult;
import com.jyt.bitcoinmaster.rate.RateFactory;
import com.jyt.hardware.cashoutmoudle.bean.CryptoSettings;
import com.jyt.hardware.cashoutmoudle.enums.*;
import com.jyt.bitcoinmaster.exchange.Entity.BuyParam;
import com.jyt.bitcoinmaster.exchange.ExchangeFactory;
import com.jyt.bitcoinmaster.statics.Setting;
import com.jyt.bitcoinmaster.wallet.IWallet;
import com.jyt.bitcoinmaster.wallet.WalletFactory;
import com.jyt.bitcoinmaster.wallet.entity.CreateAddressRequest;
import com.jyt.bitcoinmaster.wallet.entity.CreateAddressResult;
import com.jyt.bitcoinmaster.wallet.entity.QueryBalanceRequest;
import com.jyt.bitcoinmaster.wallet.entity.QueryBalanceResult;

import com.jyt.bitcoinmaster.wallet.entity.SendCoinRequest;
import com.jyt.bitcoinmaster.wallet.entity.SendCoinResult;
import com.jyt.hardware.cashoutmoudle.Sqlite.DBHelper;

import com.jyt.hardware.cashoutmoudle.bean.ParamSetting;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 前端用户访问interface
 * @since 2019/11/21
 */

public class FrontJsInterface {
    private ScheduledExecutorService threadExecutor = Executors.newScheduledThreadPool(4);

    private static Logger log = Logger.getLogger("BitCoinMaster");

    private WebView webView;

    private Context context;

    // 钱包余额不足，发送邮件
    private static final String INSUFFICIENT_BALANCE_TITLE = "Insufficient Balance";
    private static final String INSUFFICIENT_BALANCE_CONTENT = " device bitcoin balance insufficient.Please deal with it in time";


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String data = ((String[]) msg.obj)[0];
            String requestType = ((String[]) msg.obj)[1];
            webView.evaluateJavascript("javascript:frontResp(" + data + ",'" + requestType + "')",null);
        }
    };

    public FrontJsInterface(Context context, WebView webView) {
        this.webView = webView;
        this.context = context;
    }

    /**
     * 买币（异步）
     * receiveAddress 接收地址
     * amount         比特币金额（聪）
     * fee            商户手续费（聪）
     * currentPrice   当前市场价格
     * cash           购买现金
     */
    @JavascriptInterface
    public void buyCoins(final String receiveAddress,final String amount,final String fee,final String currentPrice,final String cash, final String customerId, final String exchangeRate,final String cryptoCurrency) {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[2];
        final String methodName = e.getMethodName();
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject resultJSON = new JSONObject();
                final String transId = System.currentTimeMillis()+Setting.terminalNo.substring(1);
                ContentValues values = commonBuyValues(receiveAddress, amount, currentPrice, fee, cash,transId,customerId,exchangeRate,cryptoCurrency);
                final CryptoSettings cryptoSetting = Setting.cryptoSettings.getCryptoSetting(cryptoCurrency);
                IWallet wallet = WalletFactory.getWallet(cryptoSetting.getHotWallet());
                if(0 == cryptoSetting.getExchangeStrategy() || 1 == cryptoSetting.getExchangeStrategy()){
                    //0.不使用交易所，从热钱包发送和接收币；
                    SendCoinRequest request = new SendCoinRequest();
                    request.setCryptoCurrency(cryptoCurrency);
                    request.setAmount(amount);
                    request.setReceiveAddress(receiveAddress);
                    final SendCoinResult result = wallet.sendCoin(request);
                    resultJSON.put("code",result.getCode());
                    if(CodeMessageEnum.SUCCESS.getCode().equals(result.getCode())){
                        values.put("c_fee",result.getFee());
                        values.put("status",TranStatusEnum.CONFIRM.getValue()+"");
                        values.put("channel_trans_id",result.getTransId());

                        resultJSON.put("transDateTime",values.getAsString("trans_time"));
                        resultJSON.put("transId",transId);
                        resultJSON.put("amount",amount);
                        resultJSON.put("cash",cash);
                        if(1 == cryptoSetting.getExchangeStrategy()){
                            //1.热钱包预存币，从热钱包发送和接收币，然后在交易所买卖币，如果购买成功，把这些币发送到热钱包；
                            threadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    //交易所买币操作
                                    BuyParam buyParam = new BuyParam();
                                    buyParam.setTerminalNo(Setting.terminalNo);
                                    buyParam.setTransId(transId);
                                    String size = (new BigDecimal(amount).add(new BigDecimal(result.getFee()))).divide(new BigDecimal(100000000), 8, BigDecimal.ROUND_DOWN).toString();
                                    buyParam.setSize(size);
                                    buyParam.setPrice(exchangeRate);
                                    buyParam.setFunds(cash);
                                    buyParam.setCryptoCurrency(cryptoCurrency);
                                    buyParam.setCurrency(Setting.currency);
                                    MyResponse orderResult = ExchangeFactory.getExchange(cryptoSetting.getExchange()).buyOrder(buyParam);
                                    if(!orderResult.isSuccess()){
                                        // 交易所买币失败，更新buy表状态
                                        DBHelper.getHelper().updateBuyLogRemarkAndStatus(transId, orderResult.getMessage(),TranStatusEnum.ERROR.getValue()+"");
                                    }
                                }
                            });
                        }
                    }else{
                        values.put("status",TranStatusEnum.FAIL.getValue()+"");
                        values.put("remark",ErrorTypeEnum.TO_TRAN.getValue() +result.getMessage());
                        resultJSON.put("message",result.getMessage());
                    }
                }else if (2 == cryptoSetting.getExchangeStrategy()){
                    //交易所买币操作
                    BuyParam buyParam = new BuyParam();
                    buyParam.setTerminalNo(Setting.terminalNo);
                    buyParam.setTransId(transId);

                    String size = (new BigDecimal(amount).add( wallet.getReserveFee(cryptoCurrency))).divide(new BigDecimal(100000000), 8, BigDecimal.ROUND_DOWN).toString();
                    buyParam.setSize(size);
                    buyParam.setPrice(exchangeRate);
                    buyParam.setFunds(cash);
                    buyParam.setCryptoCurrency(cryptoCurrency);
                    buyParam.setCurrency(Setting.currency);
                    MyResponse orderResult = ExchangeFactory.getExchange(cryptoSetting.getExchange()).buyOrder(buyParam);
                    if(orderResult.isSuccess()){
                        // 成功
                        values.put("c_fee","");
                        values.put("status",TranStatusEnum.PENDING.getValue()+"");

                        resultJSON.put("code",CodeMessageEnum.SUCCESS.getCode());
                        resultJSON.put("transId",transId);
                        resultJSON.put("transDateTime",values.getAsString("trans_time"));
                        resultJSON.put("amount",amount);
                        resultJSON.put("cash",cash);
                    }else{
                        // 失败
                        values.put("status",TranStatusEnum.FAIL.getValue()+"");
                        values.put("remark", orderResult.getMessage());
                        values.put("fee","0"); //买币策略2失败了直接不收手续费

                        resultJSON.put("code",CodeMessageEnum.FAIL.getCode());
                        resultJSON.put("message",orderResult.getMessage());
                    }
                }
                //入库
                try {
                    DBHelper dbHelper= DBHelper.getInstance(context);
                    dbHelper.addBuyLog(values);
                }catch (Exception e){
                    log.info("买币 数据入库系统异常:{}",e);
                    resultJSON.put("code",CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
                    resultJSON.put("message",CodeMessageEnum.SYSTEM_EXCEPTION.getMessage());
                }
                frontResult(resultJSON.toJSONString(),methodName);


            }
        });
    }

    /**
     * 卖币（异步）
     * amount         比特币金额（聪）
     * fee            商户手续费（聪）
     * currentPrice   当前市场价格
     * cash           购买现金
     */
    @JavascriptInterface
    public void sellCoins(final String amount,final String fee,final String currentPrice,final String cash,final String customerId,final String exchangeRate,final String cryptoCurrency) {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[2];
        final String methodName = e.getMethodName();
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject resultJSON = new JSONObject();
                //1.创建地址
                IWallet wallet = WalletFactory.getWallet(Setting.cryptoSettings.getCryptoSetting(cryptoCurrency).getHotWallet());
                CreateAddressRequest request = new CreateAddressRequest();
                request.setCryptoCurrency(cryptoCurrency);
                CreateAddressResult result = wallet.createAddress(request);
                resultJSON.put("code",result.getCode());
                if(CodeMessageEnum.SUCCESS.getCode().equals(result.getCode())){
                    String address = result.getAddress();
                    //2.入库
                    String transId = System.currentTimeMillis()+Setting.terminalNo.substring(1);
                    ContentValues values = commonSellValues(address,amount,currentPrice,fee,cash,transId,customerId,exchangeRate,cryptoCurrency,result.getAddressId());
                    if(StringUtils.isNotBlank(result.getXpub())){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("xpub",result.getXpub());
                        values.put("ext_id",jsonObject.toJSONString());
                    }
                    try {
                        DBHelper dbHelper= DBHelper.getInstance(context);
                        dbHelper.addWithdrawLog(values);

                        resultJSON.put("address",address);
                        resultJSON.put("amount",amount);
                        resultJSON.put("transDateTime",values.getAsString("trans_time"));
                        resultJSON.put("cash",cash);
                        resultJSON.put("transId",transId);
                    }catch (Exception e){
                        log.info("卖币 数据入库系统异常:{}",e);
                        resultJSON.put("code",CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
                        resultJSON.put("message",CodeMessageEnum.SYSTEM_EXCEPTION.getMessage());
                    }
                }else{
                    resultJSON.put("message",result.getMessage());
                }
                frontResult(resultJSON.toJSONString(),methodName);
            }
        });
    }

    private ContentValues commonSellValues(String receiveAddress,String amount,String currentPrice,String fee,String cash,String transId,String customerId,String exchangeRate,String cryptoCurrency,String addressId){
        CryptoSettings cryptoSetting = Setting.cryptoSettings.getCryptoSetting(cryptoCurrency);
        ContentValues values = new ContentValues();
        values.put("trans_id",transId);
        values.put("terminal_no",Setting.terminalNo);
        values.put("target_address",receiveAddress);
        values.put("amount",amount);
        values.put("price",currentPrice);
        values.put("fee",fee);
        values.put("exchange_strategy",cryptoSetting.getExchangeStrategy());
        values.put("cash",cash);
        values.put("trans_status",TranStatusEnum.INIT.getValue());
        values.put("redeem_status",0);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String time = sdf.format(now);
        values.put("trans_time",time);
        values.put("outCount",0);
        values.put("is_upload",0);
        values.put("channel",cryptoSetting.getHotWallet());
        values.put("sell_type",cryptoSetting.getConfirmations());
        values.put("currency",Setting.currency);
        values.put("customer_id",customerId);
        values.put("exchange_rate",exchangeRate);
        values.put("crypto_currency",cryptoCurrency);
        values.put("address_id",addressId);

        if(ExchangeStrategyEnum.NO_EXCHANGE.getValue() !=cryptoSetting.getExchangeStrategy()){
            values.put("strategy",cryptoSetting.getExchange());
        }
        values.put("confirm_status",0);
        return  values;
    }

    /**
     * 查询市场价格（异步）
     */
    @JavascriptInterface
    public void queryMarketPriceAsyn(final String cryptoCurrency) {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[2];
        final String methodName = e.getMethodName();
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                QueryPriceResult result = getMarketPriceBySource(cryptoCurrency);
                frontResult(JSON.toJSONString(result),methodName);
            }
        });
    }

    /**
     * 查询市场价格（同步）
     */
    @JavascriptInterface
    public String queryMarketPrice(String cryptoCurrency) {
        QueryPriceResult result = getMarketPriceBySource(cryptoCurrency);
        return JSON.toJSONString(result);
    }

    /**
     * 根据汇率来源查汇率
     * @return
     */
    private QueryPriceResult getMarketPriceBySource(String cryptoCurrency) {
        JSONObject channelParam = JSONObject.parseObject(Setting.cryptoSettings.getCryptoSetting(cryptoCurrency).getChannelParam());
        Integer rateSource = getRateSource(cryptoCurrency);
        IRate iRate = RateFactory.getRate(rateSource);
        QueryPriceRequest request = new QueryPriceRequest();
        request.setCurrency(Setting.currency);
        request.setExchangeCurrency(ExchangeCurrencyEnum.getDesc(channelParam.getJSONObject("exchange").getString("currency_pair")));
        request.setCryptoCurrency(cryptoCurrency);
        QueryPriceResult priceResult = iRate.queryMarketPrice(request);
        if (StringUtils.isEmpty(priceResult.getPrice())){
            priceResult.setPrice("");
        }
        if (StringUtils.isEmpty(priceResult.getExchangeRate())){
            priceResult.setExchangeRate("");
        }
        log.info("[通过汇率来源] 查询币种汇率:"+priceResult.getPrice()+", 查询交易所汇率:"+priceResult.getExchangeRate());
        return priceResult;
    }


    /**
     * 查询商户余额（同步）
     */
    @JavascriptInterface
    public String queryBalance(String type,String cryptoCurrency) {
        IWallet wallet = WalletFactory.getWallet(Setting.cryptoSettings.getCryptoSetting(cryptoCurrency).getHotWallet());
        QueryBalanceRequest request = new QueryBalanceRequest();
        request.setCryptoCurrency(cryptoCurrency);
        request.setType(type);
        QueryBalanceResult result = wallet.queryBalance(request);
        return JSON.toJSONString(result);
    }

    /**
     * 查询商户加密货币设置
     */
    @JavascriptInterface
    public String queryCryptoCurrency() {
        JSONObject jsonObject = new JSONObject();

        if(Setting.cryptoSettings.getBtc() != null){
            jsonObject.put(CryptoCurrencyEnum.BTC.getValue(),true);
        }else{
            jsonObject.put(CryptoCurrencyEnum.BTC.getValue(),false);
        }

        if(Setting.cryptoSettings.getEth() != null){
            jsonObject.put(CryptoCurrencyEnum.ETH.getValue(),true);
        }else{
            jsonObject.put(CryptoCurrencyEnum.ETH.getValue(),false);
        }
        return JSON.toJSONString(jsonObject);
    }

    /**
     * 查询商户余额（异步）
     */
    @JavascriptInterface
    public void queryBalanceAsyn(final String type,final String cryptoCurrency) {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[2];
        final String methodName = e.getMethodName();
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                IWallet wallet = WalletFactory.getWallet(Setting.cryptoSettings.getCryptoSetting(cryptoCurrency).getHotWallet());
                QueryBalanceRequest request = new QueryBalanceRequest();
                request.setType(type);
                request.setSubReserveFee(true);
                QueryBalanceResult result = wallet.queryBalance(request);
                frontResult(JSON.toJSONString(result),methodName);
            }
        });
    }

    /**
     * 发送邮件
     */
    @JavascriptInterface
    public void sendEmail(String toEmail,String subject,String content) {
        SendMailUtil.send(toEmail,subject,content);
    }

    /**
     * 余额不足发的邮件
     * @param finalCurrentCount
     * @param maxTrade
     */
    @JavascriptInterface
    public void sendInsufficientBalanceEmail(int finalCurrentCount,int maxTrade) {
        ParamSetting paramSetting = DBHelper.getHelper().queryParamInfo();
        String emailContent = "Current:" + finalCurrentCount + ",Max:" + maxTrade + ",No." + paramSetting.getTerminalNo() + INSUFFICIENT_BALANCE_CONTENT;
        SendMailUtil.send(paramSetting.getEmail(), INSUFFICIENT_BALANCE_TITLE, emailContent);

    }


    /**
     * 系统设置是否kyc
     * @return
     */
    @JavascriptInterface
    public boolean getKycEnable() {
        if (Setting.kycEnable == null) {
            return DBHelper.getHelper().queryKycEnable() == 1;
        } else {
            return Setting.kycEnable == 1;
        }
    }

    /**
     * 系统设置汇率来源
     * @return
     */
    private Integer getRateSource(String cryptoCurrency) {
        Integer rateSource = Setting.cryptoSettings.getCryptoSetting(cryptoCurrency).getRateSource();
        if (rateSource == null) {
            return DBHelper.getHelper().queryRateSource();
        } else {
            return rateSource;
        }
    }


    private ContentValues commonBuyValues(String receiveAddress,String amount,String currentPrice,String fee,String cash,String transId,String customerId,String exchangeRate,String cryptoCurrency){
        CryptoSettings cryptoSetting = Setting.cryptoSettings.getCryptoSetting(cryptoCurrency);
        ContentValues values = new ContentValues();
        values.put("trans_id",transId);
        values.put("terminal_no",Setting.terminalNo);
        values.put("address",receiveAddress);
        values.put("amount",amount);
        values.put("price",currentPrice);
        values.put("fee",fee);
        values.put("exchange_strategy",cryptoSetting.getExchangeStrategy());
        values.put("cash",cash);
        values.put("is_upload",0);
        values.put("channel",cryptoSetting.getHotWallet());
        values.put("customer_id",customerId);
        values.put("exchange_rate",exchangeRate);
        values.put("crypto_currency",cryptoCurrency);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String time = sdf.format(now);
        values.put("trans_time",time);
        values.put("currency",Setting.currency);
        if(ExchangeStrategyEnum.NO_EXCHANGE.getValue() !=cryptoSetting.getExchangeStrategy()){
            values.put("strategy",cryptoSetting.getExchange());
        }
        return  values;
    }

    public void frontResult(String resp, String type) {
        Message msg = Message.obtain();
        String[] arr = new String[2];
        arr[0] = resp;
        arr[1] = type;
        msg.obj = arr;
        handler.sendMessage(msg);
    }
}
