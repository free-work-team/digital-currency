package com.jyt.bitcoinmaster.facerecognization.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.aippsdks.facedetection.AIPP_FDFace;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyt.bitcoinmaster.R;
import com.jyt.bitcoinmaster.facerecognization.helper.AIPPFaceHelper;
import com.jyt.bitcoinmaster.facerecognization.helper.CameraHelper;
import com.jyt.bitcoinmaster.facerecognization.listener.CameraListener;
import com.jyt.bitcoinmaster.facerecognization.listener.FaceCompareListener;
import com.jyt.bitcoinmaster.facerecognization.utils.ImageUtil;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class FaceRecognizationDialog extends Dialog {

    public FaceRecognizationDialog(@NonNull Context context) {
        super(context);
    }

    public FaceRecognizationDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private String TAG = "FaceRecognizationDialog";
        private Context context;
        private TextureView previewView;
        private SurfaceHolder mSurfaceHolder;
        private Camera mCamera;
        private MyImageView imageView;
        private int mwidth,mheight;
        private byte[] imageData;
        private AIPP_FDFace faceImg;
        private Handler mFaceHandle;
        private HandlerThread mFaceHandleThread;
        private Camera.Size previewSize;
        private int cameraID;
        private FaceCompareListener listener;
        private String imagePath;
        private FaceThread faceThread;
        private boolean isPreview;
        private ImageView scan;
        private CameraHelper cameraHelper;
        private int X;
        private int Y;
        private int width,height;


        public Builder(Context context) {
            this.context = context;
        }

        public void setCameraId(int cameraID){
            this.cameraID = cameraID;
        }
        public void setPreviewViewSize(int X,int Y,int mwidth,int mheight){
            this.X = X;
            this.Y = Y;
            this.mwidth = mwidth;
            this.mheight = mheight;
        }

        public void setCompareListener(FaceCompareListener listener){
            this.listener = listener;
        }
        public void setImagePath(String imagePath){
            this.imagePath = imagePath;
        }
        public FaceRecognizationDialog create() {
            LayoutInflater layoutInflater = (LayoutInflater) context.
                    getSystemService(context.LAYOUT_INFLATER_SERVICE);
            final FaceRecognizationDialog dialog = new FaceRecognizationDialog(context, R.style.Dialog);
            View layout = layoutInflater.inflate(R.layout.face, null);
            dialog.addContentView(layout, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            DisplayMetrics dm = new DisplayMetrics();
            params.x = X;
            params.y = Y;
            params.width = mwidth;
            params.height = mheight;
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setDimAmount(0f);
            dialog.getWindow().setGravity(Gravity.LEFT);
            previewView = layout.findViewById(R.id.surfaceView);
            scan = layout.findViewById(R.id.scan);
            int left = params.x-50;
            int right = params.x+700;
            int top = params.y-50;
            int bottom = params.height-20;



            Bitmap resource = BitmapFactory.decodeFile(imagePath);
            width = resource.getWidth();
            height = resource.getHeight();
            imageData = ImageUtil.imgToNV21(resource);
            Log.e(TAG,"width:"+width+"height:"+height);
            if ( AIPPFaceHelper.getFaceByte(width,height,imageData).size()>0){
                faceImg = AIPPFaceHelper.getFaceByte(width,height,imageData).get(0);
                Log.e("faceImgRect:", "left:"+faceImg.getRect().left+",right:"+faceImg.getRect().right+",top:"+faceImg.getRect().top+",bottom:"+faceImg.getRect().bottom);
                mFaceHandleThread = new HandlerThread("face");
                mFaceHandleThread.start();
                mFaceHandle = new Handler(mFaceHandleThread.getLooper());
                faceThread = new FaceThread();
                //在布局结束后才做初始化操作
                previewView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        initCamera(scan,bottom);
                    }
                });
            }else{
                Log.e(TAG,"图片未检测到人脸");
                listener.detectedResult(false,0,"图片未检测到人脸");
            }

            return dialog;
        }

        private void initCamera(ImageView scan,int bottom) {
//            DisplayMetrics metrics = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            CameraListener cameraListener = new CameraListener() {
                @Override
                public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                    Log.i(TAG, "onCameraOpened: " + cameraId + "  " + displayOrientation + " " + isMirror);
                    previewSize = camera.getParameters().getPreviewSize();
                    // 从上到下的平移动画
                    Animation verticalAnimation = new TranslateAnimation(0, 0, 0, bottom);
                    verticalAnimation.setDuration(2000); // 动画持续时间
                    verticalAnimation.setRepeatCount(Animation.INFINITE); // 无限循环
                    verticalAnimation.setRepeatMode(Animation.RESTART);
                    // 播放动画
                    scan.setAnimation(verticalAnimation);
                    scan.startAnimation(verticalAnimation);
                }


                @Override
                public void onPreview(byte[] nv21, Camera camera) {
                    faceThread.setData(nv21,camera);
                    mFaceHandle.post(faceThread);
                }

                @Override
                public void onCameraClosed() {
                    Log.e(TAG, "onCameraClosed: ");
                    mFaceHandle.removeCallbacks(faceThread);
                }

                @Override
                public void onCameraError(Exception e) {
                    Log.i(TAG, "onCameraError: " + e.getMessage());
                }

                @Override
                public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                    Log.i(TAG, "onCameraConfigurationChanged: " );
                }
            };
            cameraHelper = new CameraHelper.Builder()
                    .previewViewSize(new Point(previewView.getMeasuredWidth(),previewView.getMeasuredHeight()))
                    .rotation(0)
                    .specificCameraId(cameraID)
                    .isMirror(true)
                    .previewOn(previewView)
                    .cameraListener(cameraListener)
                    .build();
            cameraHelper.init();
        }



