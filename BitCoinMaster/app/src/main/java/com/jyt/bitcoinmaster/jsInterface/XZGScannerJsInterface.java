package com.jyt.bitcoinmaster.jsInterface;

import android.app.PendingIntent;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.jyt.bitcoinmaster.activity.MyApp;
import com.jyt.hardware.config.Config;
import com.jyt.hardware.scanner.IScanner;
import com.jyt.hardware.scanner.OnResultListener;
import com.jyt.hardware.scanner.ScannerFactory;
import com.jyt.hardware.scanner.ScannerResultListener;
import com.jyt.hardware.scanner.XZGScanner;
import com.jyt.hardware.scanner.XZScannerResultListener;

import org.apache.log4j.Logger;

public class XZGScannerJsInterface implements XZScannerResultListener {
    private static Logger log =  Logger.getLogger("BitCoinMaster");

    private XZGScanner scanner;
    private Context context;
    private WebView webView;
    private Config config;
    private String scannerDev;

    private static final int RESULT =1;
    PendingIntent mPermissionIntent ;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RESULT){
                String data = (String) msg.obj;
                log.info("[XZGScannerJsInterface]: data=" + data);
                webView.evaluateJavascript("javascript:scanllerResult('" + data + "')",null);
            }
        }
    };


    public XZGScannerJsInterface(Context context, WebView webView) {
        this.webView = webView;
        this.context = context;
    }

    /**
     * 连接设备
     */
    @JavascriptInterface
    public void connectDevices(){
        scanner = XZGScanner.getInstance();
        log.info("正在连接扫码器...");
        this.config = ((MyApp) context.getApplicationContext()).getConfig();
        this.scannerDev =  config.getScannerDev();
        boolean isSuccess = scanner.init(this.scannerDev,this);
        Message msg = Message.obtain();
        msg.what = RESULT;
        if(isSuccess){
            msg.obj = "connect scanner success";
        }else{
            msg.obj = "connect scanner fail";
        }
        handler.sendMessage(msg);
    }



    /**
     * 开始一次扫码
     */
    @JavascriptInterface
    public void startScan(){
        scanner.startScanOnce();
    }

    /**
     * 结束扫码
     */
    @JavascriptInterface
    public void stopScan(){

        scanner.stopScan();
    }

    /**
     * 自动扫码
     */
    @JavascriptInterface
    public void autoScan(){
        log.info("开启扫码仪-----------");
        scanner.startScanAuto();
//        scanner.startScanOnce();
    }



    @Override
    public void scannerResult(String receivedata) {
        stopScan();
        String data = convertHexToString(receivedata);
        data = data.replaceAll("(\r|\n)", "");
        log.info("--------------------扫描结果:" + data);
        Message msg = Message.obtain();
        msg.what = RESULT;
        msg.obj = data;
        handler.sendMessage(msg);
    }

    public static String byte2Hex(byte[] bytes) {
        if(bytes==null)return null;
        StringBuilder sb = new StringBuilder(bytes.length*2);
        for(byte b : bytes){
            sb.append(byte2Hex(b));
        }
        return sb.toString();
    }
    /**
     * convert byte to Hex String
     * @param b
     * @return
     */
    public static String byte2Hex(byte b) {
        String v = Integer.toHexString(b & 0xff).toUpperCase();
        if (v.length() == 1) {
            v = '0' + v;
        }
        return v;
    }
    /**
     * 16进制字符串转ascii
     *
     * @param hex
     * @return
     */
    public static String convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);

            temp.append(decimal);
        }

        return sb.toString();
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
