package com.jyt.hardware.scanner.newland;


import com.hoho.android.usbserial.driver.UsbSerialPort;


import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by JYT_Hongqizhi on 2018/7/3.
 */

public class SerialReadManager implements Runnable {

    private static Logger log =  Logger.getLogger("BitCoinMaster");

    private final UsbSerialPort mDriver;
    private final ByteBuffer mReadBuffer;
    private final ByteBuffer mWriteBuffer;
    private SerialReadManager.State mState;
    private SerialReadManager.Listener mListener;
    private long startTime;
    private long timeOut=3000;

    public SerialReadManager(UsbSerialPort driver) {
        this(driver, 3000,(SerialReadManager.Listener)null);
    }

    public SerialReadManager(UsbSerialPort driver, long timeOut,SerialReadManager.Listener listener) {
        this.mReadBuffer = ByteBuffer.allocate(1024);
        this.mWriteBuffer = ByteBuffer.allocate(1024);
        this.mState = SerialReadManager.State.STOPPED;
        this.mDriver = driver;
        this.timeOut =timeOut;
        this.mListener = listener;
    }

    public synchronized void setListener(SerialReadManager.Listener listener) {
        this.mListener = listener;
    }

    public synchronized SerialReadManager.Listener getListener() {
        return this.mListener;
    }

    public void writeAsync(byte[] data) {
        ByteBuffer var2 = this.mWriteBuffer;
        synchronized(this.mWriteBuffer) {
            this.mWriteBuffer.put(data);
        }
    }

    public synchronized void stop() {
        if(this.getState() == SerialReadManager.State.RUNNING) {
            log.info("Stop requested");
            this.mState = SerialReadManager.State.STOPPING;
        }

    }

    private synchronized SerialReadManager.State getState() {
        return this.mState;
    }

    public void run() {
        synchronized(this) {
            if(this.getState() != SerialReadManager.State.STOPPED) {
                throw new IllegalStateException("Already running.");
            }

            this.mState = SerialReadManager.State.RUNNING;
            startTime= System.currentTimeMillis();
        }

        log.info("Running ..");

        try {
            while(this.getState() == SerialReadManager.State.RUNNING) {
                if(timeOut==0){//自动扫码模式，循环读取数据
                    this.step(true);
                }else if(timeOut> System.currentTimeMillis()-startTime){
                    this.step(false);
                }else{
                    //超时
                    SerialReadManager.Listener listener = this.getListener();
                    if(listener != null) {
                        listener.onTimeOut();
                    }
                    this.mState = State.STOPPED;
                }
            }

            log.info( "Stopping mState=" + this.getState());
        } catch (Exception var12) {
            log.info( "Run ending due to exception: " + var12.getMessage(), var12);
            SerialReadManager.Listener listener = this.getListener();
            if(listener != null) {
                listener.onRunError(var12);
            }
        } finally {
            synchronized(this) {
                this.mState = SerialReadManager.State.STOPPED;
                this.mReadBuffer.clear();
                this.mWriteBuffer.clear();
                log.info( "Stopped.");
            }
        }

    }


    private void step(boolean loop) throws IOException {
        int len = this.mDriver.read(this.mReadBuffer.array(), 200);
        if(len > 6) {
            log.info( "Read data len=" + len);
            SerialReadManager.Listener listener = this.getListener();
            if(listener != null) {
                byte[] data = new byte[len];
                this.mReadBuffer.get(data, 0, len);
//                log.info( "data===="+new String(data));
                listener.onNewData(data);
                this.mReadBuffer.clear();
                if(!loop){
                    mState= SerialReadManager.State.STOPPED;
                }
//                if(!((data[0]&0xFF)==0x86&&(data[1]&0XFF)==0x98&&(data[2]&0xFF)==0x8d)){//判断自定义前缀
//                    this.mReadBuffer.clear();
//                }
//                if(data[len-1]==(byte) 0x0A&&data[len-2]==(byte) 0x0d&&data[len-3]==(byte) 0xAA){//判断自定义后缀，以及自定义后缀结束符
//                    byte[] receive=new byte[len-6];
//                    System.arraycopy(data,3,receive,0,receive.length);
//                    listener.onNewData(receive);
//                    this.mReadBuffer.clear();
//                    if(!loop){
//                        mState=SerialReadManager.State.STOPPED;
//                    }
//                }
            }


        }

        byte[] outBuff = null;
        ByteBuffer var6 = this.mWriteBuffer;
        synchronized(this.mWriteBuffer) {
            len = this.mWriteBuffer.position();
            if(len > 0) {
                outBuff = new byte[len];
                this.mWriteBuffer.rewind();
                this.mWriteBuffer.get(outBuff, 0, len);
                this.mWriteBuffer.clear();
            }
        }

        if(outBuff != null) {
            log.info( "Writing data len=" + len);
            this.mDriver.write(outBuff, 200);
        }

    }

    public interface Listener {
        void onNewData(byte[] var1);
        void onTimeOut();
        void onRunError(Exception var1);
    }

    private static enum State {
        STOPPED,
        RUNNING,
        STOPPING;

        private State() {
        }
    }
}
