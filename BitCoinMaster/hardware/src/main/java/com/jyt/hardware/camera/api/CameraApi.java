package com.jyt.hardware.camera.api;

import android.annotation.SuppressLint;


import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;



@SuppressLint("SdCardPath")
public class CameraApi {

	private static Logger log =  Logger.getLogger("BitCoinMaster");

	/* 服务器地址 */
	private final static String SERVER_HOST_IP = "127.0.0.1";
	/* 服务器端口*/
	private final static int SERVER_HOST_PORT = 10100;
	private static Socket socket;
	private static PrintStream output;
	private static BufferedReader in;
	/**
	 * 默认硬盘路径  /storage/external_storage/sda1/JYT
	 */
	/**
	 * 摄像头0状态 0默认状态（关闭成功），1打开成功，2打开失败，3关闭失败
	 */
	private static int camrea0IsOpen=0;
	/**
	 * 摄像头1状态  0默认状态（关闭成功），1打开成功，2打开失败，3关闭失败
	 */
	private static int camrea1IsOpen=0;
	/**
	 * socket超时时间
	 */
	private static int timeOut=15*1000;
	private	static AtomicInteger count = new AtomicInteger();
	private CameraStatus status = new CameraStatus();

	public CameraApi() {
	}

	public CameraStatus getInstance(){
		return status;
	}

	public static void getConnect() {

		try {
			/* 连接服务器 */
			socket = new Socket(SERVER_HOST_IP, SERVER_HOST_PORT);
			/* 获取输出流 */
			output = new PrintStream(socket.getOutputStream(), true, "utf-8");

			in = new BufferedReader(new InputStreamReader

					(socket.getInputStream()));
			socket.setSoTimeout(timeOut);
		} catch (UnknownHostException e) {

			socket = null;
			output = null;
			e.printStackTrace();
		} catch (IOException e) {
			socket = null;
			output = null;
			e.printStackTrace();
		}
	}

	public static boolean openConnect(){
		synchronized (CameraApi.class) {
			boolean result=false;
			int user = count.getAndIncrement();
			try {
				socket = new Socket(SERVER_HOST_IP, SERVER_HOST_PORT);
				/* 获取输出流 */
				output = new PrintStream(socket.getOutputStream(), true, "utf-8");

				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				result=true;
				socket.setSoTimeout(timeOut);
				log.info("CameraApi,建立socket通讯");
				/* 连接服务器 */
			} catch (UnknownHostException e) {
				socket = null;
				output = null;
				log.info("CameraApi,Socket连接主机识别异常="+e.toString());
			} catch (IOException e) {
				log.error("CameraApi,Socket连接IO异常="+e.toString());
				socket = null;
				output = null;
			}catch (Exception e) {
				log.error("CameraApi,Socket连接异常="+e.toString());
			}
			return result;
		}
	}

