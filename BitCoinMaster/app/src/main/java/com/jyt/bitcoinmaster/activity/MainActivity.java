package com.jyt.bitcoinmaster.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.webkit.*;
import android.widget.RelativeLayout;

import com.jyt.bitcoinmaster.utils.PermisionUtils;
import com.jyt.hardware.config.Config;
import com.jyt.bitcoinmaster.cashAcceptor.CashAcceptorFactory;
import com.jyt.bitcoinmaster.R;
import com.jyt.bitcoinmaster.timer.*;
import com.jyt.bitcoinmaster.jsInterface.*;
import com.jyt.bitcoinmaster.alarm.*;
import com.jyt.bitcoinmaster.view.CustomViewGroup;
import com.jyt.hardware.camera.api.CameraUtils;
import com.jyt.hardware.cashoutmoudle.Sqlite.DBHelper;
import com.jyt.hardware.cashoutmoudle.bean.HardwareConfig;
import com.jyt.hardware.cashoutmoudle.utils.CompratorByLastModified;
import com.jyt.hardware.printer.utils.ToastUtil;
import com.jyt.hardware.utils.CommonConstants;
import com.tbruyelle.rxpermissions.RxPermissions;
import java.util.Calendar;
import java.util.Locale;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.mindpipe.android.logging.log4j.LogConfigurator;
import org.apache.log4j.Logger;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    /**
     * 日志保存路径
     */
    private String path = Environment.getExternalStorageDirectory().getPath()+ File.separator + "HungHui" + File.separator + "logs";

    /**
     * 当前的日志命令
     */
    private String logFileName = null;
    /**
     * com口配置文件的名字
     */
    private WebView mWebView;

    private static Logger log =  Logger.getLogger("BitCoinMaster");


    public static final String ACTION_USB_PERMISSION = "com.usb.printer.USB_PERMISSION";
    private PendingIntent mPermissionIntent;
    private UsbManager mUsbManager;
    private final BroadcastReceiver mUsbDeviceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            UsbDevice mUsbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false) && mUsbDevice != null) {
//						connectUsbPrinter(mUsbDevice,listener);
                    } else {
                        ToastUtil.showShort(context, "USB device request rejected");
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                if (mUsbDevice != null) {
                    ToastUtil.showShort(context, "Device pullout");
                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                ToastUtil.showShort(context, "Device insertion");
                if (mUsbDevice != null) {
                    if (!mUsbManager.hasPermission(mUsbDevice)) {
                        mUsbManager.requestPermission(mUsbDevice, mPermissionIntent);
                    }
                }
            }
        }
    };
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private RelativeLayout surfaceRL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 10);
            }
        }
        prohibitDropDown();
        setPermision();

    }

    //禁止下拉通知方法
    private void prohibitDropDown() {
        WindowManager manager = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (50 * getResources()
                .getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;
        CustomViewGroup  view = new CustomViewGroup(this);
        manager.addView(view, localLayoutParams);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        log.info("====================onConfigurationChanged");
    }

    private void initView() {

        //设置硬件通讯参数
        DBHelper helper = DBHelper.getHelper();
        ((MyApp)getApplicationContext()).setConfig(setConfig( helper.queryAllKey()));

        surfaceView = findViewById(R.id.surfaceView);
        surfaceRL = findViewById(R.id.surfaceRL);
        surfaceHolder = surfaceView.getHolder();
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.loadUrl("file:///android_asset/pages/init.html");//加载本地asset下面的js_java_interaction.html文件
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);//打开js支持
        webSettings.setDomStorageEnabled(true);
        /**
         * 打开js接口給H5调用，参数1为本地类名，参数2为别名；h5用window.别名.类名里的方法名才能调用方法里面的内容，例如：window.android.back();
         * */
        mWebView.addJavascriptInterface(new XZGScannerJsInterface(this,mWebView), "scanner");
        mWebView.addJavascriptInterface(new BillAcceptorInterface(this,mWebView), "billAcceptor");
        mWebView.addJavascriptInterface(new YSPrinterJsInterface(this,mWebView), "print");
        mWebView.addJavascriptInterface(new BackJsInterface(this,mWebView), "back");
        mWebView.addJavascriptInterface(new UploadTimer(this,mWebView), "webTimer");
//        mWebView.addJavascriptInterface(new CameraJsInterface(this,mWebView), "camera");
        mWebView.addJavascriptInterface(new USBCameraJsInterface(this,mWebView,surfaceRL,surfaceView,surfaceHolder), "camera");
        mWebView.addJavascriptInterface(new HungHuiLedJsInterface(this,mWebView), "led");
        mWebView.addJavascriptInterface(new BusinessJsInterface(this,mWebView), "business");
        mWebView.addJavascriptInterface(new FrontJsInterface(this,mWebView), "front");
        mWebView.addJavascriptInterface(new FaceReconizationJsInterface(this,mWebView), "face");

        // log
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage cm) {
                log.info("js:" + cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId());
                return true;
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
                //log.info("====================shouldOverrideUrlLoading: view.loadUrl(url); url:" + url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//                log.info("====================onPageFinished   url:" + url);
                super.onPageFinished(view, url);
                if (url != null && url.contains("putMoney.html")) {
                    CashAcceptorFactory.getCashAcceptor(null, null).setDeviceEnable(true);
//                    ITLDeviceCom.getInstance().SetDeviceEnable(true);
                }
            }
        });

        // 缓存相关
        mWebView.clearCache(true); // 清除缓存
        mWebView.clearHistory(); // 清除历史
        mWebView.clearFormData(); // 清除表单数据
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);  //设置 缓存模式:不使用缓存，只从网络获取数据
    }

    private Config setConfig(List<HardwareConfig> list){
        Config config =  new Config();

        for(int i=0;i<list.size();i++){
            HardwareConfig hConfig = list.get(i);
            if ("PrinterComType".equals(hConfig.getHwKey())) {
                config.setPrintComm(hConfig.getHwValue());
            } else if ("PrinterDev".equals(hConfig.getHwKey())) {
                config.setPrintCom(hConfig.getHwValue());
            } else if ("Baudrate".equals(hConfig.getHwKey())) {
                config.setPrintBaudrate(hConfig.getHwValue());
            } else if ("LEDDev".equals(hConfig.getHwKey())) {
                config.setLEDCOM(hConfig.getHwValue());
            } else if ("LEDId".equals(hConfig.getHwKey()) && StringUtils.isNotBlank(hConfig.getHwValue())) {
                config.setLedId(Integer.parseInt(hConfig.getHwValue()));
            } else if ("LEDDev2".equals(hConfig.getHwKey())) {
                config.setLEDCOM2(hConfig.getHwValue());
            } else if ("LEDId2".equals(hConfig.getHwKey()) && StringUtils.isNotBlank(hConfig.getHwValue())) {
                config.setLedId2(Integer.parseInt(hConfig.getHwValue()));
            } else if ("LEDBussiness".equals(hConfig.getHwKey()) && StringUtils.isNotBlank(hConfig.getHwValue())) {
                config.setLedVendor(Integer.parseInt(hConfig.getHwValue()));
            } else if ("CPIDev".equals(hConfig.getHwKey())) {
                config.setCPIdev(hConfig.getHwValue());
            } else if ("ScannerDev".equals(hConfig.getHwKey())) {
                config.setScannerDev(hConfig.getHwValue());
            } else if ("CameraDev".equals(hConfig.getHwKey())) {
                config.setCameraDev(Integer.parseInt(hConfig.getHwValue()));
            } else if ("FaceCameraDev".equals(hConfig.getHwKey())) {
                config.setFaceCameraDev(Integer.parseInt(hConfig.getHwValue()));
            } else if ("FaceRegistration".equals(hConfig.getHwKey())) {
                config.setFaceRegistration(hConfig.getHwValue());
            }

        }
       return config;
    }
    /**
     * 设置Android6.0的权限申请
     */
    private void setPermision(){
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.INTERNET) .
                subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean){
                            initlogger();
                           // machine =  new Machine(MainActivity.this,config);
                            DBHelper.getInstance(MainActivity.this);//初始化数据库
                            mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                            mPermissionIntent = PendingIntent.getBroadcast(MainActivity.this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
                            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
                            registerReceiver(mUsbDeviceReceiver, filter);

                            // 启动查询交易记录定时器
                            Timer checkTranTimerPending = new Timer();
                            Timer checkTranTimerConfirm = new Timer();
                            Timer checkOrderTimerConfirm = new Timer();
                            Timer checkSelectConfirmTask = new Timer();
                            Timer checkTransferConfirmTask = new Timer();
                            TimerTask taskPending = new CheckPendingTask();
                            TimerTask taskConfirm = new CheckConfirmTask();
                            TimerTask taskOrderConfirm = new CheckOrderTask();
                            TimerTask selectConfirmTask = new SelectConfirmTask();
                            TimerTask transferConfirmTask = new TransferConfirmTask();
                            checkTranTimerPending.schedule(taskPending,60000,60000);// 1分钟
                            checkSelectConfirmTask.schedule(selectConfirmTask,70000,120000);// 2分钟
                            checkTranTimerConfirm.schedule(taskConfirm,80000,120000);// 2分钟
                            checkOrderTimerConfirm.schedule(taskOrderConfirm,90000,300000);// 5分钟
                            checkTransferConfirmTask.schedule(transferConfirmTask,100000,180000);// 3分钟
//                            checkTransferConfirmTask.schedule(transferConfirmTask,100000,120000);// 2分钟 测试

                            AlarmManager manager = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                            Calendar calendar =Calendar.getInstance(Locale.getDefault());
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.set(Calendar.HOUR_OF_DAY, 12);
                            calendar.set(Calendar.MINUTE, 30);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Calendar now = Calendar.getInstance();
                            if (calendar.before(now))
                                calendar.add(Calendar.DATE, 1);

                            Intent intent = new Intent();
                            intent.setAction(AlarmBroadcastReceiver.ACTION_ALARM);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                            } else {
                                manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                            }
                            initView();
                        }else{
                            finish();
                        }
                    }
                });
    }

    /**
     * 初始化日志框架
     */
    private void initlogger() {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        } else {
            // 删除大于120天的数据
            String[] fileNames = file.list();
            if (fileNames.length > 120) {
                Arrays.sort(fileNames, new CompratorByLastModified());
                for (int i = 0; i < fileNames.length - 120; i++) {
                    File mfile = new File(path + File.separator + fileNames[i]);
                    mfile.delete();
                }
            }
        }
        configlog(CommonConstants.SDF.format(new Date()));
    }

    /**
     * 配置日志文件
     * @param logFileNamePrefix 文件名称
     */
    public void configlog(String logFileNamePrefix) {
        /*
         * 检查log配置的文件名称和传入的是否一致，不一致时切换日志
         */
        if (logFileName != null && logFileNamePrefix.equals(logFileName)) {
            return;
        }

        LogConfigurator logConfigurator = new LogConfigurator();
        logFileName = logFileNamePrefix;
        logConfigurator.setUseFileAppender(true);

        logConfigurator.setFileName(path + File.separator + logFileNamePrefix+ ".log");
        logConfigurator.setRootLevel(Level.DEBUG);
        logConfigurator.setLevel("org.apache", Level.ERROR);
        logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
        logConfigurator.setMaxFileSize(1024 * 1024 * 10);
        logConfigurator.setImmediateFlush(true);
        logConfigurator.configure();

    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

        CameraUtils cameraUtils = CameraUtils.getCameraService("/sdcard","90");
        cameraUtils.stopMonitor("0",null);

        unregisterReceiver(mUsbDeviceReceiver);
       mUsbManager = null;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            return true;
        }else {
            return super.dispatchKeyEvent(event);
        }
    }

}
