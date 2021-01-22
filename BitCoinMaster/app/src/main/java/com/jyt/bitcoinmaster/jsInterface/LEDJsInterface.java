package com.jyt.bitcoinmaster.jsInterface;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.alibaba.fastjson.JSONObject;
import com.jyt.hardware.config.Config;
import com.jyt.bitcoinmaster.activity.MyApp;

import com.jyt.hardware.led.ILedBoard;
import com.jyt.hardware.led.KMLedBoard;
import com.jyt.hardware.led.FTQLedBoard;

import org.apache.log4j.Logger;

public class LEDJsInterface {


    private static Logger log =  Logger.getLogger("BitCoinMaster");

    private WebView webView;
    private Context context;
    private Config config;
    private ILedBoard ledBoard ;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String initResult = (String) msg.obj;
            log.info("[LEDJsInterface]: initResult=" + initResult);
            webView.evaluateJavascript("javascript:ledCallBack("+initResult+")",null );
            }
    };
    public LEDJsInterface(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
    }

    /**
     * 连接led
     */
    @JavascriptInterface
    public void init(){
        log.info("[LEDJsInterface]:"+"正在连接led");
        this.config = ((MyApp) context.getApplicationContext()).getConfig();

        if(1 == config.getLedVendor()){
            ledBoard = FTQLedBoard.getInstance();
        }else{
            ledBoard = KMLedBoard.getInstance();
        }
        boolean flag = ledBoard.open(config.getLEDCOM());
        log.info("[LEDJsInterface]:"+"连接led======"+flag);
        ledBoard.closeLed(config.getLedId());//关闭Led灯

        Message msg = Message.obtain();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status",flag);
        jsonObject.put("type","1");
        jsonObject.put("message","");
        msg.obj = JSONObject.toJSONString(jsonObject);
        handler.sendMessage(msg);
    }
    /**
     * 连接led
     */
    @JavascriptInterface
    public void init2(){
        log.info("[LEDJsInterface]:"+"正在连接led");
        this.config = ((MyApp) context.getApplicationContext()).getConfig();

        if(1 == config.getLedVendor()){
            ledBoard = FTQLedBoard.getInstance();
        }else{
            ledBoard = KMLedBoard.getInstance();
        }
        boolean flag = ledBoard.open(config.getLEDCOM2());
        log.info("[LEDJsInterface]:"+"连接led======"+flag);
        ledBoard.closeLed(config.getLedId2());//关闭Led灯
        Message msg = Message.obtain();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status",flag);
        jsonObject.put("type","2");
        jsonObject.put("message","");
        msg.obj = JSONObject.toJSONString(jsonObject);
        handler.sendMessage(msg);
    }

    /**
     * 开灯
     */
    @JavascriptInterface
    public void openLed(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Config config = ((MyApp) context.getApplicationContext()).getConfig();
                ILedBoard ledBoard = null;
                if(1 == config.getLedVendor()){
                    ledBoard = FTQLedBoard.getInstance();
                }else{
                    ledBoard = KMLedBoard.getInstance();
                }
                ledBoard.ledTwinkle(config.getLedId());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ledBoard.closeLed(config.getLedId());
            }
        }).start();
    }
    /**
     * 开灯
     */
    @JavascriptInterface
    public void openLed2(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Config config = ((MyApp) context.getApplicationContext()).getConfig();
                ILedBoard ledBoard = null;
                if(1 == config.getLedVendor()){
                    ledBoard = FTQLedBoard.getInstance();
                }else{
                    ledBoard = KMLedBoard.getInstance();
                }
                ledBoard.ledTwinkle(config.getLedId2());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ledBoard.closeLed(config.getLedId2());
            }
        }).start();
    }

    public void onResult(boolean status, String message) {
        log.info("[LEDJsInterface]:"+"status="+status+",message="+message);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status",status);
        jsonObject.put("message",message);
        Message msg = Message.obtain();
        msg.obj = JSONObject.toJSONString(jsonObject);
        handler.sendMessage(msg);
    }
}
