package com.jyt.bitcoinmaster.facerecognization.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.guo.android_extend.image.ImageConverter;

public class ImageUtil {

    public static byte[] imgToNV21(Bitmap bmp){
        if(bmp == null){
            Log.d("MainActivity","Bitmap error");
        }
        byte[] mImageNV21 = new byte[bmp.getWidth() * bmp.getHeight() * 3 / 2];
        ImageConverter convert = new ImageConverter();
        try{
            convert.initial(bmp.getWidth(), bmp.getHeight(), ImageConverter.CP_PAF_NV21);
            if (convert.convert(bmp, mImageNV21)) {
                Log.d("MainActivity", "convert ok!");
            }else {
                Log.d("MainActivity", "convert error!");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //回收内存
            if(bmp !=null && !bmp.isRecycled()){
                bmp.recycle();
                bmp = null;
            }
            convert.destroy();
            System.gc();
        }
        return mImageNV21;
    }
}
