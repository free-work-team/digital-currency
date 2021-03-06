package com.jyt.bitcoinmaster.jsInterface;

import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jyt.bitcoinmaster.activity.MyApp;
import com.jyt.bitcoinmaster.facerecognization.helper.AIPPFaceHelper;
import com.jyt.bitcoinmaster.facerecognization.listener.DetectedListener;
import com.jyt.bitcoinmaster.facerecognization.listener.FaceCompareListener;
import com.jyt.bitcoinmaster.facerecognization.view.FaceRecognizationDialog;
import com.jyt.hardware.config.Config;


import org.apache.log4j.Logger;


public class FaceReconizationJsInterface implements DetectedListener, FaceCompareListener {

    private static Logger log =  Logger.getLogger("BitCoinMaster");

    private AIPPFaceHelper faceHelper;
    private Context context;
    private WebView webView;

    private Config config;
    private int faceCameraDev;

    private static final int FACERESULT =1;
    private static final int INITRESULT =2;
    private static boolean isInit = false;
    private FaceRecognizationDialog dialog;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == FACERESULT){
                String result = (String) msg.obj;
//                log.info("[FaceReconizationJsInterface]: result=" + result);
                webView.evaluateJavascript("javascript:faceResult('" + result + "')",null);
            }else if (msg.what == INITRESULT){
                int initData = (int) msg.obj;
//                log.info("[FaceReconizationJsInterface]: initData=" + initData);
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
        log.info("人脸识别初始化isInit = "+isInit);
        if (!isInit) {
            log.info("-------------------------------首次人脸识别初始化isInit");
            faceHelper.Init(context,this);
        }else{
            Message msg = Message.obtain();
            msg.what = INITRESULT;
            msg.obj = 4;
            handler.sendMessage(msg);
        }

    }

    @JavascriptInterface
    public void compareFace(String filePath,int screenType){
        this.config = ((MyApp) context.getApplicationContext()).getConfig();
        this.faceCameraDev =  config.getFaceCameraDev();
        FaceRecognizationDialog.Builder builder  = new FaceRecognizationDialog.Builder(context);
        builder.setImagePath(filePath);
        builder.setCameraId(this.faceCameraDev);
        builder.setCompareListener(this);
//        Configuration mConfiguration = context.getResources().getConfiguration();
//        int ori = mConfiguration.orientation;
        if (screenType == 1){
            builder.setPreviewViewSize(70,35,700,500);
        }else if (screenType==2){
            builder.setPreviewViewSize(210,-335,650,400);
        }else if (screenType == 3){
            builder.setPreviewViewSize(80,35,500,300);
        }else if (screenType==4){
            builder.setPreviewViewSize(70,35,300,250);
        }
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        dialog.show();
    }

    @JavascriptInterface
    public void stopCompare() {
        log.info("关闭人脸识别-------");
        try {
            dialog.dismiss();
//        faceHelper.Uninit();
        } catch (Exception e) {
            log.error("关闭人脸识别error");
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
            isInit = true;
        }
        log.info("人脸识别初始化结果: "+initInfo);
        msg.obj = ret;
        handler.sendMessage(msg);
    }

    @Override
    public void detectedResult(boolean success, float score,String imageData) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status",success);
            jsonObject.put("imageData",imageData);
            Message msg = Message.obtain();
            msg.obj = JSONObject.toJSONString(jsonObject);
            msg.what = FACERESULT;
            handler.sendMessage(msg);
    }
}