//        @Override
//        public void surfaceCreated(SurfaceHolder surfaceHolder) {
//            CameraOpen();
//        }
//
//        @Override
//        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//
//        }
//
//        @Override
//        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//            Log.e("surfaceDestroyed","surfaceDestroyed");
//            if(mCamera != null){
//                if (isPreview){
//                    mFaceHandle.removeCallbacks(faceThread);
//                    mCamera.stopPreview();
//                    mCamera.release();
//                    mCamera = null;
//
//                }
//
//            }
//
//
//        }
//        //打开照相机
//        public void CameraOpen() {
//            try
//            {
//                //打开摄像机
//                mCamera = Camera.open(cameraID);
//                mCamera.setDisplayOrientation(0);
//                //绑定Surface并开启预览
//                mCamera.setPreviewDisplay(mSurfaceHolder);
//                previewSize = mCamera.getParameters().getPreviewSize();
//                Camera.Parameters parameters = mCamera.getParameters();
//                parameters.setPreviewSize(previewSize.width, previewSize.height);
//                if (cameraID == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                    //设置镜像效果，支持的值为flip-mode-values=off,flip-v,flip-h,flip-vh;
//                    parameters.set("preview-flip", "flip-h");
//                }
//                mCamera.setParameters(parameters);
//                final byte[] mPreBuffer = new byte[previewSize.width * previewSize.height * 3 / 2];//首先分配一块内存作为缓冲区，size的计算方式见第四点中
//                mCamera.addCallbackBuffer(mPreBuffer);
//                mCamera.addCallbackBuffer(mPreBuffer);
//                mCamera.addCallbackBuffer(mPreBuffer);
//                mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
//                    @Override
//                    public void onPreviewFrame(byte[] bytes, Camera camera) {
//                        if (mCamera!=null){
//                            if (bytes == null) {
//                                mCamera.addCallbackBuffer(mPreBuffer);
//                            }
//                            mCamera.addCallbackBuffer(bytes);//将此缓冲区添加到预览回调缓冲区队列中
//                            faceThread.setData(bytes,camera);
//                            mFaceHandle.post(faceThread);
//                        }
//                    }
//                });
//                mCamera.startPreview();
//                isPreview = true;
//            } catch (IOException e) {
//                mCamera.release();
//                mCamera = null;
//                Toast.makeText(context, "surface created failed", Toast.LENGTH_SHORT).show();
//            }
//        }
        public  class FaceThread implements Runnable {

            private byte[] mData;
            private ByteArrayOutputStream mBitmapOutput;//mUploadOutp1ut
            private Matrix mMatrix;
            private Message mMessage;
            private Camera mtCamera;
            private int index;
            public void setData(byte[] data, Camera camera){
                mData = data;
                mtCamera = camera;
            }
            @Override
            public void run() {
                List<AIPP_FDFace> facesList = AIPPFaceHelper.getFaceByte(previewSize.width,previewSize.height,mData);
                Log.e("facesList",facesList.size()+"");
                String bitData = ImageUtil.getBitmapImageFromYUV(mData,previewSize.width,previewSize.height);
                if (facesList.size()==0) listener.detectedResult(false,-1,"");
                for (int i = 0;i<facesList.size();i++){
                    AIPP_FDFace fdFace = facesList.get(i);
                    Log.e("rect:", "left:"+fdFace.getRect().left+",right:"+fdFace.getRect().right+",top:"+fdFace.getRect().top+",bottom:"+fdFace.getRect().bottom);
                    float score = AIPPFaceHelper.faceCompare(previewSize,width,height,fdFace,faceImg, mData,imageData);
                    Log.e("score:",score+"");
                    if (score>0.6){

                        listener.detectedResult(true,score,bitData);
                        mData = null;
                        deleteFiles(imagePath);
                        mFaceHandle.removeCallbacks(this);
                    }else{
                        listener.detectedResult(false,score,"");
                    }
                }
            }

        }

        public static void deleteFiles(String fileNames) {
                File file = new File(fileNames);
                if (file.exists())
                    file.delete();

        }

    }



}
