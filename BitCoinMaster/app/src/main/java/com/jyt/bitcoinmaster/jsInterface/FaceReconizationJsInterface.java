package com.jyt.bitcoinmaster.jsInterface;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
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
    private boolean isInit = false;
    private FaceRecognizationDialog dialog;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == FACERESULT){
                String result = (String) msg.obj;
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
        if (!isInit) {
            faceHelper.Init(context,this);
        }else{
            Message msg = Message.obtain();
            msg.what = INITRESULT;
            msg.obj = 4;
            handler.sendMessage(msg);
        }

    }

    @JavascriptInterface
    public void compareFace(String filePath){
        this.config = ((MyApp) context.getApplicationContext()).getConfig();
        this.faceCameraDev =  config.getFaceCameraDev();
        FaceRecognizationDialog.Builder builder  = new FaceRecognizationDialog.Builder(context);
        builder.setImagePath(filePath);
        builder.setCameraId(this.faceCameraDev);
        builder.setCompareListener(this);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
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
        if (ret == 4){
            isInit = true;
        }
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
