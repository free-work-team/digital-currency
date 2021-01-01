package com.jyt.bitcoinmaster.jsInterface;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jyt.hardware.cashoutmoudle.enums.CodeMessageEnum;
import com.jyt.bitcoinmaster.exchange.Entity.InitParam;
import com.jyt.bitcoinmaster.exchange.ExchangeFactory;
import com.jyt.bitcoinmaster.listener.BusinessListener;
import com.jyt.bitcoinmaster.statics.Setting;
import com.jyt.bitcoinmaster.wallet.IWallet;
import com.jyt.bitcoinmaster.wallet.WalletFactory;
import com.jyt.bitcoinmaster.wallet.entity.InitRequest;
import com.jyt.bitcoinmaster.wallet.entity.InitResult;
import com.jyt.hardware.cashoutmoudle.Sqlite.DBHelper;
import com.jyt.hardware.cashoutmoudle.enums.CryptoCurrencyEnum;
import com.jyt.hardware.cashoutmoudle.enums.ExchangeStrategyEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


/**
 * admin后端interface
 */

public class BusinessJsInterface implements BusinessListener {

    private ScheduledExecutorService threadExecutor = Executors.newScheduledThreadPool(4);

    private static Logger log = Logger.getLogger("BitCoinMaster");

    private final int NET_CONNECT_COUNT = 18;

    private WebView webView;

    private Context context;

    public BusinessJsInterface(Context context, WebView webView) {
        this.webView = webView;
        this.context = context;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String data = ((String[]) msg.obj)[0];
            String requestType = ((String[]) msg.obj)[1];
            webView.evaluateJavascript("javascript:businessResp(" + data + ",'" + requestType + "')",null);
        }
    };

    @Override
    public void businessResult(String resp, String type) {
        Message msg = Message.obtain();
        String[] arr = new String[2];
        arr[0] = resp;
        arr[1] = type;
        msg.obj = arr;
        handler.sendMessage(msg);
    }

    /**
     * 检查网络
     */
    @JavascriptInterface
    public void checkNetWork() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[2];
        final String methodName = e.getMethodName();
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                boolean flag = isConnect(NET_CONNECT_COUNT);
                jsonObject.put("flag", flag);
                businessResult(jsonObject.toJSONString(), methodName);
            }
        });
    }
    /**
     * 钱包初始化
     */
    @JavascriptInterface
    public void getWalletInit() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[2];
        final String methodName = e.getMethodName();
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                InitResult result = null;
                JSONObject jsonObject = new JSONObject();

                if (Setting.cryptoSettings.getBtc() != null) {
                    IWallet btcWallet = WalletFactory.getWallet(Setting.cryptoSettings.getBtc().getHotWallet());
                    InitRequest request = new InitRequest();
                    request.setCryptoCurrency(CryptoCurrencyEnum.BTC.getValue());
                    result = btcWallet.init(request);
                    if (CodeMessageEnum.SUCCESS.getCode().equals(result.getCode())) {
                        jsonObject.put("flag", true);
                    } else {
                        jsonObject.put("flag", false);
                        businessResult(jsonObject.toJSONString(), methodName);
                        return;
                    }
                }
                if (Setting.cryptoSettings.getEth() != null) {
                    IWallet ethWallet = WalletFactory.getWallet(Setting.cryptoSettings.getEth().getHotWallet());
                    InitRequest request = new InitRequest();
                    request.setCryptoCurrency(CryptoCurrencyEnum.ETH.getValue());
                    result = ethWallet.init(request);
                    if (CodeMessageEnum.SUCCESS.getCode().equals(result.getCode())) {
                        jsonObject.put("flag", true);
                    } else {
                        jsonObject.put("flag", false);
                    }
                }
                businessResult(jsonObject.toJSONString(), methodName);
            }
        });
    }
    /**
     * 交易所初始化
     */
    @JavascriptInterface
    public void getExchangeInit() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[2];
        final String methodName = e.getMethodName();
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                if(Setting.cryptoSettings.getBtc() != null){
                    if(ExchangeStrategyEnum.NO_EXCHANGE.getValue() == Setting.cryptoSettings.getBtc().getExchangeStrategy()){
                        jsonObject.put("flag", true);
                    }else {
                        InitParam initParam = new InitParam();
                        initParam.setCryptoCurrency(CryptoCurrencyEnum.BTC.getValue());
                        jsonObject.put("flag", ExchangeFactory.getExchange(Setting.cryptoSettings.getBtc().getExchange()).init(initParam));
                    }
                    if (jsonObject.getBoolean("flag")){
                        log.info("交易所同步[btc] 成功!");
                    }else{
                        log.info("交易所同步[btc] 失败!");
                        businessResult(jsonObject.toJSONString(), methodName);
                        return;
                    }
                }
                if(Setting.cryptoSettings.getEth() != null){
                    if(ExchangeStrategyEnum.NO_EXCHANGE.getValue() == Setting.cryptoSettings.getEth().getExchangeStrategy()){
                        jsonObject.put("flag", true);
                    }else {
                        InitParam initParam = new InitParam();
                        initParam.setCryptoCurrency(CryptoCurrencyEnum.ETH.getValue());
                        jsonObject.put("flag", ExchangeFactory.getExchange(Setting.cryptoSettings.getEth().getExchange()).init(initParam));
                    }
                    if (jsonObject.getBoolean("flag")){
                        log.info("交易所同步[eth] 成功!");
                    }else{
                        log.info("交易所同步[eth] 失败!");
                    }
                }
                businessResult(jsonObject.toJSONString(), methodName);
            }
        });
    }

    private boolean isConnect(int count) {
        if (count == 0) {
            log.info("-------- No network connection！--------");
            Toast.makeText(context, "No network connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        log.info("-------- 第" + (NET_CONNECT_COUNT - count + 1) + "次链接网络 --------");
        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean internet = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean ethInternet = con.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET).isConnectedOrConnecting();
        if (wifi | internet | ethInternet) {
            //执行相关操作
            log.info("-------- 第" + (NET_CONNECT_COUNT - count + 1) + "次链接网络----------------成功");
            return true;
        } else {
            try {
                Thread.sleep(3000);
            }catch (Exception e){
                log.error("sleep",e);
            }
            return isConnect(count - 1);
        }
    }

    /**
     * 判断网络链接状态
     * @return
     */
    private boolean isConnect() {
        String result = null;
        try {
            String ip = "www.coinbase.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);// ping网址1次
            // 读取ping的内容，可以不加
//            InputStream input = p.getInputStream();
//            BufferedReader in = new BufferedReader(new InputStreamReader(input));
//            StringBuffer stringBuffer = new StringBuffer();
//            String content = "";
//            while ((content = in.readLine()) != null) {
//                stringBuffer.append(content);
//            }
//            log.info("------ping-----result content : " + stringBuffer.toString());
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);// ping网址1次
                status = p.waitFor();
                if (status == 0) {
                    result = "success";
                    return true;
                } else {
                    p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);// ping网址1次
                    status = p.waitFor();
                    if (status == 0) {
                        result = "success";
                        return true;
                    } else {
                        result = "failed";
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            log.info("------ping-----result = " + result);
        }
        return false;
    }

    /**
     * 获取当前钱包币种
     * @return
     */
    @JavascriptInterface
    public String getCurrency(){
//        if (StringUtils.isEmpty(Setting.currency)){
//            return  DBHelper.getHelper().queryParamCurrency();
//        }else{
            return Setting.currency;
//        }
    }

    /**
     * 获取当前单双向
     * @return
     */
    @JavascriptInterface
    public int getTranWay() {
            return Setting.way;
    }
}
