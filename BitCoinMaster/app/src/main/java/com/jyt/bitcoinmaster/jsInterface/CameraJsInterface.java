package com.jyt.bitcoinmaster.jsInterface;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import com.jyt.bitcoinmaster.view.VideoPlayDialog;
import com.jyt.hardware.camera.listener.CameraListener;
import com.jyt.hardware.camera.api.CameraUtils;

import com.alibaba.fastjson.JSONObject;

import com.jyt.hardware.cashoutmoudle.bean.EntityFile;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CameraJsInterface implements CameraListener {


    private static Logger log =  Logger.getLogger("BitCoinMaster");

    private WebView webView;

    private Context context;

    private CameraUtils cameraUtils ;

    private String  videoPath;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String initResult = (String) msg.obj;
            log.info("[CameraJsInterface]: initResult=" + initResult);
            webView.evaluateJavascript("javascript:cameraCallBack("+initResult+")",null );
            }
    };

    public CameraJsInterface(Context context, WebView webView) {
        this.webView = webView;
        this.context = context;
    }

    /**
     * 获取状态
     */
    @JavascriptInterface
    public void openConnect(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status",true);
        Message msg = Message.obtain();
        msg.obj = JSONObject.toJSONString(jsonObject);
        handler.sendMessage(msg);

//        videoPath = getStoragePath(context,true);
//        log.info("获取外置sd卡路径:"+videoPath);
//        if(StringUtils.isEmpty(videoPath)){
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("status",false);
//            jsonObject.put("message","connect camera fail");
//            Message msg = Message.obtain();
//            msg.obj = JSONObject.toJSONString(jsonObject);
//            handler.sendMessage(msg);
//        }else{
            cameraUtils = CameraUtils.getCameraService(videoPath,"90");
//            cameraUtils.getCameraStatus(this);
//        }

    }


    /**
     * 通过反射调用获取内置存储和外置sd卡根路径(通用)
     *
     * @param mContext    上下文
     * @param is_removale 是否可移除，false返回内部存储路径，true返回外置SD卡路径
     * @return
     */
    private static String getStoragePath(Context mContext, boolean is_removale) {
        String path = "";
        //使用getSystemService(String)检索一个StorageManager用于访问系统存储功能。
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);

            for (int i = 0; i < Array.getLength(result); i++) {
                Object storageVolumeElement = Array.get(result, i);
                path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (Exception e) {
            log.error("获取外置sd卡路径失败",e);
        }
        return path;
    }
    /**
     * 开始录像
     */
    @JavascriptInterface
    public void startMonitor(int type){
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String dataFormat = df.format(date);
        String str= dataFormat.substring(8);
        if(type == 1){
            str = str+"-buy";
        }else if(type == 2){
            str = str+"-sell";
        }
        cameraUtils.startMonitor(str,"0",this);
    }

    /**
     * 结束录像
     */
    @JavascriptInterface
    public void stopMonitor(){
//        cameraUtils.stopMonitor("0",this);
    }

    @Override
    public void onResult(boolean status, String message) {
        log.info("[CameraJsInterface]:"+"status="+status+",message="+message);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status",true);
        jsonObject.put("message",message);
        Message msg = Message.obtain();
        msg.obj = JSONObject.toJSONString(jsonObject);
        handler.sendMessage(msg);
    }

    /**
     * 获取
     */
    @JavascriptInterface
    public String getSDCardVideo() {
        List<EntityFile> allVideoList = new ArrayList<>();
        File directoryVideo = new File(videoPath+"/Video");
        getSDCardFile(allVideoList, directoryVideo, ".m4v");// 获得视频文件
        return JSONObject.toJSONString(allVideoList);
    }

    // 获取sdcard上文件
    private void getSDCardFile(final List<EntityFile> list, File file, final String fileType) {
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                // sdCard找到视频名称
                String name = file.getName();
                int i = name.indexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(fileType)) {
                        EntityFile vi = new EntityFile();
                        vi.setThumbPath(file.getName());
                        vi.setPath(file.getAbsolutePath());
                        list.add(vi);
                        return true;
                    }
                } else if (file.isDirectory()) {
                    getSDCardFile(list, file, fileType);
                }
                return false;
            }
        });
    }

    /**
     * 播放视频
     * @param filePath
     */
    @JavascriptInterface
    public void playVideo(String filePath){
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        File file = new File(filePath);
//        Uri uri = Uri.fromFile(file);
//        intent.setDataAndType(uri, "video/*");
//        context.startActivity(intent);
        VideoPlayDialog.Builder builder  = new VideoPlayDialog.Builder(context);
        builder.setContent(filePath);
        VideoPlayDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
