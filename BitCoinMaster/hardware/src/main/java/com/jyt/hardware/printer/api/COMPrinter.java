package com.jyt.hardware.printer.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.jyt.hardware.printer.PrintInitListener;
import com.jyt.hardware.printer.utils.ComBean;
import com.jyt.hardware.printer.utils.ESCUtil;
import com.jyt.hardware.printer.utils.SerialHelper;

import com.printsdk.cmd.PrintCmd;

import java.io.IOException;

import java.security.InvalidParameterException;

/**
 * Created date: 2017/7/1
 * Author:  Leslie
 *
 * 调用 美松串口热敏打印机 的工具类
 * 使用基础：
 * 1.类：ComBean.java,MyFunc.java,SerialHelper.java,TimeUtil.java,ToastUtil.java
 * 2.包：android_serialport_api 里面有 SerialPort.java,SerialPortFinder.java
 * 3.jar包：printsdk-v2.2.jar
 * 4.动态库文件：main/jniLibs/armeabi-v7a/libserial_port.so
 *
 * 使用方法：
 * 1.PrinterUtil.getInstance().init();  初始化打印设备。
 * 2.printerInstance.openComPort();     打开设备，printerInstance 工具类实例。
 * 3.printerInstance.printXxx();        打印具体内容，有多个方法。
 * 4.printerInstance.closeComPort();    关闭设备
 */

public class COMPrinter {

    private static final String TAG = "COMPrinter";
    //使用volatile保证了多线程访问时instance变量的可见性，避免了instance初始化时其他变量属性还没赋值完时，被另外线程调用
    private static volatile COMPrinter mPrinter;
    private SerialControl comA;
    private boolean isOpenCom = false;

    private PrintInitListener listener;

    private Context mContext;

    public COMPrinter() {

    }

