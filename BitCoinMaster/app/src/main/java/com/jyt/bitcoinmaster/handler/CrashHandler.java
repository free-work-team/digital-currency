package com.jyt.bitcoinmaster.handler;


import java.lang.Thread.UncaughtExceptionHandler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.widget.Toast;

import com.jyt.bitcoinmaster.activity.MainActivity;
import com.jyt.hardware.camera.api.CameraUtils;

import org.apache.log4j.Logger;


public class CrashHandler implements UncaughtExceptionHandler {

    private static Logger log = Logger.getLogger("BitCoinMaster");

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信息

    /**
     * 保证只有一个CrashHandler实例
     **/

    volatile private static CrashHandler sCrashHandler;


    public static CrashHandler getInstance(){
        if (sCrashHandler == null) {
            synchronized (CrashHandler.class){
                if (sCrashHandler == null) {
                    //使用Application Context
                    sCrashHandler=new CrashHandler();
                }
            }
        }
        return sCrashHandler;
    }


    /**
     * 初始化
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UnCaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);

    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        log.error("uncaughtException error :" , ex);
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // 此处示例发生异常后，重新启动应用
              try{
                  Thread.sleep(30000);
                  //关闭录像
                  CameraUtils cameraUtils = CameraUtils.getCameraService("/sdcard","90");
                  cameraUtils.stopMonitor("0",null);

              }catch(Exception e){
                  log.error("uncaughtException:" , e);
              }


                Intent intent = new Intent(mContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread(){
            @Override
            public void run() {
                Looper.prepare();

             /*   new AlertDialog.Builder(mContext).setTitle("标题").setMessage("提示的信息")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which){
                                dialog.dismiss();

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();*/
                Toast.makeText(mContext, "Sorry,the program is abnormal,it will restart after 30 seconds.", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        return true;
    }
}
