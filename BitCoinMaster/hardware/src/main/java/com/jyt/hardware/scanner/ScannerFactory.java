package com.jyt.hardware.scanner;

import android.content.Context;
import android.hardware.usb.UsbManager;

import com.jyt.hardware.scanner.newland.NewLandEm3096;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScannerFactory {

    private static ScannerFactory instance = new ScannerFactory();

    private IScanner mIScanner;

    private Class mClass;
    private ExecutorService executorService;
    private ScannerFactory() {
    }

    public static ScannerFactory getInstance() {
        return instance;
    }


    /**
     * 初始化打印机对象，使用默认的线程池
     * @param context
     * @param cls
     * @return
     */
    public synchronized IScanner init(Context context, UsbManager usbManager,Class cls) {
        try {
            if (mIScanner == null||mClass==null||!cls.getName().equals(mClass)) {
                mIScanner = (IScanner) cls.newInstance();
                mClass=cls;
                if(executorService==null){
                    executorService= Executors.newSingleThreadExecutor();
                }
                mIScanner.init(context, usbManager,executorService);
            }
            return mIScanner;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化默认二维码扫描仪对象，使用默认的线程池
     * @param context
     * @return
     */
    public synchronized IScanner initDefaultScanner(Context context, UsbManager usbManager) {
        try {
            if (mIScanner == null||mClass==null||!mClass.getName().equals(NewLandEm3096.class.getName())) {
                mIScanner = (IScanner) NewLandEm3096.class.newInstance();
                mClass=NewLandEm3096.class;
                if(executorService==null){
                    executorService= Executors.newSingleThreadExecutor();
                }
                mIScanner.init(context,usbManager,executorService);
            }
            return mIScanner;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 初始化扫码器对象，线程池由外部提供
     * @param context
     * @param cls
     * @param mExecutorService 线程池
     * @return
     */
    public synchronized IScanner init(Context context, UsbManager usbManager,Class cls, ExecutorService mExecutorService) {
        try {
            if (mIScanner == null||mClass==null||!cls.getName().equals(mClass)) {
                mIScanner = (IScanner) cls.newInstance();
                mClass=cls;
                mIScanner.init(context,usbManager,mExecutorService);
            }
            return mIScanner;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
