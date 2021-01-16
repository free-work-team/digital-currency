package com.jyt.bitcoinmaster.jsInterface;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.jyt.bitcoinmaster.facerecognization.helper.AIPPFaceHelper;
import com.jyt.bitcoinmaster.facerecognization.listener.DetectedListener;
import com.jyt.bitcoinmaster.facerecognization.listener.FaceCompareListener;
import com.jyt.bitcoinmaster.facerecognization.view.FaceRecognizationDialog;


import org.apache.log4j.Logger;

public class FaceReconizationJsInterface implements DetectedListener, FaceCompareListener {

    private static Logger log =  Logger.getLogger("BitCoinMaster");

    private AIPPFaceHelper faceHelper;
    private Context context;
    private WebView webView;
    private static final int FACERESULT =1;
    private static final int INITRESULT =2;
    private FaceRecognizationDialog dialog;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == FACERESULT){
                boolean result = (boolean) msg.obj;
                log.info("[FaceReconizationJsInterface]: result=" + result);
                webView.evaluateJavascript("javascript:faceResult('" + result + "')",null);
            }else if (msg.what == INITRESULT){
                int initData = (int) msg.obj;
                log.info("[FaceReconizationJsInterface]: initData=" + initData);
                webView.evaluateJavascript("javascript:faceInitResult('" + initData + "')",null);
            }
        }
    };


    public FaceReconizationJsInterface(Context context, WebView webView) {
        this.webView = webView;
        this.context = context;
        faceHelper = AIPPFaceHelper.getInstance();
    }

    @JavascriptInterface
    public void Init(){
        faceHelper.Init(context,this);
    }

    @JavascriptInterface
    public void compareFace(String filePath){
        FaceRecognizationDialog.Builder builder  = new FaceRecognizationDialog.Builder(context);
        builder.setImagePath(filePath);
        builder.setCompareListener(this);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @JavascriptInterface
    public void stopCompare() {
        log.info("关闭人脸识别-------");
        try {
            dialog.dismiss();
//        faceHelper.Uninit();
        } catch (Exception e) {
            log.error("关闭人脸识别error", e);
        }
    }
    @Override
    public void initResult(int ret, int errorCode) {
        Message msg = Message.obtain();
        msg.what = INITRESULT;
        String initInfo = "";
        if (ret ==0){
            initInfo = "授权检测模块异常"+errorCode;
        }else if (ret == 1){
            initInfo = "授权比对模块异常"+errorCode;
        }else if (ret == 2){
            initInfo = "初始化测模块异常"+errorCode;
        }else if (ret == 3){
            initInfo = "初始化比对模块异常"+errorCode;
        }else if (ret == 4){
            initInfo = "初始化成功";
        }
        log.info("人脸识别初始化结果: "+initInfo);
        msg.obj = ret;
        handler.sendMessage(msg);
    }

    @Override
    public void detectedResult(boolean success, float score) {
        Message msg = Message.obtain();
        msg.what = FACERESULT;
        msg.obj = success;
        handler.sendMessage(msg);
    }
}
