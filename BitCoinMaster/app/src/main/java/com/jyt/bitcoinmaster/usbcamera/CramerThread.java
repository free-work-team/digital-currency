package com.jyt.bitcoinmaster.usbcamera;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.jyt.bitcoinmaster.utils.PermisionUtils;
import com.jyt.hardware.camera.listener.CameraListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CramerThread {
    private MediaRecorder mediarecorder;// 录制视频的类private long
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceview;// 显示视频的控件
    private RelativeLayout surfaceRL;
    private Context context;
    private Camera mCamera;
    private long recordTime;
    private long startTime = Long.MIN_VALUE;
    private long endTime = Long.MIN_VALUE;
    private HashMap<String, String> map = new HashMap<String, String>();
    private static final String TAG = "SEDs508EG";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private Integer cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;


    public CramerThread(Context context,RelativeLayout surfaceRL, SurfaceView surfaceview,
                        SurfaceHolder surfaceHolder) {
        this.context = context;
        this.surfaceRL = surfaceRL;
        this.surfaceview = surfaceview;
        this.surfaceHolder = surfaceHolder;
    }

    public void setSurfaceViewSize( int x,int y,int width, int height){
        surfaceRL.post(new Runnable() {
            @Override
            public void run() {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
                lp.gravity = Gravity.LEFT|Gravity.TOP;
                lp.leftMargin = x; //矩形距离原点最近的点距离X轴的距离
                lp.topMargin = y; //矩形距离原点最近的点距离Y轴的距离
                //以上两个值，即坐标(x,y);
                surfaceRL.setLayoutParams(lp);
                surfaceRL.setVisibility(View.VISIBLE);
            }
        });
    }
    /**
     * 获取摄像头状态
     * @return
     */
    public void getCameraStatus(CameraListener listener){
        boolean canUse = true;
        Camera mCamera = null;
        try {
            // TODO camera驱动挂掉,处理??
            mCamera = Camera.open();
        } catch (Exception e) {
            listener.onResult(false,"摄像头不可用");
        }
        if(mCamera == null){
            listener.onResult(false,"摄像头不可用");
        }
        if (canUse) {
            mCamera.release();
            mCamera = null;
            listener.onResult(true,"摄像头正常");
        }
    }
    /** * 获取摄像头实例对象 * * @return */
    public Camera getCameraInstance(int cameraID) {
        Camera c = null;
        try {
            c = Camera.open(cameraID);
        } catch (Exception e) {
            // 打开摄像头错误
            Log.i("info", "打开摄像头错误");
        }
        return c;
    }

    /** * 开始录像 */
    public void startRecord(int cameraId,String path,String fileName) {
        Log.e(TAG,"开始录像");
        mediarecorder = new MediaRecorder();// 创建mediarecorder对象
        mCamera = getCameraInstance(cameraId); // 解锁camera
        mCamera.unlock();
        mediarecorder.setCamera(mCamera); // 设置录制视频源为Camera(相机)
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); // 设置录制文件质量，格式，分辨率之类，这个全部包括了
        mediarecorder.setProfile(CamcorderProfile
                .get(CamcorderProfile.QUALITY_LOW));
        mediarecorder.setPreviewDisplay(surfaceHolder.getSurface()); // 设置视频文件输出的路径
        // mediarecorder.setOutputFile("/sdcard/sForm.3gp");
        // 创建媒体文件名
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
//        mediarecorder.setOutputFile(path+File.separator+"video"+File.separator+"VID_" + timestamp + ".mp4");
        mediarecorder.setOutputFile(getOutputMediaFile(2,path,fileName).toString());
        try { // 准备录制
            mediarecorder.prepare(); // 开始录制
            mediarecorder.start();
            // time.setVisibility(View.VISIBLE);// 设置录制时间显示
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void releaseMediaRecorder() {
        if (mediarecorder != null) {
            // 清除recorder配置
            mediarecorder.reset();
            // 释放recorder对象
            mediarecorder.release();
            mediarecorder = null;
            // 为后续使用锁定摄像头
            mCamera.lock();
        }
    }


    /** * 停止录制 */
    public void stopRecord() {
        Log.e(TAG,"结束录像");
        if (mediarecorder != null) {
            // 停止录制
            mediarecorder.stop();
            // 释放资源
            mediarecorder.release();
            mediarecorder = null;
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        }
    }

//
//    /** * 定时器 * @author bcaiw * */
//    public class TimerThread extends TimerTask {
//        /** * 停止录像 */
//        @Override
//        public void run() {
//            try {
//                stopRecord();
//                this.cancel();
//            } catch (Exception e) {
//                map.clear();
//                map.put("recordingFlag", "false");
//                String ac_time = getVedioRecordTime();// 录像时间
//                map.put("recordTime", ac_time);
//                // sendMsgToHandle(m_msgHandler, iType, map);
//            }
//
//        }
//    }

    /**
     * 通用方法，接收多线程过来的数据，有可能不仅仅是msg，所以定义map对象
     *
     * @param handle
     * @param iType
     * @param info
     */
    public void sendMsgToHandle(Handler handle, int iType,
                                Map<String, String> info) {
        Message threadMsg = handle.obtainMessage();
        threadMsg.what = iType;
        Bundle threadbundle = new Bundle();
        threadbundle.clear();
        for (Iterator i = info.keySet().iterator(); i.hasNext();) {
            Object obj = i.next();
            threadbundle.putString(obj.toString(), info.get(obj));
        }
        threadMsg.setData(threadbundle);
        handle.sendMessage(threadMsg);

    }

    /**
     * 计算当前已录像时间，默认值返回0
     *
     * @return
     */
    public String getVedioRecordTime() {
        String result = "0";
        if (startTime != Long.MIN_VALUE && endTime != Long.MIN_VALUE) {
            long tempTime = (endTime - startTime);
            result = String.valueOf(tempTime);
        }
        return result;

    }

    private static File getOutputMediaFile(int type,String path,String fileName) {
        // 判断SDCard是否存在
        if (!Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            Log.d(TAG, "SDCard不存在");
            return null;
        }
//        PermisionUtils.verifyStoragePermissions(context);
        // 创建媒体文件名
        String timedir = new SimpleDateFormat("yyyyMMdd")
                .format(new Date());
        File mediaStorageDir = new File(path+ "/Android/data/com.jyt.bitcoinmaster/files/HungHui/"+timedir);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdir()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }
        // 创建媒体文件名
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    +fileName + ".mp4");
            try {
                mediaFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return mediaFile;
    }
}
