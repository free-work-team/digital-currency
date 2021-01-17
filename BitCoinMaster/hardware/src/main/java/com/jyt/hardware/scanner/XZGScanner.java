package com.jyt.hardware.scanner;

import android.serialport.SerialPort;
import android.util.Log;

import com.hunghui.SerialPort.HungHuiSerialPort;
import com.jyt.hardware.utils.ByteUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

public class XZGScanner {
    private static Logger log =  Logger.getLogger("BitCoinMaster");

    private HungHuiSerialPort serialPort;

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
            serialPort =  new HungHuiSerialPort(new File(dev),115200,'N',8,1,0);
            mOutputStream = serialPort.getOutputStream();
            mInputStream = serialPort.getInputStream();
            isConnect = true;
            readThread = new ReadThread();
            readThread.start();
            readThread.pauseThread();
            return isConnect;
        } catch (IOException e) {
            Log.e("FTQ led open:",e.toString());
        }
        return isConnect;
    }

    /**
     * 获取版本信息
     */
    public void getVersion(){
        if (isConnect){
            if (ExecuteCommand("$>:TMDEF02.<$")) {
//                readThread.resumeThread();
            }else{
                Log.e("scanner:","开启扫码失败");
            }
        }
    }
    /**
     * 开启扫码
     */
    public void startScanOnce(){
        if (isConnect){
            if (ExecuteCommand("SW00000")) {
                readThread.resumeThread();
            }else{
                Log.e("scanner:","开启扫码失败");
            }
        }
    }
    /**
     * 开启连续扫码
     */
    public void startScanAuto(){
        if (isConnect){
            if (ExecuteCommand("SW00001")) {
                readThread.resumeThread();
            }else{
                Log.e("scanner:","开启扫码失败");
            }
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
        private String readString="";
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
                    byte[] buffer=new byte[128];
                    if (mInputStream.available()>0){
                        int size = mInputStream.read(buffer);
                        if (size > 0){
                            String receiveData = ByteUtils.byte2Hex(buffer).substring(0,size*2);
                            readString += receiveData;
                        }
                    }else{
                        if (!readString.equals("")&&readString.substring(readString.length()-4,readString.length()).equals("0D0A"))
                            listener.scannerResult(readString);
                            readString="";
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
    public boolean ExecuteCommand(String cmd)  {
        boolean bRet = false ;
        try{
            byte[] bCmd = cmd.getBytes("UTF-8");
            byte[] bSendData = new byte[bCmd.length + 6];
            byte[] bCmdLength = HexStringToBytes(Integer.toHexString(bCmd.length));
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
            byte[] bCRC = HexStringToBytes(Integer.toHexString(iCRC));

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

            if (cmd.length()>60) {
                Thread.sleep(3000);
            }else {
                Thread.sleep(500);
            }
            int iAvailableCount = mInputStream.available();

            if(iAvailableCount>0) {
                byte[] bReadData = new byte[iAvailableCount];
                int iReadDatalen = iAvailableCount;

                iReadDatalen = mInputStream.read(bReadData, 0, iReadDatalen);
                Log.d("receiveData", "数据返回成功["+Integer.toString(iReadDatalen)+"]=" + bytesToHexString(bReadData,bReadData.length));
                if (bReadData[0]== 0x05){
                    bRet = true ;
                }
//                replay.setACK(bReadData[0]);
//                byte[] bTmp = new byte[bReadData.length-1];
//                System.arraycopy(bReadData, 1, bTmp, 0, bTmp.length);
//
//                replay.setData(bTmp);


            }
        }
        catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bRet;
    }
    private byte[] HexStringToBytes(String src) {
        String sSrc = src;
        if (src.length() % 2 != 0) {
            sSrc = "0" + src;
        }

        int len = sSrc.length() / 2;
        byte[] ret = new byte[len];
        byte[] tmp = sSrc.getBytes();

        for(int i = 0; i < len; ++i) {
            ret[i] = this.uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }

        return ret;
    }
    private byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0}));
        _b0 = (byte)(_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1}));
        byte ret = (byte)(_b0 ^ _b1);
        return ret;
    }
    private String bytesToHexString(byte[] src, int length) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src != null && src.length > 0) {
            if (src.length > length) {
                length = src.length;
            }

            for(int i = 0; i < length; ++i) {
                int v = src[i] & 255;
                String hv = Integer.toHexString(v);
                if (i == 0) {
                    stringBuilder.append("0x");
                } else {
                    stringBuilder.append(" 0x");
                }

                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }

                stringBuilder.append(hv);
            }

            return stringBuilder.toString();
        } else {
            return null;
        }
    }
}
