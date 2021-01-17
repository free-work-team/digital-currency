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
import com.jyt.bitcoinmaster.activity.MyApp;
import com.jyt.bitcoinmaster.statics.Setting;
import com.jyt.bitcoinmaster.utils.StatusDescribe;
import com.jyt.hardware.config.Config;
import com.jyt.hardware.printer.PrintInitListener;
import com.jyt.hardware.printer.api.COMPrinter;
import com.jyt.hardware.printer.api.USBPrinter;
import com.jyt.hardware.printer.utils.BitmapUtils;
import com.jyt.hardware.printer.utils.BmpUtil;
import com.szsicod.print.escpos.PrinterAPI;
import com.szsicod.print.io.InterfaceAPI;
import com.szsicod.print.io.USBAPI;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;

public class YSPrinterJsInterface {
    private static Logger log =  Logger.getLogger("BitCoinMaster");


    //    private final String TRANSACTION_RECEIPT = "Transaction Receipt";
//    private final String RECEIPT_BOTTOMs = "I agree to the above transaction";
//    private final String RECEIPT_BOTTOM1 = "    The coins will be sent to you in 1 hour";
//    private final String RECEIPT_BOTTOM2 = "    Phone support: ";
//    private final String RECEIPT_BOTTOM3 = "    Email support: ";
//    private final String Sell_BOTTOMs = "    Please scan QR code and send BTC in 10 mins.";
//    private final String SELL_BOTTOMS_CONFIRM = "    Confirm the exact value of BTC you sent!";
    private Context context;
    private COMPrinter comPrinter = COMPrinter.getInstance();
    private WebView webView;
    private InterfaceAPI io;
    private static PrinterAPI mPrinter;
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
    public YSPrinterJsInterface(Context context,WebView webView) {
        this.context = context;
        this.webView = webView;
    }

    /**
     * 连接打印机
     */
    @JavascriptInterface
    public void init(){
        mPrinter= PrinterAPI.getInstance();
        log.info("[PrinterJsInterface]:"+"正在连接打印机");
        io = new USBAPI(context);
        int ret =  mPrinter.connect(io);
        boolean status = false;
        String message = "打印机连接失败";
        if (ret == 0){
            status = true;
            message = "打印机连接成功";
        }
        log.info("[PrinterJsInterface]:"+"status="+status+",message="+message);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status",status);
        jsonObject.put("message",message);
        Message msg = Message.obtain();
        msg.what = INIT;
        msg.obj = JSONObject.toJSONString(jsonObject);
        handler.sendMessage(msg);

    }

    /**
     * 获取打印机状态
     */
    @JavascriptInterface
    public void getStatus(){
//            String message= StatusDescribe.getStatusDescribe(mPrinter.getStatus());
        byte[] bytes = new byte[]{0x10, 0x04, 0x01, 0x10, 0x04, 0x02, 0x10, 0x04, 0x03, 0x10, 0x04, 0x04};
        mPrinter.sendOrder(bytes);
        byte[] ret = new byte[4];
        mPrinter.readIO(ret, 0, ret.length, 2 * 1000);
        boolean status = false;
        String message = "";
        if (!getBit(ret[3],5)&&!getBit(ret[3],6)){
            if (!getBit(ret[3],2)&&!getBit(ret[3],3)){
                status = true;
                message = "正常";
            }else{
                status = true;
                message ="纸将尽";
            }
        }else{
            status = false;
            message = "纸尽";
        }
        log.info("[PrinterJsInterface]:"+"status="+status+",message="+message);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status",status);
        jsonObject.put("message",message);
        Message msg = Message.obtain();
        msg.what = STATUS;
        msg.obj = JSONObject.toJSONString(jsonObject);
        handler.sendMessage(msg);
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

                try {
                    //设置行间距
                    mPrinter.setLineSpace(40);
                    // 凭条头部
                    mPrinter.setCharSize(3,3);
                    mPrinter.setAlignMode(1);
                    mPrinter.printString("Empty Notes","gbk",true);
                    mPrinter.printAndFeedLine(4);
                    // 凭条内容
                    mPrinter.setCharSize(0,0);
                    mPrinter.setAlignMode(0);
                    mPrinter.printString(content,"gbk",true);

                    mPrinter.printAndFeedLine(8);
                    mPrinter.cutPaper(66,0);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
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
                String content = "    Terminal No: T0000001 " + "\n" +
                        "    Trade Time: 1970-01-01 " + "\n" +
                        "    Trade Cash: 100.00CNY" + "\n" +
                        "    Trade Status: Success " + "\n" +
                        "    Trade Content: test print " + "\n";
                ;
                try {
                    //设置行间距
                    mPrinter.setLineSpace(40);
                    // 凭条头部
                    mPrinter.setCharSize(3,3);
                    mPrinter.setAlignMode(1);
                    mPrinter.printString("Print Test","gbk",true);
                    mPrinter.printAndFeedLine(4);
                    // 凭条内容
                    mPrinter.setCharSize(0,0);
                    mPrinter.setAlignMode(0);
                    mPrinter.printString(content,"gbk",true);

                    mPrinter.printAndFeedLine(8);
                    mPrinter.cutPaper(66,0);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
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

        try {
            //设置行间距
            mPrinter.setLineSpace(40);
            // 凭条头部
            mPrinter.setCharSize(3,3);
            mPrinter.setAlignMode(1);
            mPrinter.printString(title,"gbk",true);
            mPrinter.printAndFeedLine(1);
            // 凭条内容
            mPrinter.setCharSize(0,0);
            mPrinter.setAlignMode(0);
            mPrinter.printString(content,"gbk",true);
            mPrinter.printAndFeedLine(1);
            // 凭条底部
            mPrinter.setCharSize(0,0);
            mPrinter.setAlignMode(0);
            mPrinter.printString(bottom1,"gbk",true);
//            mPrinter.printAndFeedLine(1);
            mPrinter.printString(bottom2+ Setting.hotline,"gbk",true);
//            mPrinter.printAndFeedLine(1);
            mPrinter.printString(bottom3+ Setting.email,"gbk",true);
            mPrinter.printAndFeedLine(1);
            mPrinter.cutPaper(66,0);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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

                try {
                    //设置行间距
                    mPrinter.setLineSpace(40);
                    // 凭条头部
                    mPrinter.setCharSize(3,3);
                    mPrinter.setAlignMode(1);
                    mPrinter.printString(title,"gbk",true);
                    mPrinter.printAndFeedLine(1);

                    mPrinter.printQRCode(QRContent,  0,false);
                    mPrinter.printAndFeedLine(1);
                    // 凭条内容
                    mPrinter.setCharSize(0,0);
                    mPrinter.setAlignMode(0);
                    mPrinter.printString(content,"gbk",true);
                    mPrinter.printAndFeedLine(1);
                    // 凭条底部
                    mPrinter.setCharSize(0,0);
                    mPrinter.setAlignMode(0);
                    mPrinter.setEmphasizedMode(1);
                    mPrinter.printString(footerStr,"gbk",true);
                    mPrinter.printAndFeedLine(1);
                    mPrinter.printString(bottom2+ Setting.hotline,"gbk",true);
//                    mPrinter.printAndFeedLine(1);
                    mPrinter.printString(bottom3+ Setting.email,"gbk",true);
                    mPrinter.printAndFeedLine(1);
                    mPrinter.cutPaper(66,0);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }

    private boolean getBit(int num,int i){
        return ((num&(1<<i))!=0);
    }
}
