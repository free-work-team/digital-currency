package com.jyt.hardware.scanner;

import android.app.PendingIntent;
import android.content.Context;
import android.hardware.usb.UsbManager;

import java.util.concurrent.ExecutorService;

public interface IScanner {

    void init(Context context, UsbManager usbManager,ExecutorService executorService);
    /**
     * 连接扫码设备
     */
     void connectDevices(OnResultListener resultListener, PendingIntent mPermissionIntent);
    /**
     * 开始扫码(自动扫描模式)
     */
    void scannerAuto(ScannerResultListener listener);

    /**
     * 获取扫码超时时间
     * @param onScannerTimeOutListener
     */
    void getScannerTimeOut(OnScannerTimeOutListener onScannerTimeOutListener);
    /**
     * 结束扫码
     */
    void stopScanner();

    /**
     * 扫描一次
     * @param listener
     */
    void startScannerOnce(ScannerResultListener listener);

    /**
     * 设置一次扫码超时时间,对自动扫描无效
     * @param timeOut
     * @return
     */
    void setScannerTimeOut(long timeOut, OnResultListener onResultListener);

    /**
     * 照明灯设置
     * @param statue 1-常亮模式，2-无照明模式，0闪烁模式
     * @param resultListener
     */
    void lighting(int statue, OnResultListener resultListener);
}
