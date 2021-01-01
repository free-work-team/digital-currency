package com.jyt.hardware.scanner.newland;

import java.io.IOException;


class Em3096 {
   private String TAG="Em3096";
   private Serial serial;
   private Util util;
   public Em3096(Serial serial){
       this.serial=serial;
       util=Util.getInstance();
   }

   /**
    * 开启设置
    * @return
    */
   public boolean startSetting() throws IOException {
       byte[] receive =new byte[20];
       int len = serial.send(util.startSetting(),receive);
       if(len==1&&receive[0]==6){
           return true;
       }else {//如果失败，再失败重试一次
           byte[] receive1 = new byte[20];
           int len1 = serial.send(util.startSetting(), receive1);
           if (len1 == 1 && receive1[0] == 6) {
               return true;
           }
       }
       return false;
   }

   /**
    * 关闭设置
    */
   public boolean stopSetting() throws IOException {
       byte[] receive =new byte[20];
       int len = serial.send(util.stopSetting(),receive);
       if(len==1&&receive[0]==6){
           return true;
       }
       return false;
   }

   /**
    * 获取一次性读码延时时间
    * @return
    */
   public long getTimeOut() throws IOException {
       long timeOut= -1;
       byte[] receive=new byte[25];
       int len = serial.send(util.getReadCodeTimeOut(),receive);
       if(len>9) {
           if (receive[0] == 2 && receive[1] == 0 && receive[4] == 0x34 && receive[5] == 0x30 && receive[6] == 0x33 && receive[7] == 0x30 && receive[8] == 0x30) {
               byte[] timeOutBytes = new byte[7];
               System.arraycopy(receive, 9, timeOutBytes, 0, 7);
               timeOut = bytesToInt(timeOutBytes);
           }
       }
       return timeOut;
   }

   /**
    * 设置超时时间
    * @param timeOut
    * @return
    */
   public boolean setTimeOut(long timeOut) throws IOException {
       byte[] sendData=("nls0313000="+timeOut+";").getBytes();
       byte[] receive =new byte[20];
       if(serial.send(sendData,receive)==1&&receive[0]==6){
           return true;
       }
       return false;
   }

   /**
    * 设置照明灯
    * @param statue  1-常亮模式，2-无照明模式，0闪烁模式
    * @return
    */
   public boolean setLight(int statue)  throws IOException {
       byte[] receive =new byte[20];
       int len = serial.send(util.lighting(statue),receive);
       if(len==1&&receive[0]==6){
           return true;
       }
       return false;
   }
   /**
    * 设置对焦灯
    * @param statue  1-常亮模式，2-无照明模式，3,感应模式,0闪烁模式
    * @return
    */
   public boolean setFocusLight(int statue)throws IOException {
       byte[] receive =new byte[20];
       int len = serial.send(util.focus(statue),receive);
       if(len==1&&receive[0]==6){
           return true;
       }
       return false;
   }

   private int bytesToInt(byte[] bytes){
       String strLen=new String(bytes);
       return Integer.parseInt(strLen);
   }

   interface Serial{
       int send(byte[] bytes, byte[] receive) throws IOException;
   }
}
