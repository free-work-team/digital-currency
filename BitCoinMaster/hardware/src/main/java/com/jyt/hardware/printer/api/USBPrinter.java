package com.jyt.hardware.printer.api;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.jyt.hardware.printer.PrintInitListener;
import com.jyt.hardware.printer.utils.ESCUtil;
import com.jyt.hardware.printer.utils.ToastUtil;
import com.printsdk.cmd.PrintCmd;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

@SuppressLint("NewApi")
public class USBPrinter {

	private static Logger log =  Logger.getLogger("BitCoinMaster");
//	public static final String ACTION_USB_PERMISSION = "com.usb.printer.USB_PERMISSION";
	private static USBPrinter mInstance; 
	private Context mContext;
	private UsbManager mUsbManager;
	private UsbDeviceConnection mUsbDeviceConnection; 
	private UsbEndpoint usbEpIn,usbEpOut;
	private UsbInterface usbInterface;
	private PrintInitListener listener;
	private static final int TIME_OUT = 10000;
	public static USBPrinter getInstance() { 
		if (mInstance == null) { 
			synchronized (USBPrinter.class) { 
				if (mInstance == null) { 
					mInstance = new USBPrinter(); 
				} 
			} 
		} 
		return mInstance; 
	} 
	/** 
	 * 初始化打印机，需要与destroy对应  
	 * @param context 上下文 
	 * */ 
	public void initPrinter(final Context context, final UsbManager mUsbManager, final PrintInitListener listener, final PendingIntent mPermissionIntent) {
		this.mUsbManager = mUsbManager;
		new Thread(new Runnable() {
			@Override
			public void run() {
				getInstance().init(context,mUsbManager,listener,mPermissionIntent);
			}
		}).start();

	} 
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.BASE) 
	private void init(Context context,UsbManager mUsbManager,PrintInitListener listener,PendingIntent mPermissionIntent) {
		// list.clear(); 
		mContext = context;
		this.listener = listener;
		// 列出所有的USB设备，并且都请求获取USB权限 
		HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
		for (UsbDevice device : deviceList.values()) { 
			usbInterface = device.getInterface(0);
			if (usbInterface.getInterfaceClass() == 7) {

				if (!mUsbManager.hasPermission(device)) {
//					listener.initResult(false,"Printer failed to gain USB privileges");
					mUsbManager.requestPermission(device, mPermissionIntent);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				  connectUsbPrinter(device,listener);
                  return;
			} 
		}
		listener.initResult(false,"The device not found");
	}

	public void getStatus(){
		String message = "success";
		boolean result = true;
		byte b = sendStatus();
		if (b > 0) {
			int status = checkStatus(b);
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
	 private  byte  sendStatus(){
		write(ESCUtil.getStatus());
		  try {
			Thread.sleep(1000);//延时1000ms
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return  read();
	}

	private  int checkStatus(byte var0) {
		if ((var0 & 96) == 96) {
			return 7;
		} else {
			return (var0 & 12) == 12 ? 8 : 0;
		}
	}


//	private final BroadcastReceiver mUsbDeviceReceiver = new BroadcastReceiver() {
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction(); log.d("action", action);
//			UsbDevice mUsbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//			if (ACTION_USB_PERMISSION.equals(action)) {
//				synchronized (this) {
//					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false) && mUsbDevice != null) {
//						log.d("receiver", action);
////						connectUsbPrinter(mUsbDevice,listener);
//					} else {
//						listener.initResult(false, "USB设备请求被拒绝");
//					}
//				}
//			} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
//				if (mUsbDevice != null) {
//					ToastUtil.showShort(context, "有设备拔出");
//				}
//			} else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
//				ToastUtil.showShort(context, "有设备插入");
//				if (mUsbDevice != null) {
//					if (!mUsbManager.hasPermission(mUsbDevice)) {
//						mUsbManager.requestPermission(mUsbDevice, mPermissionIntent);
//					}
//				}
//			}
//		}
//	};
	public void close() { 
		if(mUsbDeviceConnection != null){
			mUsbDeviceConnection.close(); 
			mUsbDeviceConnection = null; 
		} 
//		mContext.unregisterReceiver(mUsbDeviceReceiver);
//		mContext = null; mUsbManager = null;
	} 
	private void connectUsbPrinter(UsbDevice mUsbDevice,PrintInitListener listener) {
		if (mUsbDevice != null) {
			for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
				UsbEndpoint ep = usbInterface.getEndpoint(i);
				if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
					if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
						mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
						usbEpOut = ep;
						if (mUsbDeviceConnection != null) {

							mUsbDeviceConnection.claimInterface(usbInterface, true);
							mUsbDeviceConnection.releaseInterface(usbInterface);
						}
					}else if(ep.getDirection() == UsbConstants.USB_DIR_IN){
						usbEpIn= ep;
						byte b =  sendStatus();
							if(b>0){
								int status = checkStatus(b);
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
					}
				}
			}
		} else {
			listener.initResult(false,"No printer was found available");
		}
	}
	private void write(byte[] bytes) { 
		if (mUsbDeviceConnection != null) {
			int b = mUsbDeviceConnection.bulkTransfer(usbEpOut, bytes, bytes.length, TIME_OUT);
		} else { 
			handler.sendEmptyMessage(0); 
		} 
	}

	private byte read() {

			byte[] receive = new byte[10];   //根据设备实际情况写数据大小
			int b = mUsbDeviceConnection.bulkTransfer(usbEpIn, receive, receive.length, TIME_OUT);
			if(b>0){
				if(receive.length>0)
				 return  receive[0];
			}
		return 0;
	}

	private Handler handler = new Handler() {
		@Override 
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
//			ToastUtil.showShort(mContext, "No printer was found available");
		} 
	}; 
	/** 
	 *  打印文字 
	 *  @param msg 
	 * */ 
	public void printText(String msg) { 
		byte[] bytes = new byte[0]; 
		try { 
			bytes = msg.getBytes("gbk"); 
		} catch (UnsupportedEncodingException e) { 
			e.printStackTrace(); 
		} 
		write(bytes); 
	} 
	/** * 换行打印文字 * @param msg */ 
	public void printTextNewLine(String msg) { 
		byte[] bytes = new byte[0]; 
		try { 
			bytes = msg.getBytes("gbk"); 
		} catch (UnsupportedEncodingException e) { 
			e.printStackTrace(); 
		} 
		write(new String("\n").getBytes()); 
		write(bytes); 
	}
	/** * 打印空行 * @param size */ 
	public void printLine(int size) { 
		for (int i = 0; i < size; i++) { 
			printText("\n"); 
		} 
	} 
	/**  
	 * 设置字体大小 
	 *  @param size 0:正常大小 1:两倍高 2:两倍宽 3:两倍大小 4:三倍高 5:三倍宽 6:三倍大 7:四倍高 8:四倍宽 9:四倍大小 10:五倍高 11:五倍宽 12:五倍大小
	 * */ 
	public void setTextSize(int size) {
		write(ESCUtil.setTextSize(size)); 
	} 
	/** * 
	 * 字体加粗 * 
	 * @param isBold 
	 * */ 
	public void bold(boolean isBold) {
		if (isBold) write(ESCUtil.boldOn()); 
		else write(ESCUtil.boldOff()); 
	} 
	/** 
	 * * 
	 * 打印一维条形码 * 
	 * @param data 
	 * */ 
	public void printBarCode(String data) { 
		write(ESCUtil.getPrintBarCode(data, 5, 90, 5, 2));
	} 
	/**
	 * 
	 * 设置对齐方式 
	 * @param position 
	 * */ 
	public void setAlign(int position) {
		byte[] bytes = null; 
		switch (position) { 
		case 0: 
			bytes = ESCUtil.alignLeft(); 
			break; 
		case 1: 
			bytes = ESCUtil.alignCenter();
			break; 
		case 2:
			bytes = ESCUtil.alignRight(); 
			break; 
		} 
		write(bytes);
	} 
	/** * 切纸 */
	public void cutPager() { 
		write(ESCUtil.cutter()); 
	} 
	/**
	 * 打印图片
	 * @param bitmap
	 */
	public void printBitmap(Bitmap bitmap){
	//	byte[] bytes = ESCUtil.printBitmap(bitmap);
		int width = bitmap.getWidth();
		int  heigh = bitmap.getHeight();
		int iDataLen = width * heigh;
		int[] pixels = new int[iDataLen];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, heigh);
		byte[] bytes = ESCUtil.PrintDiskImagefile(pixels, width, heigh);
		write(bytes);
	}



	/**
	 * 设置行间距
	 * @param space
	 */
	public void setLineSpace(int space){
		byte[] bytes = ESCUtil.setLinespace(space);
		write(bytes);
	}


}

