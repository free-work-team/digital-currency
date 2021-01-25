package com.jyt.bitcoinmaster.jsInterface;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.alibaba.fastjson.JSONObject;

import com.jyt.bitcoinmaster.usbcamera.CramerThread;
import com.jyt.bitcoinmaster.view.VideoPlayDialog;
import com.jyt.hardware.camera.listener.CameraListener;
import com.jyt.hardware.cashoutmoudle.bean.EntityFile;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class USBCameraJsInterface implements CameraListener {
    private static Logger log =  Logger.getLogger("BitCoinMaster");

    private WebView webView;

    private Context context;
    private String  videoPath;
    private CramerThread thread;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String initResult = (String) msg.obj;
            log.info("[CameraJsInterface]: initResult=" + initResult);
            webView.evaluateJavascript("javascript:cameraCallBack("+initResult+")",null );
        }
    };

    public USBCameraJsInterface(Context context, WebView webView, SurfaceView surfaceView, SurfaceHolder surfaceHolder) {
        this.webView = webView;
        this.context = context;
        thread = new CramerThread(surfaceView,surfaceHolder);
    }

    /**
     * 获取状态
     */
    @JavascriptInterface
    public void openConnect(){

        videoPath = getStoragePath(context,true);
        log.info("获取外置sd卡路径:"+videoPath);
        if(StringUtils.isEmpty(videoPath)){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status",false);
            jsonObject.put("message","connect camera fail");
            Message msg = Message.obtain();
            msg.obj = JSONObject.toJSONString(jsonObject);
            handler.sendMessage(msg);
        }else{
            thread.getCameraStatus(this);
        }

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
     * 设置预览窗体大小
     * @param width
     * @param heght
     */
    @JavascriptInterface
    public void setPreViewSize(int x,int y,int width,int heght){
        Configuration mConfiguration = context.getResources().getConfiguration();
        int ori = mConfiguration.orientation;
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE){
            thread.setSurfaceViewSize(x,y,width,heght);
        }else if (ori == mConfiguration.ORIENTATION_PORTRAIT){
            thread.setSurfaceViewSize(x,y,width,heght);
        }

    }
    /**
     * 开始录像
     */
    @JavascriptInterface
    public void startMonitor(int type,int cameraId){

        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String dataFormat = df.format(date);
        String str= dataFormat.substring(8);
        if(type == 1){
            str = str+"-buy";
        }else if(type == 2){
            str = str+"-sell";
        }
        thread.startRecord(cameraId,videoPath);
    }

    /**
     * 结束录像
     */
    @JavascriptInterface
    public void stopMonitor(){
        thread.stopRecord();
    }

    @Override
    public void onResult(boolean status, String message) {
        log.info("[CameraJsInterface]:"+"status="+status+",message="+message);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status",status);
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
