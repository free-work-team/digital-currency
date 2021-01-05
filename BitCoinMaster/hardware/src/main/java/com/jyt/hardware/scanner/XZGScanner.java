package com.jyt.hardware.scanner;

import android.serialport.SerialPort;
import android.util.Log;

import com.jyt.hardware.utils.ByteUtilException;
import com.jyt.hardware.utils.ByteUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

public class XZGScanner {
    private static Logger log =  Logger.getLogger("BitCoinMaster");

    private SerialPort serialPort;

    private OutputStream mOutputStream;
    private InputStream mInputStream;

    //使用volatile保证了多线程访问时instance变量的可见性，避免了instance初始化时其他变量属性还没赋值完时，被另外线程调用
    private static volatile XZGScanner xzgScanner;
    boolean isConnect=false;
    private ScannerResultListener listener;
    private ReadThread readThread;
    /**
     * 单例模式的最佳实现。内存占用地，效率高，线程安全，多线程操作原子性。
     *
     * @return
     */
    public static XZGScanner getInstance() {
        // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (xzgScanner == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (XZGScanner.class) {
                //未初始化，则初始instance变量
                if (xzgScanner == null) {
                    xzgScanner = new XZGScanner();
                }
            }
        }
        return xzgScanner;
    }
    public boolean init(String dev, ScannerResultListener listener) {
        this.listener = listener;
        try {
            serialPort =  new SerialPort(dev, 115200, 8,1,'n');
            mOutputStream = serialPort.getOutputStream();
            mInputStream = serialPort.getInputStream();
            isConnect = true;
            readThread = new ReadThread();
            readThread.start();
            return isConnect;
        } catch (IOException e) {
            Log.e("FTQ led open:",e.toString());
        }
        return isConnect;
    }

    /**
     * 开启扫码
     */
    public void startScanOnce(){
        if (isConnect){
            try {
                String cmd = "020700535730303030309A0103";
                Log.e("startScanOncecmd = ",cmd);
                byte[] data = ByteUtils.hex2Byte(cmd);
                mOutputStream.write(data);
            } catch (ByteUtilException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 开启连续扫码
     */
    public void startScanAuto(){
        if (isConnect){
            try {
                String cmd = "020700535730303030319B0103";
                Log.e("startScanAutocmd = ",cmd);
                byte[] data = ByteUtils.hex2Byte(cmd);
                mOutputStream.write(data);
            } catch (ByteUtilException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 停止扫码
     */
    public void stopScan(){
        if (isConnect){
            try {
                String cmd = "02070053574646464646080203";
                Log.e("stopScancmd = ",cmd);
                byte[] data = ByteUtils.hex2Byte(cmd);
                mOutputStream.write(data);
                readThread.pauseThread();
            } catch (ByteUtilException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class ReadThread extends Thread {
        private final Object lock = new Object();
        private boolean pause = false;
        StringBuffer stringBuffer = new StringBuffer();
        private String readString;
        /**
         * 调用这个方法实现暂停线程
         */
        void pauseThread() {
            pause = true;
        }

        /**
         * 调用这个方法实现恢复线程的运行
         */
        void resumeThread() {
            pause = false;
            stringBuffer = new StringBuffer();
            synchronized (lock) {
                lock.notifyAll();
            }
        }

        /**
         * 注意：这个方法只能在run方法里调用，不然会阻塞主线程，导致页面无响应
         */
        void onPause() {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        public void run() {
            super.run();
            while(true) {
                while (pause){
                    onPause();
                }
                try
                {
                    if (mInputStream == null) return;
                    byte[] buffer=new byte[512];
                    int size = mInputStream.read(buffer);
                    if (size > 0){
                        String receiveData = ByteUtils.byte2Hex(buffer).substring(0,size*2);
                        Log.e("receiveData:",receiveData);
                        listener.scannerResult(buffer);
                    }
                    try
                    {
                        Thread.sleep(50);//延时50ms
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                } catch (Throwable e)
                {
                    e.printStackTrace();
                    return;
                }
            }
        }

    }
}
