package com.jyt.bitcoinmaster.facerecognization.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Base64;
import android.util.Log;

import com.guo.android_extend.image.ImageConverter;

import java.io.ByteArrayOutputStream;

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

    public static String getBitmapImageFromYUV(byte[] data, int width, int height) {
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);
        byte[] jdata = baos.toByteArray();
        BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
        bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length, bitmapFatoryOptions);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT).trim().replaceAll("\n", "").replaceAll("\r", "");
    }

}
