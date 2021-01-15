package com.jyt.bitcoinmaster.facerecognization.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.aippsdks.facedetection.AIPP_FDFace;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyt.bitcoinmaster.R;
import com.jyt.bitcoinmaster.facerecognization.helper.AIPPFaceHelper;
import com.jyt.bitcoinmaster.facerecognization.listener.FaceCompareListener;
import com.jyt.bitcoinmaster.facerecognization.utils.ImageUtil;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class FaceRecognizationDialog extends Dialog {

    public FaceRecognizationDialog(@NonNull Context context) {
        super(context);
    }

    public FaceRecognizationDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder implements SurfaceHolder.Callback {
        private String TAG = "FaceRecognizationDialog";
        private Context context;
        private SurfaceView surfaceView;
        private SurfaceHolder mSurfaceHolder;
        private Camera mCamera;
        private MyImageView imageView;
        private int width,height;
        private byte[] imageData;
        private AIPP_FDFace faceImg;
        private Handler mFaceHandle;
        private HandlerThread mFaceHandleThread;
        private Camera.Size previewSize;
        private Integer cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
        private FaceCompareListener listener;
        private String imagePath;
        private FaceThread faceThread;
        private boolean isPreview;
        public Builder(Context context) {
            this.context = context;
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
            surfaceView = layout.findViewById(R.id.surfaceView);
            mSurfaceHolder = surfaceView.getHolder();//获得SurfaceView的Holder
            mSurfaceHolder.addCallback(this);//设置Holder的回调
//        //在布局结束后才做初始化操作
//        surfaceView.getViewTreeObserver().addOnGlobalLayoutListener(this);
//            imageView = layout.findViewById(R.id.face_picture);
//            Glide.with(context).load("/sdcard/e.jpg").asBitmap()
//                    .skipMemoryCache(true).into(imageView);
//            Glide.with(context)
//                    .load("/sdcard/e.jpg")
//                    .asBitmap()
//                    .skipMemoryCache(true)
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//
//                        }
//                    });
            Bitmap resource = BitmapFactory.decodeFile(imagePath);
            width = resource.getWidth();
            height = resource.getHeight();
            imageData = ImageUtil.imgToNV21(resource);
            Log.e(TAG,"width:"+width+"height:"+height);
            faceImg = AIPPFaceHelper.getFaceByte(width,height,imageData).get(0);
            Log.e("faceImgRect:", "left:"+faceImg.getRect().left+",right:"+faceImg.getRect().right+",top:"+faceImg.getRect().top+",bottom:"+faceImg.getRect().bottom);
            mFaceHandleThread = new HandlerThread("face");
            mFaceHandleThread.start();
            mFaceHandle = new Handler(mFaceHandleThread.getLooper());
            faceThread = new FaceThread();
            return dialog;
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            CameraOpen();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Log.e("surfaceDestroyed","surfaceDestroyed");
            if(mCamera != null){
                if (isPreview){
//                    mFaceHandle.removeCallbacks(faceThread);
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;

                }

            }


        }
        //打开照相机
        public void CameraOpen() {
            try
            {
                //打开摄像机
                mCamera = Camera.open(cameraID);
                mCamera.setDisplayOrientation(0);
                //绑定Surface并开启预览
                mCamera.setPreviewDisplay(mSurfaceHolder);
                previewSize = mCamera.getParameters().getPreviewSize();
                final byte[] mPreBuffer = new byte[previewSize.width * previewSize.height * 3 / 2];//首先分配一块内存作为缓冲区，size的计算方式见第四点中
                mCamera.addCallbackBuffer(mPreBuffer);
                mCamera.addCallbackBuffer(mPreBuffer);
                mCamera.addCallbackBuffer(mPreBuffer);
                mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] bytes, Camera camera) {
                        if (mCamera!=null){
                            if (bytes == null) {
                                mCamera.addCallbackBuffer(mPreBuffer);
                            }
                            mCamera.addCallbackBuffer(bytes);//将此缓冲区添加到预览回调缓冲区队列中
                            faceThread.setData(bytes,camera);
                            mFaceHandle.post(faceThread);
                        }
                    }
                });
                mCamera.startPreview();
                isPreview = true;
            } catch (IOException e) {
                mCamera.release();
                mCamera = null;
                Toast.makeText(context, "surface created failed", Toast.LENGTH_SHORT).show();
            }
        }
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
                if (facesList.size()==0) listener.detectedResult(false,-1);
                for (int i = 0;i<facesList.size();i++){
                    AIPP_FDFace fdFace = facesList.get(i);
                    Log.e("rect:", "left:"+fdFace.getRect().left+",right:"+fdFace.getRect().right+",top:"+fdFace.getRect().top+",bottom:"+fdFace.getRect().bottom);
                    float score = AIPPFaceHelper.faceCompare(previewSize,width,height,fdFace,faceImg, mData,imageData);
                    Log.e("score:",score+"");
                    if (score>0.6){
                        listener.detectedResult(true,score);
                        mData = null;
                        mFaceHandle.removeCallbacks(this);
                    }else{
                        listener.detectedResult(false,score);
                        mData = null;
                    }
                }
            }

        }

    }



}
