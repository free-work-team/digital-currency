package com.jyt.hardware.led;

import com.hunghui.LEDControLlib.LEDControLlib;

/**
 * Created by 84549 on 2021/1/1.
 */

public class Led {
    private LEDControLlib m_LEDDevice = new LEDControLlib();
    private static Led led;

    public static Led getInstance(){
        if (led == null){
            led = new Led();
        }
        return led;
    }

    /**
     * 打开连接
     * @param port 串口号
     * @return
     */
    public boolean openDev(String port){
        Boolean isOpen = m_LEDDevice.Open(port);
        if (isOpen){
            return true;
        }
        return false;
    }

    /**
     * 关闭连接
     */
    public void closeDev(){
        m_LEDDevice.Close();
    }

    /**
     * 设置led灯
     * @param ledId
     * @param status 0，关闭，1闪烁；2，常亮
     * @return
     */
    public boolean openLed(int ledId,int status){
        return m_LEDDevice.LEDLampControl(ledId,status);
    }

    /**
     * 设置报警器
     * @param status true 打开，false 关闭；
     * @return
     */
    public boolean openAlarm(boolean status){
       return m_LEDDevice.Alarm(status);
    }


}
