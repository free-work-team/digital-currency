package com.jyt.hardware.camera.api;

import android.os.AsyncTask;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.jyt.hardware.camera.listener.CameraListener;

import org.apache.log4j.Logger;


public class CameraUtils {

    private static Logger log =  Logger.getLogger("BitCoinMaster");

    /**
     * 打开摄像头
     */
    public static final String OPEN = "0";

    /**
     * 关闭摄像头;
     */
    public static final String CLOSE= "1";
    /**
     * 获取摄像头状态
     */
    public static  final String STATUS ="2";

    /**
     * 设置视频路径
     */
    public static final String SETPATH="3";

    /**
     * 摄像头0
     */
    public static final String FRONT = "0";

    /**
     * 摄像头1
     */
    public static final String BEHIND= "1";

    //默认录像视频格式
    private static final String DEFAULT_VIDEO_FORMAT = ".m4v";

    protected volatile String videoName;

    /**
     * 视频路径
     */
    private String videoPath;
    /**
     * 录像保存时间
     */
    private String videoSavetime;

    /**
     * 是否开启录像 ,第一次初始化的时候，关闭
     */
    private static boolean  isOpen=true;


    //使用volatile保证了多线程访问时instance变量的可见性，避免了instance初始化时其他变量属性还没赋值完时，被另外线程调用
    private static volatile CameraUtils cameraUtils;



    /**
     * 单例模式的最佳实现。内存占用地，效率高，线程安全，多线程操作原子性。
     *
     * @return
     */
    public static CameraUtils getCameraService(String videoPath, String videoSavetime) {
        // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (cameraUtils == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (CameraUtils.class) {
                //未初始化，则初始instance变量
                if (cameraUtils == null) {
                    cameraUtils = new CameraUtils(videoPath,videoSavetime);
                }
            }
        }
        return cameraUtils;
    }


    private CameraUtils(String videoPath, String videoSavetime){
        this.videoPath = videoPath;
        this.videoSavetime = videoSavetime;
        new MyCameraTask(null).execute(SETPATH,"");
    }

    /**
     * 获取摄像头状态
     * @param listener
     */
    public void getCameraStatus(CameraListener listener){
        new MyCameraTask(listener).execute(STATUS,"");
    }
    /**
     * 录像
     * @param videoName 视频录像名字
     * @param id 摄像头id
     * @param listener 回调
     */
    public void startMonitor(String videoName,String id,CameraListener listener){
        this.videoName = videoName;
        if(FRONT.equals(id)){
            new MyCameraTask(listener).execute(OPEN,FRONT,videoName);
        }else{
            new MyCameraTask(listener).execute(OPEN,BEHIND,videoName);
//            listener.onResult(false,"摄像头不存在");
//            log.e("CameraUtils","摄像头不存在");
        }
    }
    /**
     * 停止录像
     * @param id
     * @param listener
     */
    public void stopMonitor(String id,CameraListener listener){
        if(isOpen)
        {
            if(FRONT.equals(id)){
                new MyCameraTask(listener).execute(CLOSE,FRONT,this.videoName);
            }else{
                new MyCameraTask(listener).execute(CLOSE,BEHIND,this.videoName);
            }
        }
        else   log.info("stopMonitor,摄像头录像已关闭");
    }


    class MyCameraTask extends AsyncTask<String ,Void,CameraResult>{
        CameraListener listener;
        public MyCameraTask(CameraListener cameraListener){
            this.listener=cameraListener;
        }

        @Override
        protected CameraResult doInBackground(String... param) {
            CameraResult cameraResult= null;
            try {
                String videoName=null;
                String sendCmd=param[0];
                String cameraId=param[1];
                if(param.length==3){
                    videoName = param[2];
                }

                cameraResult = new CameraResult(false,"");
                CameraApi.CameraStatus status =  null;
                if(CameraApi.openConnect()){
                    if(OPEN.equals(sendCmd)){
                        //打开摄像头
                        status = CameraApi.openCamera(videoName,cameraId);
                        if (cameraId.equals(FRONT)){
                            if (status.isCamera0()){
                                isOpen=true;
                                cameraResult.setSuccess(true);
                                cameraResult.setErr("Open Camera 0 Successfully");
                                log.info("CameraUtils,Open Camera 0 Successfully");
                            }else{
                                cameraResult.setSuccess(false);
                                cameraResult.setErr(status.getErrMessage());
                                log.info("CameraUtils,Open Camera 0 Fail："+status.getErrMessage());
                            }
                        }else {
                            if (status.isCamera1()){
                                cameraResult.setSuccess(true);
                                cameraResult.setErr("Open Camera 1 Successfully ");
                                log.info("CameraUtils,Open Camera 1 Successfully");
                            }else{
                                cameraResult.setSuccess(false);
                                cameraResult.setErr(status.getErrMessage());
                                log.info("CameraUtils,Open Camera 1 Fail："+status.getErrMessage());
                            }
                        }
                    }else if(CLOSE.equals(sendCmd)){
                        //关闭摄像头
                        status =  CameraApi.closeCamera(cameraId);

                        if (cameraId.equals(FRONT)){
                            if (status.isCamera0()){
                                isOpen = false;
                                cameraResult.setSuccess(true);
                                cameraResult.setErr(videoPath+
                                        File.separator + "0" +File.separator +getDate() + File.separator +
                                        videoName + DEFAULT_VIDEO_FORMAT);//成功时返回录像路径名字
                                log.info("CameraUtils,视频路径："+videoPath+
                                        File.separator + "0" +File.separator +getDate() + File.separator +
                                        videoName + DEFAULT_VIDEO_FORMAT);
                            }else{
                                cameraResult.setSuccess(false);
                                cameraResult.setErr(status.getErrMessage());
                            }
                        }else {
                            if (status.isCamera1()){
                                cameraResult.setSuccess(true);
                                cameraResult.setErr(videoPath+
                                        File.separator + "1" +File.separator +getDate() + File.separator +
                                        videoName + DEFAULT_VIDEO_FORMAT);//成功时返回录像路径名字
                                log.error("CameraUtils,视频路径："+videoPath+
                                        File.separator + "1" +File.separator +getDate() + File.separator +
                                        videoName + DEFAULT_VIDEO_FORMAT);
                            }else{
                                cameraResult.setErr(status.getErrMessage());
                                cameraResult.setErr(status.getErrMessage());
                            }
                        }
                    }else if (STATUS.equals(sendCmd)){
                        //获取摄像头状态
                        status = CameraApi.getCameraStatus();
                        if (status.isCamera0()){
                            cameraResult.setSuccess(true);
                            cameraResult.setErr("Camera service normal");
                            CameraApi.deleteOldFile(videoPath+ File.separator + "video" +
                                    File.separator + "0",videoSavetime);
                        }else{
                            cameraResult.setSuccess(false);
                            cameraResult.setErr(status.getErrMessage());
                        }
                    }else {
                        //设置视频路径
                        CameraApi.setVideoPath(videoPath);
                    }

                }else{
                    cameraResult.setErr("Camera service not started");
                    log.error("CameraUtils,Camera service not started");
                }
            } catch (Exception e) {
                cameraResult.setSuccess(false);
                cameraResult.setErr("Camera service exception");
                log.error("CameraUtils,Camera service exception"+e.toString());
            }finally {
                CameraApi.closeSocket();
            }
            return cameraResult;
        }

        @Override
        protected void onPostExecute(CameraResult cameraResult) {
            if(listener!=null)listener.onResult(cameraResult.isSuccess(),"Camera "+cameraResult.getErr());
        }
    }

    private String getDate(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format(new Date());
    }
}
