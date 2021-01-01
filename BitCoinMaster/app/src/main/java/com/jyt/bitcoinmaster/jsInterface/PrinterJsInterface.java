package com.jyt.bitcoinmaster.jsInterface;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.alibaba.fastjson.JSONObject;
import com.jyt.hardware.config.Config;
import com.jyt.bitcoinmaster.activity.MyApp;
import com.jyt.bitcoinmaster.statics.Setting;
import com.jyt.hardware.led.FTQLedBoard;
import com.jyt.hardware.led.ILedBoard;
import com.jyt.hardware.led.KMLedBoard;
import com.jyt.hardware.printer.PrintInitListener;
import com.jyt.hardware.printer.api.COMPrinter;
import com.jyt.hardware.printer.api.USBPrinter;
import com.jyt.hardware.printer.utils.BitmapUtils;
import com.jyt.hardware.printer.utils.BmpUtil;

import org.apache.log4j.Logger;


public class PrinterJsInterface implements PrintInitListener {

    private static Logger log =  Logger.getLogger("BitCoinMaster");


//    private final String TRANSACTION_RECEIPT = "Transaction Receipt";
//    private final String RECEIPT_BOTTOMs = "I agree to the above transaction";
//    private final String RECEIPT_BOTTOM1 = "    The coins will be sent to you in 1 hour";
//    private final String RECEIPT_BOTTOM2 = "    Phone support: ";
//    private final String RECEIPT_BOTTOM3 = "    Email support: ";
//    private final String Sell_BOTTOMs = "    Please scan QR code and send BTC in 10 mins.";
//    private final String SELL_BOTTOMS_CONFIRM = "    Confirm the exact value of BTC you sent!";
    private Context context;
    private USBPrinter usbPrinter = USBPrinter.getInstance();
    private COMPrinter comPrinter = COMPrinter.getInstance();
    private WebView webView;
    private UsbManager usbManager;
    private PendingIntent mPermissionIntent;
    private Config config;
    private  String printType;
    private static final int INIT = 2;
    private static final int STATUS = 3;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case INIT:
                    String initResult = (String) msg.obj;
                    log.info("[PrintJsInterface]: initResult=" + initResult);
                    webView.evaluateJavascript("javascript:printCallBack(" + initResult + ")",null);
                    break;
                case STATUS:
                    String statusResult = (String) msg.obj;
                    log.info("[PrintJsInterface]: initResult=" + statusResult);
                    webView.evaluateJavascript("javascript:printCallBack(" + statusResult + ")",null);
                    break;
                default:
                    break;
            }
        }
    };
    public PrinterJsInterface(Context context,WebView webView,UsbManager usbManager,PendingIntent mPermissionIntent) {
        this.context = context;
        this.webView = webView;
        this.usbManager = usbManager;
        this.mPermissionIntent = mPermissionIntent;

    }

    /**
     * 连接打印机
     */
    @JavascriptInterface
    public void init(){
        log.info("[PrinterJsInterface]:"+"正在连接打印机");
        this.config = ((MyApp) context.getApplicationContext()).getConfig();
        this.printType =  config.getPrintComm();

        if(printType!=null&&printType.equals("SerialPort"))
              comPrinter.initPrinter(context,config.getPrintCom(),Integer.parseInt(config.getPrintBaudrate()),this);
        else
            usbPrinter.initPrinter(context, usbManager,this,mPermissionIntent);
    }

    /**
     * 获取打印机状态
     */
    @JavascriptInterface
    public void getStatus(){
        if(printType!=null&&printType.equals("SerialPort"))
            comPrinter.getStatus();
        else
            usbPrinter.getStatus();
    }

    /**
     * 打印文本 清机凭条
     * @param content
     */
    @JavascriptInterface
    public void printEmpty(final String content){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(printType!=null&&printType.equals("SerialPort")){
                        comPrinter.setLineSpace(40);
                        // 凭条头部
                        comPrinter.setTextSize(1);
                        comPrinter.setAlign(1);
                        comPrinter.printTextNewLine("Empty Cash");
                        comPrinter.printLine(3);
                        // 凭条内容
                        comPrinter.setTextSize(0);
                        comPrinter.setAlign(0);
                        comPrinter.printTextNewLine(content);

                        comPrinter.printLine(6);
                        comPrinter.cutPager();
                    }else {
                        //设置行间距
                        usbPrinter.setLineSpace(40);
                        // 凭条头部
                        usbPrinter.setTextSize(3);
                        usbPrinter.setAlign(1);
                        usbPrinter.printTextNewLine("Empty Notes");
                        usbPrinter.printLine(4);
                        // 凭条内容
                        usbPrinter.setTextSize(0);
                        usbPrinter.setAlign(0);
                        usbPrinter.printTextNewLine(content);

                        usbPrinter.printLine(8);
                        usbPrinter.cutPager();

                    }
                }catch (Exception e){
                    log.error("打印清机凭条异常",e);
                }

            }
        }).start();

    }
    /**
     * 打印测试
     */
    @JavascriptInterface
    public void printTest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String content ="    Terminal No: T0000001 "  + "\n" +
                            "    Trade Time: 1970-01-01 "  + "\n" +
                            "    Trade Cash: 100.00CNY"  + "\n" +
                            "    Trade Status: Success " + "\n" +
                            "    Trade Content: test print "  + "\n";;
                    if(printType!=null&&printType.equals("SerialPort")){
                        comPrinter.setLineSpace(40);
                        // 凭条头部
                        comPrinter.setTextSize(1);
                        comPrinter.setAlign(1);
                        comPrinter.printTextNewLine("Print Test");
                        comPrinter.printLine(3);
                        // 凭条内容
                        comPrinter.setTextSize(0);
                        comPrinter.setAlign(0);
                        comPrinter.printTextNewLine(content);
                        comPrinter.printLine(6);
                        comPrinter.cutPager();
                    }else {
                        //设置行间距
                        usbPrinter.setLineSpace(40);
                        // 凭条头部
                        usbPrinter.setTextSize(3);
                        usbPrinter.setAlign(1);
                        usbPrinter.printTextNewLine("Print Test");
                        usbPrinter.printLine(4);
                        // 凭条内容
                        usbPrinter.setTextSize(0);
                        usbPrinter.setAlign(0);
                        usbPrinter.printTextNewLine(content);
                        usbPrinter.printLine(8);
                        usbPrinter.cutPager();
                    }
                }catch (Exception e){
                    log.error("打印测试凭条异常",e);
                }

            }
        }).start();
    }

    /**t
     * 打印文本
     * @param content
     */
    @JavascriptInterface
    public void printText(final String title ,final String content,final String bottom1,final String bottom2,final String bottom3){
        if(printType!=null&&printType.equals("SerialPort")){
            comPrinter.setLineSpace(40);
            // 凭条头部
            comPrinter.setTextSize(1);
            comPrinter.setAlign(1);
            comPrinter.printTextNewLine(title);
            comPrinter.printLine(3);
            // 凭条内容
            comPrinter.setTextSize(0);
            comPrinter.setAlign(0);
            comPrinter.printTextNewLine(content);
            comPrinter.printLine(2);
            // 凭条底部
            comPrinter.setTextSize(0);
            comPrinter.setAlign(0);
            comPrinter.printText(bottom1);
            comPrinter.printLine(1);
            comPrinter.printText(bottom2+ Setting.hotline);
            comPrinter.printLine(1);
            comPrinter.printText(bottom3+ Setting.email);
            comPrinter.printLine(6);
            comPrinter.cutPager();
        }else {
            //设置行间距
            usbPrinter.setLineSpace(40);
            // 凭条头部
            usbPrinter.setTextSize(3);
            usbPrinter.setAlign(1);
            usbPrinter.printTextNewLine(title);
            usbPrinter.printLine(4);
            // 凭条内容
            usbPrinter.setTextSize(0);
            usbPrinter.setAlign(0);
            usbPrinter.printTextNewLine(content);
            usbPrinter.printLine(2);
            // 凭条底部
            usbPrinter.setTextSize(0);
            usbPrinter.setAlign(0);
            usbPrinter.printText(bottom1);
            usbPrinter.printLine(1);
            usbPrinter.printText(bottom2+ Setting.hotline);
            usbPrinter.printLine(1);
            usbPrinter.printText(bottom3+ Setting.email);
            usbPrinter.printLine(8);
            usbPrinter.cutPager();

        }

    }

    /**
     * 打印二维码
     * @param QRContent
     */
    @JavascriptInterface
    public void printQRCode(final String title,final String QRContent,final String content,final String footerStr,final String bottom2,final String bottom3) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (printType != null && printType.equals("SerialPort")) {
                    //设置行间距
                    comPrinter.setLineSpace(40);
                    comPrinter.setTextSize(1);
                    comPrinter.setAlign(1);
                    comPrinter.printTextNewLine(title);
                    comPrinter.printLine(2);
                    // 打印图片二维码
                    BitmapUtils bUtils = new BitmapUtils();
                    Bitmap bitmap = bUtils.generateBitmap(QRContent, 250, 250);
                    String path = BmpUtil.getBitmapPath(bitmap);
                    Bitmap bmp = BitmapFactory.decodeFile(path);
                    comPrinter.setAlign(0);
                    comPrinter.setLeftmargin(175);
                    comPrinter.printBitmap(bmp);
                    comPrinter.setLeftmargin(0);
                    comPrinter.printLine(2);
                    // 凭条内容
                    comPrinter.setTextSize(0);
                    comPrinter.setAlign(0);
                    comPrinter.printTextNewLine(content);
                    comPrinter.printLine(2);
                    // 凭条底部
                    comPrinter.setTextSize(0);
                    comPrinter.setAlign(0);
                    comPrinter.bold(true);
                    comPrinter.printText(footerStr);
                    comPrinter.printLine(3);
                    comPrinter.printText(bottom2+ Setting.hotline);
                    comPrinter.printLine(1);
                    comPrinter.printText(bottom3+ Setting.email);
                    comPrinter.printLine(6);
                    comPrinter.cutPager();
                } else {
                    //设置行间距
                    usbPrinter.setLineSpace(40);
                    usbPrinter.setTextSize(3);
                    usbPrinter.setAlign(1);
                    usbPrinter.printTextNewLine(title);
                    usbPrinter.printLine(2);
                    // 打印图片二维码
                    BitmapUtils bUtils = new BitmapUtils();
                    Bitmap bitmap = bUtils.generateBitmap(QRContent, 250, 250);
                    String path = BmpUtil.getBitmapPath(bitmap);
                    Bitmap bmp = BitmapFactory.decodeFile(path);
                    usbPrinter.printBitmap(bmp);
                    usbPrinter.printLine(2);
                    // 凭条内容
                    usbPrinter.setTextSize(0);
                    usbPrinter.setAlign(0);
                    usbPrinter.printTextNewLine(content);
                    usbPrinter.printLine(2);
                    // 凭条底部
                    usbPrinter.setTextSize(0);
                    usbPrinter.setAlign(0);
                    usbPrinter.bold(true);
                    usbPrinter.printText(footerStr);
                    usbPrinter.printLine(3);
                    usbPrinter.printText(bottom2+ Setting.hotline);
                    usbPrinter.printLine(1);
                    usbPrinter.printText(bottom3+ Setting.email);
                    usbPrinter.printLine(8);
                    usbPrinter.cutPager();
                }
            }
        }).start();
    }


    @Override
    public void initResult(boolean status, String message) {
        log.info("[PrinterJsInterface]:"+"status="+status+",message="+message);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status",status);
        jsonObject.put("message",message);
        Message msg = Message.obtain();
        msg.what = INIT;
        msg.obj = JSONObject.toJSONString(jsonObject);
        handler.sendMessage(msg);
    }

    @Override
    public void statusResult(boolean status, String message) {
        log.info("[PrinterJsInterface]:"+"status="+status+",message="+message);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status",status);
        jsonObject.put("message",message);
        Message msg = Message.obtain();
        msg.what = STATUS;
        msg.obj = JSONObject.toJSONString(jsonObject);
        handler.sendMessage(msg);
    }
}
