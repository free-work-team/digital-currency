package com.jyt.bitcoinmaster.facerecognization.helper;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;


import com.aippsdks.facedetection.AIPP_FDError;
import com.aippsdks.facedetection.AIPP_FDFace;
import com.aippsdks.facedetection.AIPP_FDHandle;
import com.aippsdks.facedetection.AIPP_FRError;
import com.aippsdks.facedetection.AIPP_FRFace;
import com.aippsdks.facedetection.AIPP_FRHandle;
import com.aippsdks.facedetection.AIPP_FRMatching;
import com.aippsdks.facedetection.AIPP_FTHelper;
import com.jyt.bitcoinmaster.facerecognization.listener.DetectedListener;


import java.util.ArrayList;
import java.util.List;

public class AIPPFaceHelper {
    private static AIPP_FDHandle DetectEngine=null;
    private static AIPP_FRHandle VerifyEngine=null;
    private static AIPP_FRFace face1;
    private static AIPP_FRFace face2;
    private static AIPP_FRError frError;
    private static AIPP_FDError fdError;
    private static List<AIPP_FDFace> faceList = new ArrayList<AIPP_FDFace>();// 用来存放检测到的人脸信息列表
    private static AIPP_FTHelper m_FaceTraceHelper;
    private DetectedListener listener;
    private static AIPPFaceHelper aippFaceHelper;
    public static AIPPFaceHelper getInstance(){
        if (aippFaceHelper==null){
            aippFaceHelper = new AIPPFaceHelper();
        }
        return aippFaceHelper;
    }

    /**
     * 初始化人脸识别引擎
     * @param context
     * @param listener
     */
    public  void Init(Context context, String authCode,DetectedListener listener){
        this.listener = listener;
        if (DetectEngine==null)
            DetectEngine = new AIPP_FDHandle(context);

        if (VerifyEngine==null)
            VerifyEngine = new AIPP_FRHandle(context);

        int ret = DetectEngine.Authorization(authCode,context);
        if (ret!= AIPP_FDError.OK){
            listener.initResult(0,ret);
            return;
        }
        ret = VerifyEngine.Authorization(authCode,context);
        if (ret!= AIPP_FDError.OK){
            listener.initResult(1,ret);
            return;
        }

        AIPP_FDError DetectEngineErr = DetectEngine.InitHandle();
        if (DetectEngineErr.getCode() != 0) {
            listener.initResult(2,DetectEngineErr.getCode());
            return;
        }

        AIPP_FRError VerifyEngineErr = VerifyEngine.InitHandle() ;
        if (VerifyEngineErr.getCode() != 0) {
            listener.initResult(3,VerifyEngineErr.getCode());
            return;
        }
        listener.initResult(4,0);
    }

    /**
     * 获取人脸数据
     * @param imageData
     * @return
     */
    public static List<AIPP_FDFace> getFaceByte(int width, int height, byte[]imageData){
        fdError = DetectEngine.FaceDetection(imageData, width, height, AIPP_FDHandle.NV21, faceList);
        m_FaceTraceHelper = new AIPP_FTHelper();
        //重置人脸跟踪队列
        if(faceList.size()<=0){
            DetectEngine.BioLearnbg(imageData, width, height); //进行背景学习
            m_FaceTraceHelper.TraceFace(null);
        }
//        DetectEngine.BioCheck (imageData, width, height, faceList);
        //跟踪人脸，比对成功的人脸进行跟踪，没有成功的接着进行后续比对
        m_FaceTraceHelper.TraceFace(faceList);
        return faceList;
    }
    /**
     * 人脸对比
     * @param view
     * @param fd_face1
     * @param fd_face2
     * @param data1
     * @param data2
     * @return
     */
    public static float faceCompare(Camera.Size view, int width, int height, AIPP_FDFace fd_face1, AIPP_FDFace fd_face2, byte[]data1, byte[]data2){
        face1 = new AIPP_FRFace();
        face2 = new AIPP_FRFace();
        frError = VerifyEngine.GetFeature(data1, view.width,view.height, AIPP_FRHandle.NV21,fd_face1,face1);
        if (frError.mCode != 0){
            Log.e("errCode1:",frError.mCode+"");
        }
        frError = VerifyEngine.GetFeature(data2, width,height, AIPP_FRHandle.NV21,fd_face2,face2);
        if (frError.mCode != 0){
            Log.e("errCode2:",frError.mCode+"");
        }
        AIPP_FRMatching score = new AIPP_FRMatching();
        frError = VerifyEngine.Compare (face1, face2, score);
        if (frError.mCode != 0){
            Log.e("errCode3:",frError.mCode+"");
        }
        return score.getScore();
    }

    public  void Uninit(){
        if (DetectEngine!=null)
            DetectEngine.UninitHandle();

        if (VerifyEngine!=null)
            VerifyEngine.UninitHandle();
    }
}