    /**
     * 单例模式的最佳实现。内存占用地，效率高，线程安全，多线程操作原子性。
     *
     * @return
     */
    public static COMPrinter getInstance() {
        // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (mPrinter == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (COMPrinter.class) {
                //未初始化，则初始instance变量
                if (mPrinter == null) {
                    mPrinter = new COMPrinter();
                }
            }
        }
        return mPrinter;
    }

    public boolean isOpenCom() {
        return isOpenCom;
    }

    /**
     * 初始化串口打印机设备，默认数据：1.串口:/dev/ttyS4; 2.波特率:115200;
     *
     * @return
     */
    public void initPrinter(Context context,final String sPort,final int iBaudRate,final PrintInitListener listener) {
        this.mContext = context;
        this.listener = listener;
        new Thread(new Runnable() {
            @Override
            public void run() {
                getInstance().openComPort(sPort,iBaudRate);
            }
        }).start();

    }

    /**
     * 打开串口设备
     *
     * @return
     */
    private void openComPort(String sPort,int iBaudRate) {


        try {

            if(!isOpenCom){ //串口未打开
                comA = new SerialControl();
                comA.setPort(sPort);//"/dev/ttyS3"
                comA.setBaudRate(iBaudRate);
                comA.open();
            }
            isOpenCom = true;

           byte b =  comA.sendStatus(PrintCmd.GetStatus4());
            if(b>0){
                int status = PrintCmd.CheckStatus4(b);
                if(status ==7)//纸尽
                {
                    listener.initResult(false,"The printer is out of paper");
                    return;
                }else if(status ==8)//纸将尽
                {
                    listener.initResult(false,"The printer will be out of paper");
                    return;
                }
            } else {
                listener.initResult(false,"Printer status acquisition failed");
                return;
            }
            listener.initResult(true,"The device has been connected");
            
        } catch (SecurityException e) {
            isOpenCom = false;
            listener.initResult(false,"No read and write permissions");
        } catch (IOException e) {
            isOpenCom = false;
            listener.initResult(false,"IO stream error");
        } catch (InvalidParameterException e) {
            isOpenCom = false;
            listener.initResult(false,"Parameter error");
        }
    }


    /**
     *  打印机状态
     *  @param
     * */
    public void getStatus() {
        String message = "success";
        boolean result = true;
        byte b = comA.sendStatus(PrintCmd.GetStatus4());
        if (b > 0) {
            int status = PrintCmd.CheckStatus4(b);
            if (status == 7)//纸尽
            {
                message = "The printer is out of paper";
                result = false;
            }
        } else {
            message = "Printer status acquisition failed";
            result = false;
        }
        listener.statusResult(result, message);
    }

    /**
     * 设置字体大小
     *  @param size 0:正常大小 1:两倍高 2:两倍宽 3:两倍大小 4:三倍高 5:三倍宽 6:三倍大 7:四倍高 8:四倍宽 9:四倍大小 10:五倍高 11:五倍宽 12:五倍大小
     * */
    public void setTextSize(int size) {
        //第一个参数时字体的宽度，第二个参数时字体的高度
        comA.send(PrintCmd.SetSizetext(size, size));
    }

    /**
     *
     * 设置对齐方式
     * @param position
     *  如果内容此行没有填满，0:左对齐;1:水平居中;2:右对齐
     *
     * */
    public void setAlign(int position) {
        comA.send(PrintCmd.SetAlignment(position));
    }


    /**
     *
     * 设置条码对齐方式
     * @param position
     *  如果内容此行没有填满，0:左对齐;1:水平居中;2:右对齐
     *
     * */
    public void set1DBarCodeAlign(int position) {
        comA.send(PrintCmd.Set1DBarCodeAlign(position));
    }


    /**
     *  打印文字
     *  @param msg
     * */
    public void printText(String msg) {
        comA.send(PrintCmd.PrintString(msg, 2));
    }

    /** * 换行打印文字 * @param msg */
    public void printTextNewLine(String msg) {
        //第二个参数，是否加换行指令。0:加换行指令; 2.不加换行指令(等到下一个换行指令才打印)
        comA.send(PrintCmd.PrintString(msg, 0));
    }


    /** * 打印空行 * @param size */
    public void printLine(int size) {
        comA.send(PrintCmd.PrintFeedline(size));
    }


    /**
     * 打印图片
     * @param
     */
    public void printBitmap(Bitmap bm){
        int width = bm.getWidth();
        int  heigh = bm.getHeight();
        int iDataLen = width * heigh;
        int[] pixels = new int[iDataLen];
        bm.getPixels(pixels, 0, width, 0, 0, width, heigh);
        comA.send(PrintCmd.PrintDiskImagefile(pixels, width, heigh));
    }


    /**
     * 设置左边距
     * @param space
     */
    public void setLeftmargin(int space){
        comA.send(PrintCmd.SetLeftmargin(space));
    }

    /**
     * 设置行间距
     * @param space
     */
    public void setLineSpace(int space){
        comA.send(PrintCmd.SetLinespace(space));
    }


    /**
     * 字体加粗
     * @param isBold
     */
    public void bold(boolean isBold){
        if(isBold)
            comA.send(PrintCmd.SetBold(1));
        else
            comA.send(PrintCmd.SetBold(0));
    }


    /** * 切纸 */
    public void cutPager() {
        comA.send(PrintCmd.PrintCutpaper(0));
        comA.send(PrintCmd.SetClean());
    }


    /**
     * 关闭串口设备
     */
    public void closeComPort() {
        if (comA != null) {
            comA.stopSend();
            comA.close();
            isOpenCom = false;
        }
    }


    /**
     * 打印一维码
     * @param width
     * @param height
     * @param hrisize 条码显示字符字形 0: 12*24;    1:  9*17
     * @param hriseat 条码显示字符位置 0: 无;    1: 上;   2: 下;   3: 上下;
     * @param codeType 条码类型 0: UPC-A;   1: UPC-E;   2: EAN13;   3: EAN8;    4: CODE39;   5: ITF;
     *                 6: CODABAR;  7: Standard EAN13;  8: Standard EAN8;   9: CODE93;  10: CODE128;
     * @param strData 条码内容
     */
    public void print1DBar(int width,int height,int hrisize,int hriseat,int codeType,String strData){
        comA.send(PrintCmd.SetAlignment(1));
        comA.send(PrintCmd.Print1Dbar(width, height, hrisize, hriseat, codeType, strData));
        comA.send(PrintCmd.SetAlignment(0));
        // 走纸4行,再切纸,清理缓存
        try {
            PrintFeedCutpaper(4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 打印二维码
     *
     * @param strData    二维码内容
     * @param leftMargin 左边距，取值 0-27， 单位 mm
     * @param unitLength 单位长度，即二维码大小，取值 1-8，(有些打印机型只支持 1-4)
     * @param roundMode  环绕模式，0:环绕 (混排，有些机型不支持)、1:立即打印 (不混排)
     */
    public void printQRCode(String strData, int leftMargin, int unitLength, int roundMode) {
        comA.send(PrintCmd.PrintQrcode51(strData, leftMargin, unitLength, roundMode));

    }


    /**
     * @param iLine
     * @throws IOException 走纸换行，再切纸，清理缓存
     */
    private void PrintFeedCutpaper(int iLine) throws IOException {
        comA.send(PrintCmd.PrintFeedline(iLine));
        comA.send(PrintCmd.PrintCutpaper(0));
        comA.send(PrintCmd.SetClean());
    }

    public void printImgFile(){
        int[] data = getBitmapParamsData(getBitmapPath("logo100.bmp"));
        comA.send(PrintCmd.PrintDiskImagefile(data, 300, 300));
        try {
            PrintFeedCutpaper(10);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[] getBitmapParamsData(String imgPath) {
        Bitmap bm = BitmapFactory.decodeFile(imgPath, getBitmapOption(1)); // 将图片的长和宽缩小味原来的1/2
        int width = bm.getWidth();
        int  heigh = bm.getHeight();
        int iDataLen = width * heigh;
        int[] pixels = new int[iDataLen];
        bm.getPixels(pixels, 0, width, 0, 0, width, heigh);
        return pixels;
    }

    // 获取SDCard图片路径
    private String getBitmapPath(String fileName) {
        String imgPath = Environment.getExternalStorageDirectory().getPath() + "/Pictures/" + "11.bmp";
        return imgPath;
    }

    // BitmapOption 位图选项
    private static BitmapFactory.Options getBitmapOption(int inSampleSize) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return options;
    }


    private class SerialControl extends SerialHelper {
        public SerialControl() {
        }

        @Override
        protected void onDataReceived(final ComBean ComRecData) {

        }
    }
}
