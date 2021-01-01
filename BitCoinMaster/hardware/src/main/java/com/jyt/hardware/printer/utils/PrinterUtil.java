package com.jyt.hardware.printer.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import org.apache.log4j.Logger;


public class PrinterUtil {

	private static Logger log =  Logger.getLogger("BitCoinMaster");

	private UsbManager usbManager;
	private Context ctx;
	public static final byte LF = 10;
	public static final byte ESC = 27;
	public static final byte GS = 29;
	private UsbDevice myUsbDevice;// 满足的设�??
	private UsbInterface usbInterface;// usb接口
	private UsbEndpoint epControl;// 控制端点
	private UsbDeviceConnection myDeviceConnection;// 连接
	/**
	 * 块输出端�??
	 */
	private UsbEndpoint epBulkOut;
	private UsbEndpoint epBulkIn;
	/**
	 * 中断端点
	 */
	private UsbEndpoint epIntEndpointOut;
	private UsbEndpoint epIntEndpointIn;

	public PrinterUtil(Context ctx) {
		this.ctx = ctx;
	}

	public boolean connect() {
		// 1)创建usbManager
		usbManager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
		// 2)获取到所有设�?? 选择出满足的设备
		enumeraterDevices();
		if (usbInterface == null || myUsbDevice == null) {
			return false;
		}
		// 4)获取设备endpoint
		assignEndpoint();
		// 5)打开conn连接通道
		openDevice();

		return true;
	}


	public void createData(List<String> line) {
		ByteBuffer buffer = ByteBuffer.allocate(1024 * 160);
		buffer.put(init_printer());
		buffer.put(print_linefeed());
		buffer.put(print_linefeed());
		buffer.put(print_linefeed());
		buffer.put(print_linefeed());
		for (int i = 0; i < line.size(); i++) {
			try {
				buffer.put((line.get(i) + "\n").getBytes("GBK"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		buffer.put(feedpapercut());

		int len = buffer.position();
		byte[] bytes = new byte[len];
		buffer.rewind();
		buffer.get(bytes, 0, len);
		buffer.clear();

		send(bytes);
	}

	public void send(final byte[] buffer) {
		if (!connect()) {
			log.error("PrinterUtil,connect failed");
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					sendMessageToPoint(buffer);
				} catch (Exception e) {
				}
			}
		}).start();
	}

	/**
	 * 枚举设备
	 */
	public void enumeraterDevices() {
		HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		while (deviceIterator.hasNext()) {
			UsbDevice device = deviceIterator.next();
			UsbInterface usbinterface = device.getInterface(0);
			if (usbinterface.getInterfaceClass() == 7) {
				myUsbDevice = device;
				usbInterface = usbinterface;
			}
		}
	}

	/**
	 * 分配端点，IN | OUT，即输入输出；可以�?�过判断
	 */
	private void assignEndpoint() {
		if (usbInterface != null) {
			for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
				UsbEndpoint ep = usbInterface.getEndpoint(i);
				switch (ep.getType()) {
				case UsbConstants.USB_ENDPOINT_XFER_BULK:// �??
					if (UsbConstants.USB_DIR_OUT == ep.getDirection()) {// 输出
						epBulkOut = ep;
						System.out.println("Find the BulkEndpointOut," + "index:" + i + "," + "使用端点号：" + epBulkOut.getEndpointNumber());
					} else {
						epBulkIn = ep;
						System.out.println("Find the BulkEndpointIn:" + "index:" + i + "," + "使用端点号：" + epBulkIn.getEndpointNumber());
					}
					break;
				case UsbConstants.USB_ENDPOINT_XFER_CONTROL:// 控制
					epControl = ep;
					System.out.println("find the ControlEndPoint:" + "index:" + i + "," + epControl.getEndpointNumber());
					break;
				case UsbConstants.USB_ENDPOINT_XFER_INT:// 中断
					if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {// 输出
						epIntEndpointOut = ep;
						System.out.println("find the InterruptEndpointOut:" + "index:" + i + "," + epIntEndpointOut.getEndpointNumber());
					}
					if (ep.getDirection() == UsbConstants.USB_DIR_IN) {
						epIntEndpointIn = ep;
						System.out.println("find the InterruptEndpointIn:" + "index:" + i + "," + epIntEndpointIn.getEndpointNumber());
					}
					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 * 连接设备
	 */
	public void openDevice() {
		if (usbInterface != null) {// 接口是否为null
			// 在open前判断是否有连接权限；对于连接权限可以静态分配，也可以动态分配权�??
			UsbDeviceConnection conn = null;
			if (usbManager.hasPermission(myUsbDevice)) {
				// 有权限，那么打开
				conn = usbManager.openDevice(myUsbDevice);
			}
			if (null == conn) {
				Toast.makeText(ctx, "cann't connect device", Toast.LENGTH_SHORT).show();
				return;
			}
			// 打开设备
			if (conn.claimInterface(usbInterface, true)) {
				myDeviceConnection = conn;
				if (myDeviceConnection != null)// 到此你的android设备已经连上zigbee设备
					System.out.println("open device success");
				final String mySerial = myDeviceConnection.getSerial();
				System.out.println("设备serial number�??" + mySerial);
			} else {
				System.out.println("open device failed");
				Toast.makeText(ctx, "open device failed", Toast.LENGTH_SHORT).show();
				conn.close();
			}
		}
	}

	/**
	 * 发�?�数�??
	 * 
	 * @param buffer
	 */
	public void sendMessageToPoint(byte[] buffer) {
		if (myDeviceConnection.bulkTransfer(epBulkOut, buffer, buffer.length, 0) >= 0) {
			// 0 或�?�正数表示成�??
			// myUsbDevice.
			Toast.makeText(ctx, "send success", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(ctx, "send failed", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Print and line feed LF
	 * 
	 * @return bytes for this command
	 */
	public static byte[] print_linefeed() {
		byte[] result = new byte[1];
		result[0] = LF;

		return result;
	}

	/**
	 * Initialize printer Clears the data in the print buffer and resets the printer modes to the modes that were in effect when the power was turned on. ESC @
	 * 
	 * @return bytes for this command
	 */
	public byte[] init_printer() {
		byte[] result = new byte[2];
		result[0] = ESC;
		result[1] = 64;
		return result;
	}

	/**
	 * feed paper and cut Feeds paper to ( cutting position + n x vertical motion unit ) and executes a full cut ( cuts the paper completely )
	 * 
	 * @return bytes for this command
	 */
	public byte[] feedpapercut() {
		byte[] result = new byte[4];
		result[0] = GS;
		result[1] = 86;
		result[2] = 65;
		result[3] = 0;
		return result;
	}

}
