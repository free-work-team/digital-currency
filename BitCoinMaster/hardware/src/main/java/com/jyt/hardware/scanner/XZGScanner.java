package com.jyt.hardware.scanner;

import android.serialport.SerialPort;
import android.util.Log;

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
    private XZScannerResultListener listener;
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
    public boolean init(String dev, XZScannerResultListener listener) {
        this.listener = listener;
        try {
            serialPort =  new SerialPort(dev, 115200, 0);
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
        readThread.resumeThread();
        if (isConnect){
            ExecuteCommand("SW00000");
        }
    }
    /**
     * 开启连续扫码
     */
    public void startScanAuto(){
        if (isConnect){
            ExecuteCommand("SW00001");
        }
    }
    /**
     * 停止扫码
     */
    public void stopScan(){
        if (isConnect){
            ExecuteCommand("SWFFFFF");
            readThread.pauseThread();
        }
    }


    private class ReadThread extends Thread {
        private final Object lock = new Object();
        private boolean pause = false;
        StringBuffer stringBuffer = new StringBuffer();
        private String readString = "";
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

                    if (mInputStream.available()>0){
                        byte[] buffer=new byte[128];
                        int size = mInputStream.read(buffer);
                        Log.e("size:", size+"");
                        if (size > 0) {
                            String receiveData = ByteUtils.byte2Hex(buffer).substring(0, size * 2);
                            Log.e("receiveData:", receiveData);
                            readString+=receiveData;
                        }
                    }else{
                        if (!readString.equals("")&&readString.substring(readString.length() - 4, readString.length()).equals("0D0A")) {
                            Log.e("readString:", readString);
                            listener.scannerResult(readString);
                            readString="";
                        }
                    }

//                    try
//                    {
//                        Thread.sleep(50);//延时50ms
//                    } catch (InterruptedException e)
//                    {
//                        e.printStackTrace();
//                    }
                } catch (Throwable e)
                {
                    e.printStackTrace();
                    return;
                }
            }
        }

    }
    private int calculate_checksum(byte[] buffer)
    {
        int csum = 0;
        for(int i=0;i<buffer.length;i++)
            csum += buffer[i] & 0xFF;
        return csum;
    }
    public void ExecuteCommand(String cmd)  {
        boolean bRet = false ;
        try{
            byte[] bCmd = cmd.getBytes("UTF-8");
            byte[] bSendData = new byte[bCmd.length + 6];
            byte[] bCmdLength = ByteUtils.hexStr2Bytes(Integer.toHexString(bCmd.length));
            bSendData[0] = 0x02;
            if (bCmdLength.length == 1){
                bSendData[1] = bCmdLength[0];
                bSendData[2] = 0x00;
            }else if (bCmdLength.length == 2){
                bSendData[1] = bCmdLength[1];
                bSendData[2] = bCmdLength[0];
            }

            for (int i=0;i<bCmd.length;i++) bSendData[3+i] = bCmd[i];

            int iCRC = calculate_checksum(bCmd);
            byte[] bCRC = ByteUtils.hexStr2Bytes(Integer.toHexString(iCRC));

            if (bCRC.length == 1){
                bSendData[bSendData.length - 3] = bCRC[0];
                bSendData[bSendData.length - 2] = 0x00;
            }else if (bCRC.length == 2) {
                bSendData[bSendData.length - 3] = bCRC[1];
                bSendData[bSendData.length - 2] = bCRC[0];
            }
            bSendData[bSendData.length - 1] = 0x03;

            mInputStream.skip(mInputStream.available()); //清除读缓冲

            mOutputStream.write(bSendData,0,bSendData.length);
            mOutputStream.flush();
            Log.d("TAG", "数据发送成功["+ Integer.toString(bSendData.length)+"]=" + ByteUtils.byte2Hex(bSendData,bSendData.length));
//            try {
//            if (cmd.length()>60)
//                Thread.sleep(3000);
//            else {
//                Thread.sleep(500);
//            }
//
//            int iAvailableCount = mInputStream.available();
//
//            if(iAvailableCount>0) {
//                byte[] bReadData = new byte[iAvailableCount];
//                int iReadDatalen = iAvailableCount;
//
//                iReadDatalen = mInputStream.read(bReadData, 0, iReadDatalen);
//                Log.d("receiveData", "数据返回成功["+Integer.toString(iReadDatalen)+"]=" + ByteUtils.byte2Hex(bReadData,bReadData.length));
//                String receiveData = ByteUtils.byte2Hex(bReadData);
//                if (receiveData.substring(receiveData.length()-4,receiveData.length()).equals("0D0A")){
//                    listener.scannerResult(receiveData);
//                }
////                replay.setACK(bReadData[0]);
////                byte[] bTmp = new byte[bReadData.length-1];
////                System.arraycopy(bReadData, 1, bTmp, 0, bTmp.length);
////
////                replay.setData(bTmp);
//
//                bRet = true ;
//            }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
}
