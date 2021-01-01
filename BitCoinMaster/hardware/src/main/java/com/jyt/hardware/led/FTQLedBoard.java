package com.jyt.hardware.led;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;

import android.serialport.SerialPort;


public class FTQLedBoard implements ILedBoard{

    private static Logger log =  Logger.getLogger("BitCoinMaster");


    private SerialPort serialPort;

    private OutputStream mOutputStream;

    //使用volatile保证了多线程访问时instance变量的可见性，避免了instance初始化时其他变量属性还没赋值完时，被另外线程调用
    private static volatile FTQLedBoard lededBoard;

    /**
     * 单例模式的最佳实现。内存占用地，效率高，线程安全，多线程操作原子性。
     *
     * @return
     */
    public static FTQLedBoard getInstance() {
        // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (lededBoard == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (FTQLedBoard.class) {
                //未初始化，则初始instance变量
                if (lededBoard == null) {
                    lededBoard = new FTQLedBoard();
                }
            }
        }
        return lededBoard;
    }
    @Override
    public boolean open(String dev) {
        boolean isConnect=false;
        try {
            serialPort =  new SerialPort(dev, 9600, 8,1,'E');
            mOutputStream = serialPort.getOutputStream();
            // mInputStream = serialPort.getInputStream();
            isConnect = true;
            return isConnect;
        } catch (IOException e) {
            log.error("FTQ led open:",e);
        }
        return isConnect;
    }

    @Override
    public boolean ledTwinkle(int ledId) {
        if (serialPort == null) {
            return false;
        }
        byte[] data = {0x68 ,05 ,0x10 ,01 ,00 ,1 ,0x43};
        data[2]=(byte) (15+ledId);
        data[5]=(byte) (data[1]+data[2]+data[3]+data[4]);
        return  send(data);

    }

    @Override
    public boolean closeLed(int ledId) {
        if (serialPort == null) {
            return false;
        }
        byte[] data={0x68 ,05 ,0x10 ,00 ,00 ,1 ,0x43};
        data[2]=(byte) (15+ledId);
        data[5]=(byte) (data[1]+data[2]+data[3]+data[4]);
        return  send(data);
    }

    @Override
    public boolean alwaysLight(int ledId) {
        if (serialPort == null) {
            return false;
        }
        byte[] data={0x68 ,0x05 ,0x10  ,02 ,00 ,1 ,0x43};
        data[2]=(byte) (15+ledId);
        data[5]=(byte) (data[1]+data[2]+data[3]+data[4]);
        return  send(data);
    }


    @Override
    public boolean openAlarm() {
        if (serialPort == null) {
            return false;
        }
        byte[] data={0x68,05 ,0x2e ,01 ,00 ,(byte) 0x33 ,0x43};
        return  send(data);
    }

    @Override
    public boolean closeAlarm() {
        if (serialPort == null) {
            return false;
        }
        byte[] data={0x68,05 ,0x2c,00 ,00 ,(byte) 0x74 ,0x43};
        return  send(data);
    }

    private boolean send(byte[] data){
        try {
            mOutputStream.write(data);
            return  true;
        } catch (IOException e) {
            log.info("FTQ led send:",e);
        }
        return  false;
    }

}