	/**
	 * 测试摄像头是否正常
	 *
	 */
	public static String testConnect() {
		synchronized (CameraApi.class) {
			String msgInfo = "";
			try {
				if (socket != null && output != null) {
					String msg = "3";
					output.print(msg);

					try {
						msgInfo = in.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						closeSocket();
					}

				}else{

				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				closeSocket();
			}
			return msgInfo;
		}
	}

	public static CameraStatus getCameraStatus() {
		synchronized (CameraApi.class) {
			CameraStatus result = new CameraApi().new CameraStatus();
			try {
				if (socket != null && output != null) {
					String msg = "3";
					output.print(msg);
					String msgInfo = in.readLine();
					if(msgInfo==null||msgInfo.length()!=48){
						result.setCamera0(false);
						result.setCamera1(false);
						result.setErrMessage("Camera Service Exception");
						log.info("CameraApi,Camera Service Exception");
						return result;
					}
				/*	String cameraId0 = msgInfo.substring(0, 1);*/
					String status0=msgInfo.substring(1,2);
					String tmpMes0 = msgInfo.substring(2, 24).trim();
					/*String cameraId1=msgInfo.substring(24, 25);
					String tmpStatus1 = msgInfo.substring(25, 26);
					String tmpMes1 = msgInfo.substring(26, 48).trim();*/
					//龙湖项目只有一个摄像头摄像头0状态正常
					if("1".equals(status0)){
						result.setCamera0(true);
					}else{
						result.setCamera0(false);
						result.setErrMessage(tmpMes0);
					}
				}
			} catch (Exception e) {
				result.setCamera0(false);
			//	result.setCamera1(false);
				result.setErrMessage("Camera Service IO Exception");
				log.error("CameraApi,Camera Service IO Exception");
			}
			return result;
		}
	}

	/**
	 * 设置视频储存路径
	 * @param path
	 * @return
	 */
	public static boolean setVideoPath(String path){
		synchronized (CameraApi.class) {
			boolean result = false;
			try {
				if (socket != null && output != null) {
					String msg = "p="+path;
					output.print(msg);
					String msgInfo = in.readLine();
					if (msgInfo!=null&&msgInfo.equals("ok"))result = true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				closeSocket();
			}
			return result;
		}
	}

	public static void closeSocket() {
		synchronized (CameraApi.class) {
			try {
				int user=count.decrementAndGet();


				if (output != null) {
					output.close();
					output=null;
				}
				if (in != null) {
					in.close();
				}
				if (socket != null) {
					socket.close();
					socket=null;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 停止linux服务
	 * @return
	 */
	public static boolean stopService(){
		boolean result=false;
		try {
			if (socket != null && output != null) {
				String cmd="4";
				output.print(cmd);
				String	resultMessage = in.readLine();

				if(resultMessage.equals("ok"))result=true;
			}
		}catch (Exception e) {

		}finally{
			closeSocket();
		}
		return result;
	}

	/**
	 * socket方式
	 *
	 * @param type
	 *            0开始录制 1结束录制
	 * @param serial_number
	 *            流水号
	 * @param video
	 *            0 摄像头 /dev/video0 摄像头/dev/video1
	 */
	public static String callCamera(final int type, String serial_number,final int

			video) {
		String msgInfo = "";
		if (socket != null && output != null) {
			String msgTmp = "";
			if (type == 0) {
				msgTmp = type + "" + video + serial_number;
			} else {
				msgTmp = type + "" + video;
			}
			String msg = msgTmp;

			output.print(msg);
			try {
				if (type == 0) {
					msgInfo = in.readLine();

				} else if (type == 1) {
					msgInfo = in.readLine();

				} else {

				}

			} catch (IOException e) {
				e.printStackTrace();

			} finally {
				closeSocket();
			}
		}
		return msgInfo;
	}
	public static CameraStatus openCamera(String serial_number, final String cameraId) {
		return openCamera(serial_number,Integer.parseInt(cameraId));
	}
	/**打开摄像头
	 * @param serial_number   视频名字
	 *
	 * @param cameraId   摄像头id(1表示有灯的)
	 */
	public static CameraStatus openCamera(String serial_number, final int cameraId) {
		CameraStatus result=new CameraApi().new CameraStatus();
		boolean isSuccess = false; //标记是否打开成功
		try {
			if (socket != null && output != null) {
				/*
				 * 打开摄像头指令：开（0）+摄像头id（0或者1）+流水号
				 * 返回指令 摄像头id +成功失败（0失败，1成功）
				 */
				String cmd = "0" + cameraId + serial_number;
				String cmdSuccess = cameraId + "1";

				output.print(cmd);
				String msgInfo = in.readLine();
				if(!(msgInfo.length()==24)){

					return result;
				} else {
					String status =  msgInfo.substring(0, 2);
					String message = msgInfo.substring(2,24);
					if (status.equals(cmdSuccess)) {
						isSuccess = true;
						if(cameraId==0){
							camrea0IsOpen=1;
							result.setCamera0(true);
						}else if(cameraId==1){
							camrea1IsOpen=1;
							result.setCamera1(true);
						}
					} else {
						if(cameraId==0){
							camrea0IsOpen=2;
							result.setCamera0(false);
						}
						if(cameraId==1){
							camrea1IsOpen=2;
							result.setCamera1(false);
						}
						result.setErrMessage(message);
					}


				}

			}
		} catch (IOException e) {
			e.printStackTrace();

		} catch (Exception e) {


		}finally {
			closeSocket();
		}
		return result;
	}

	public static CameraStatus closeCamera(String cameraId) {
		return  closeCamera(Integer.parseInt(cameraId));
	}
	/**
	 * 关闭摄像头
	 * @param cameraId
	 *
	 */
	public static CameraStatus closeCamera(int cameraId) {
		CameraStatus result=new CameraApi().new CameraStatus();
		boolean success=false;
		try {
			if (socket != null && output != null) {
				/*
					打开摄像头指令：关（1）+摄像头id（0或者1）
					 返回指令 ：摄像头id +成功失败（0失败，1成功）
				 *
				 */
				String cmd = "1" + cameraId ;
				String cmdSuccess = cameraId + "1";

				output.print(cmd);
				String msgInfo = in.readLine();
				if(!(msgInfo.length()==24)){

					return result;
				} else {
					String status =  msgInfo.substring(0, 2);
					String message = msgInfo.substring(2,24).trim();
					if (status.equals(cmdSuccess)) {
						success=true;
						if(cameraId==0){
							camrea0IsOpen=0;
							result.setCamera0(true);
						}
						if(cameraId==1){
							camrea1IsOpen=0;
							result.setCamera1(true);
						}
					} else {
						if(cameraId==0){
							result.setCamera0(false);
						}
						if(cameraId==1){
							result.setCamera1(false);
						}
						result.setErrMessage(message);
					}

				}

			}else{

			}
		} catch (IOException e) {

		} catch (Exception e) {

		}finally {
			closeSocket();
		}
		return result;
	}

	/**
	 * 同时打开两个摄像头
	 * @param serial_number 流水号，传null 流水号为test
	 * @return
	 */
	public static  CameraStatus openCamera2(String serial_number){
		CameraStatus result=new CameraApi().new CameraStatus();
		if(serial_number==null)serial_number="test";
		String msgTmp =   "00" + serial_number + "|"  + "01"+ serial_number;
		try {
			if (socket != null && output != null) {
				output.print(msgTmp);
				String resultMsg= in.readLine();

				result= getStauts(resultMsg,true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setErrMessage(e.toString());

		}finally{
			closeSocket();
		}
		int cameraId = 99;//标记双摄像头
		return result;
	}

	/**
	 * 同时关闭两个摄像头
	 * @return
	 */
	public static  CameraStatus closeCamera2(){
		CameraStatus result=new CameraApi().new CameraStatus();

		String msgTmp =  "10|11";
		try {
			if (socket != null && output != null) {
				output.print(msgTmp);
				String resultMsg= in.readLine();


				result=getStauts(resultMsg,false);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setErrMessage(e.toString());
		}finally{
			closeSocket();
		}
		return result;
	}
	/**
	 * 获取 videomp4程序的版本号
	 * @return
	 */
	public static String getVersionCode() {
		String result = null;
		try {
			if (socket != null && output != null) {
				output.print("2");
				result = in.readLine();

			}
		} catch (Exception e) {

		}finally{
			closeSocket();
		}
		return result;
	}
	/**
	 * socket方式
	 *
	 * @param type
	 *            0开始录制 1结束录制
	 * @param serial_number
	 *            流水号
	 * @param
	 *            /关闭两个摄像头
	 */
	public static String callCamera(final int type, String serial_number) {
		String msgInfo = "";
		if (socket != null && output != null) {
			String msgTmp = "";
			if (type == 0) {
				msgTmp = type + "0" + serial_number + "|" + type + "1"
						+ serial_number;
			} else {
				msgTmp = type + "0" + "|" + type + "1";
			}
			String msg = msgTmp;
			output.print(msg);

			try {
				if (type == 0) {
					msgInfo = in.readLine();

				} else if (type == 1) {
					msgInfo = in.readLine();


				} else {

				}
			} catch (IOException e) {
				e.printStackTrace();

			} finally {
				closeSocket();
			}
		}
		return msgInfo;
	}

	/**
	 * 打开/关闭 两个摄像头,返回值进行判断
	 * @param resultMsg
	 * @return
	 */
	public static   CameraStatus getStauts(String resultMsg ,boolean openOrClose){
		CameraStatus result=new CameraApi().new CameraStatus();
		if(resultMsg==null||resultMsg.length()!=48){
			result.setCamera0(false);
			result.setCamera1(false);
			return result;
		}
		String tmpStatus0 = resultMsg.substring(0, 2);
		String tmpMes0 = resultMsg.substring(2, 24).trim();
		String tmpStatus1 = resultMsg.substring(24, 26);
		String tmpMes1 = resultMsg.substring(26, 48).trim();

		if(tmpStatus0.equals("01")&&tmpStatus1.equals("11")){//两个摄像头都打开成功
			result.setCamera0(true);
			result.setCamera1(true);
			if(openOrClose){
				camrea0IsOpen=1;
				camrea1IsOpen=1;
			}else{
				camrea0IsOpen=0;
				camrea1IsOpen=0;
			}
		}else if(tmpStatus0.equals("00")&&tmpStatus1.equals("11")){//0失败，1成功
			result.setCamera0(false);
			result.setCamera1(true);
			if(openOrClose){
				camrea1IsOpen=1;
				result.setErrMessage("广告机摄像头0打开失败"+":"+tmpMes0+",摄像头1打开成功");
				log.error("CameraApi,广告机摄像头0打开失败"+":"+tmpMes0+",摄像头1打开成功");
			}else {
				result.setErrMessage("广告机摄像头0关闭失败"+":"+tmpMes0+",摄像头1关闭成功");
				log.error("CameraApi,广告机摄像头0打开失败"+":"+tmpMes0+",摄像头1打开成功");
			}
		}else if(tmpStatus0.equals("01")&&tmpStatus1.equals("10")){
			result.setCamera0(true);
			result.setCamera1(false);
			camrea0IsOpen=0;
			if(openOrClose){
				result.setErrMessage("广告机摄像头0打开成功"+",摄像头1打开失败"+":"+tmpMes1);
				log.error("CameraApi,广告机摄像头0打开成功"+",摄像头1打开失败"+":"+tmpMes1);
			}
			else {
				result.setErrMessage("广告机摄像头0关闭成功"+",摄像头1关闭失败"+":"+tmpMes1);
				log.error("CameraApi,广告机摄像头0关闭成功"+",摄像头1关闭失败"+":"+tmpMes1);
			}
		}else if(tmpStatus0.equals("00")&&tmpStatus1.equals("10")){
			result.setCamera0(false);
			result.setCamera1(false);
			if(openOrClose) {
				result.setErrMessage("广告机两个摄像头都打开失败"+",摄像头0:"+tmpMes0+",摄像头1:"+tmpMes1);
				log.error("CameraApi,广告机两个摄像头都打开失败"+",摄像头0:"+tmpMes0+",摄像头1:"+tmpMes1);
			}
			else{
				result.setErrMessage("广告机两个摄像头都关闭失败"+",摄像头0:"+tmpMes0+",摄像头1:"+tmpMes1);
				log.error("CameraApi,广告机两个摄像头都关闭失败"+",摄像头0:"+tmpMes0+",摄像头1:"+tmpMes1);
			}
		}
		return result;
	}
	public class CameraStatus{
		boolean camera0;
		boolean camera1;
		String errMessage;
		public boolean isCamera0() {
			return camera0;
		}
		public void setCamera0(boolean camera0) {
			this.camera0 = camera0;
		}
		public boolean isCamera1() {
			return camera1;
		}
		public void setCamera1(boolean camera1) {
			this.camera1 = camera1;
		}
		public String getErrMessage() {
			return errMessage;
		}
		public void setErrMessage(String errMessage) {
			this.errMessage = errMessage;
		}
		@Override
		public String toString() {
			return "CameraStatus [camera0=" + camera0 + ", camera1=" + camera1
					+ ", errMessage=" + errMessage + "]";
		}


	}


	public static int getCamrea0IsOpen() {
		return camrea0IsOpen;
	}

	public static void setCamrea0IsOpen(int camrea0IsOpen) {
		CameraApi.camrea0IsOpen = camrea0IsOpen;
	}

	public static int getCamrea1IsOpen() {
		return camrea1IsOpen;
	}

	public static void setCamrea1IsOpen(int camrea1IsOpen) {
		CameraApi.camrea1IsOpen = camrea1IsOpen;
	}

	/**
	 * 删除过期文件
	 * @param path 文件目录
	 */
	public static void deleteOldFile(String path,String saveTime){
		File realFile = new File(path);
		int saveDay = Integer.parseInt(saveTime);
		if (realFile.exists()){
			if (realFile.isDirectory()) {
				File[] subfiles = realFile.listFiles();
				for (File file : subfiles) {
					String time = file.getName();

					String currTime = new SimpleDateFormat("yyyyMMdd")
							.format(System.currentTimeMillis());
					long day = dateDiff(currTime,time);
					if (day>saveDay){
						deleteDirWihtFile(file);
					}
				}
			}
		}
	}

	private static void deleteDirWihtFile(File dir) {
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); // 删除所有文件
			else if (file.isDirectory())
				deleteDirWihtFile(file); // 递规的方式删除文件夹
		}
		dir.delete();// 删除目录本身
	}

	private static long dateDiff(String startTime, String endTime) {
		// 按照传入的格式生成一个simpledateformate对象
		DateFormat format = new SimpleDateFormat("yyyyMMdd HHmmss");
		long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
		long diff;
		long day = 0;
		// 获得两个时间的毫秒时间差异
		try {
			long a = format.parse(startTime+" 000000").getTime();
			long b = format.parse(endTime+" 000000").getTime();
			diff = a - b;
			day = diff / nd;// 计算差多少天
			// 输出结果
			if (day>=1) {
				return day;
			}else {
				if (day==0) {
					return 1;
				}else {
					return 0;
				}

			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
