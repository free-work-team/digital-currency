package com.jyt.hardware.led;

import android.content.Context;

import java.util.concurrent.ExecutorService;

public interface ILedBoard {

    /**
     * 打开串口
     * @param dev
     * @param
     * @return
     */
    boolean open(String dev);

    /**
     * 指定闪烁led灯
     */
    boolean ledTwinkle(int ledId);
    /**
     * 关闭led
     */
    boolean closeLed(int ledId);
    /**
     * 指定led长亮
     * @param ledId
     * @return
     */
    boolean alwaysLight(int ledId);

    /**
     * 开启报警器
     * @return
     */
    boolean openAlarm();

    /**
     * 关闭报警器
     * @return
     */
    boolean closeAlarm();
}
