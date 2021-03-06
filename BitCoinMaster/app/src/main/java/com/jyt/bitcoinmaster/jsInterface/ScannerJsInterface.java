package com.jyt.bitcoinmaster.jsInterface;

import android.app.PendingIntent;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.jyt.hardware.scanner.IScanner;
import com.jyt.hardware.scanner.OnResultListener;
import com.jyt.hardware.scanner.ScannerFactory;
import com.jyt.hardware.scanner.ScannerResultListener;

import org.apache.log4j.Logger;


public class ScannerJsInterface implements ScannerResultListener {

    private static Logger log =  Logger.getLogger("BitCoinMaster");

    private IScanner scanner;
    private Context context;
    private WebView webView;
    private static final int RESULT =1;
    PendingIntent mPermissionIntent ;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RESULT){
                String data = (String) msg.obj;
//                log.info("[ScannerJsInterface]: data=" + data);
                webView.evaluateJavascript("javascript:scanllerResult('" + data + "')",null);
            }
        }
    };


    public ScannerJsInterface(Context context, WebView webView,UsbManager usbManager,PendingIntent mPermissionIntents) {
        this.webView = webView;
        this.context = context;
        this.mPermissionIntent = mPermissionIntents;
        scanner = ScannerFactory.getInstance().initDefaultScanner(context,usbManager);
    }

    /**
     * 连接设备
     */
    @JavascriptInterface
    public void connectDevices(){
       log.info("正在连接扫码器...");
       scanner.connectDevices(new OnResultListener() {
            @Override
            public void onResult(boolean success) {
                Message msg = Message.obtain();
                msg.what = RESULT;
                if(success){
                    msg.obj = "connect scanner success";
                }else{
                    msg.obj = "connect scanner fail";
                }
                handler.sendMessage(msg);
            }
        },mPermissionIntent);
    }



    /**
     * 开始一次扫码
     */
    @JavascriptInterface
    public void startScan(){
        scanner.startScannerOnce(this);
    }

    /**
     * 结束扫码
     */
    @JavascriptInterface
    public void stopScan(){
        scanner.stopScanner();
    }

    /**
     * 自动扫码
     */
    @JavascriptInterface
    public void autoScan(){
        scanner.scannerAuto(this);
    }


    @Override
    public void onError(String message) {
//        Message msg = Message.obtain();
//        msg.what = RESULT;
//        msg.obj = "01";
//        handler.sendMessage(msg);
    }
    @Override
    public void onTimeOut() {
//        Message msg = Message.obtain();
//        msg.what = RESULT;
//        msg.obj = "01";
//        handler.sendMessage(msg);
    }

    @Override
    public void scannerResult(byte[] bytes, int size) {
        stopScan();
        String data = ByteToString(bytes);
        data = data.replaceAll("(\r|\n)", "");
        log.info("--------------------扫描结果:" + data);
        Message msg = Message.obtain();
        msg.what = RESULT;
        msg.obj = data;
        handler.sendMessage(msg);
    }
    private  String ByteToString(byte[] bytes)
    {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i <bytes.length ; i++) {
            if (bytes[i]>0){
                strBuilder.append((char)bytes[i]);
            }else {
                continue;
            }
        }
        return strBuilder.toString();
    }
}
