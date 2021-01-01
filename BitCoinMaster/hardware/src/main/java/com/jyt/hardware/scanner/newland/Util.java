package com.jyt.hardware.scanner.newland;

/**
 * Created by JYT_Hongqizhi on 2018/7/3.
 */

class Util {

    private static Util instance=new Util();

    public static Util getInstance(){
        return instance;
    }

    /**
     * 获取一次性读码延时时间的命令
     * @return
     */
    public byte[] getReadCodeTimeOut(){
        byte[] temp={0x44,0x30,0x33,0x30};
        return makeCmd(temp);
    }

    /**
     * 开启设置命令
     * @return
     */
    public byte[] startSetting(){
        return new byte[]{'n','l','s','0','0','0','6','0','1','0'};
    }

    public byte [] scannerOnce(){
        return new byte[]{0x1b,0x31};
    }

    /**
     * 停止设置命令
     * @return
     */
    public byte[] stopSetting(){
        return new byte[]{'n','l','s','0','0','0','6','0','0','0'};
    }
    /**
     * 组建查询命令
     * @param data
     * @return
     */
    private byte[] makeCmd(byte[] data){
        int dataLen=data.length;
        byte[] send=new byte[dataLen+6];
        send[0]=0x7E;
        send[2]=(byte) (((dataLen+1)&0xFF00)>>8);
        send[3]=(byte) ((dataLen+1)&0xFF);
        send[4]=0x33;
        System.arraycopy(data,0,send,5,dataLen);
        byte lrc=0;
        for (int i=2;i<send.length-1;i++){
            if(i==2){
                lrc=(byte)(send[i]^0xff) ;
            }else{
                lrc=(byte) (lrc^send[i]);
            }
        }
        send[send.length-1]=lrc;
        return send;
    }

    /**初始化命令
     * 自定义前缀 ：86 98 8D
     * 自定义后缀：AA
     * 自定义后缀结束符：0D 0A
     * 先禁止所有的前后缀，然后再开启前缀，后缀，后缀结束符
     */
    public byte[] init(){
        String cmd="nls0300000=0x86988D;nls0301000=0xAA;NLS0310000 = 0x0d0a;NLS0311000;NLS0305010;nls0306010;nls0309010;";
        return cmd.getBytes();
    }

    /**
     *
     * @param statue 1-常亮模式，2-无照明模式，0闪烁模式
     * @return
     */
    public byte[] lighting(int statue){
        if(statue==1){
            return new byte[]{'n','l','s','0','2','0','0','0','1','0'};
        }else if(statue==2){
            return new byte[]{'n','l','s','0','2','0','0','0','2','0'};
        }else {
            return new byte[]{'n','l','s','0','2','0','0','0','0','0'};
        }

    }

    /**
     *
     * @param statue 1-常亮模式，2-无照明模式，3,感应模式,0闪烁模式
     * @return
     */
    public byte[] focus(int statue){
        if(statue==1){
            return new byte[]{'n','l','s','0','2','0','1','0','1','0'};
        }else if(statue==2){
            return new byte[]{'n','l','s','0','2','0','1','0','2','0'};
        }else if(statue==3){
            return new byte[]{'n','l','s','0','2','0','1','0','3','0'};
        }else {
            return new byte[]{'n','l','s','0','2','0','1','0','0','0'};
        }

    }
}
