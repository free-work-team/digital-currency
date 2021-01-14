package com.jyt.hardware.scanner.newland;

import android.app.PendingIntent;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.jyt.hardware.scanner.IScanner;
import com.jyt.hardware.scanner.OnResultListener;
import com.jyt.hardware.scanner.OnScannerTimeOutListener;
import com.jyt.hardware.scanner.ScannerResultListener;
import com.jyt.hardware.utils.ByteUtils;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NewLandEm3096 implements IScanner, Em3096.Serial {

    private static Logger log =  Logger.getLogger("BitCoinMaster");

    private ExecutorService executorService;

    private Context context;

    private UsbManager usbManager;


    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private String TAG ="NewLandScanner";
    private SerialReadManager mSerialIoManager;

    private UsbSerialPort scannerPort;
    private ScannerResultListener listener;
    private UsbDeviceConnection connection;

    private long timeOut=3000;

    Em3096 em3096=new Em3096(this);
//    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (ACTION_USB_PERMISSION.equals(action)){
//                synchronized (this) {
//                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                }
//            }
//        }
//    };

    public UsbSerialPort getDevices(PendingIntent mPermissionIntent){
        UsbSerialPort mport = null;
        List<UsbSerialDriver> drivers =
                UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
        final List<UsbSerialPort> result = new ArrayList<UsbSerialPort>();
        for (final UsbSerialDriver driver : drivers) {
            final List<UsbSerialPort> ports = driver.getPorts();
            log.info( String.format("+ %s: %s port%s",
                    driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
            result.addAll(ports);
        }
        for (int i = 0; i < result.size(); i++) {
            UsbSerialPort port = result.get(i);
            UsbSerialDriver driver = port.getDriver();
            UsbDevice device = driver.getDevice();
            String VendorId = HexDump.toHexString((short) device.getVendorId());
            String ProductId = HexDump.toHexString((short) device.getProductId());
            if (VendorId.equals("10C4")&&ProductId.equals("EA60")) {
                mport = result.get(i);
                return mport;
            }
        }
        return mport;
    }

    @Override
    public void init(Context context, UsbManager usbManager,ExecutorService executorService) {
        if(executorService==null){
            executorService= Executors.newCachedThreadPool();
        }else{
            if(this.executorService!=null&&! this.executorService.isShutdown()){
                this.executorService.shutdownNow();
            }
            usbManager = (UsbManager) context.getSystemService(context.USB_SERVICE);
        }
        this.executorService=executorService;
        this.context = context;
        this.usbManager = usbManager;

    }

    @Override
    public void connectDevices(final OnResultListener resultListener,PendingIntent mPermissionIntent) {
        scannerPort = getDevices(mPermissionIntent);
        if (scannerPort == null) {
            log.info( "No serial device.");
            resultListener.onResult(false);
            return ;
        }
        UsbDevice device = scannerPort.getDriver().getDevice();
        if (!usbManager.hasPermission(device)) {
//					listener.initResult(false,"Printer failed to gain USB privileges");
            usbManager.requestPermission(device, mPermissionIntent);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        connection = usbManager.openDevice(device);
        if (connection == null) {
            resultListener.onResult(false);
            log.info("=======scanner connection == null");
            return ;
        }
        try {
            scannerPort.open(connection);
            scannerPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        em3096.stopSetting();
                        Thread.sleep(1000);
                        if(em3096.startSetting()){
                            long timeOutTemp=em3096.getTimeOut();
                            if(timeOutTemp!=-1){
                                timeOut=timeOutTemp;
                                resultListener.onResult(true);
                                log.info(TAG+"=======scanner connection success");
                                return;
                            }else
                                log.error(TAG+"=======scanner timeOutTemp");
                        }else
                            log.error(TAG+"=======scanner startSetting fail");

                    } catch (Exception e) {
                        log.error(TAG+ "=======scanner error"+e.getMessage());
                    }
                    resultListener.onResult(false);
                }
            });

        } catch (Exception e) {
            log.error(TAG+ "=============================scanner error"+e.getMessage());
            resultListener.onResult(false);
        }
        return ;
    }

    /**
     * 开始扫码,自动扫描模式
     */
    @Override
    public void scannerAuto(ScannerResultListener listener) {
        this.listener = listener;

        try {
            byte[] cmd={0x1b,0x33};
            scannerPort.write(cmd, 3000); //设置自动扫码
            onDeviceStateChange(0);
        } catch (Exception e) {
            e.printStackTrace();
            listener.onError("scanner error");
        }
    }

    @Override
    public void getScannerTimeOut(final OnScannerTimeOutListener onScannerTimeOutListener) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (em3096.startSetting()) {
                        long timeOutTemp = em3096.getTimeOut();
                        onScannerTimeOutListener.onResult(timeOutTemp);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onScannerTimeOutListener.onResult(-1);
                }
            }
        });
    }

    /**
     * 结束扫码
     */
    @Override
    public void stopScanner() {
        try {
            stopIoManager();
            if (connection == null) {
                connection = usbManager.openDevice(scannerPort.getDriver().getDevice());
                scannerPort.open(connection);
            }
            scannerPort.write(ByteUtils.hexStr2Bytes("1b30"), 3000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setScannerTimeOut(final long timeOut,final OnResultListener onResultListener) {
        //设置一次性扫描延时时间
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if( em3096.startSetting()){
                        if(em3096.setTimeOut(timeOut)){
                            onResultListener.onResult(true);
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                onResultListener.onResult(false);
            }
        });

    }

    @Override
    public void lighting(final int statue, final OnResultListener resultListener) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (em3096.startSetting()){
                        if( em3096.setLight(statue)){
                            resultListener.onResult(true);
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                resultListener.onResult(false);
            }
        });
    }

    @Override
    public void startScannerOnce(ScannerResultListener listener) {
        this.listener = listener;
        try {
            byte[] cmd = Util.getInstance().scannerOnce();
            scannerPort.write(cmd,3000);
            onDeviceStateChange(timeOut);
        } catch (Exception e) {
            e.printStackTrace();
            listener.onError("scanner error");
        }

    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager(long timeOut) {
        mSerialIoManager = new SerialReadManager(scannerPort, timeOut,mListener);
        executorService.submit(mSerialIoManager);
    }

    private void onDeviceStateChange(long timeOut) {
        stopIoManager();
        startIoManager(timeOut);
    }

    private final SerialReadManager.Listener mListener =
            new SerialReadManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    log.info( "Runner stopped.");
                    listener.onError(e.toString());
                }

                @Override
                public void onNewData(final byte[] data) {
                    listener.scannerResult(data,0);
                }

                @Override
                public void onTimeOut() {
                    listener.onTimeOut();
                }

            };







    @Override
    public int send(byte[] bytes, byte[] receive) throws IOException {
        synchronized (NewLandEm3096.class){
            try {
                scannerPort.write(bytes,3000);
                return scannerPort.read(receive,2000);
            } catch (IOException e) {
                log.error("scanner send error",e);
            }
            return 0;
        }
    }
}
