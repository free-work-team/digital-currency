package com.jyt.hardware.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.jyt.hardware.R;


/**
 * sharePreferences 类
 * 
 *
 */
public class Preferences {

	private static Logger log =  Logger.getLogger("BitCoinMaster");

	private static Preferences cashErrorMessage;

	private Preferences() {

	}

	public static Preferences getCashErrorMessageInstance() {
		if (null == cashErrorMessage) {
			cashErrorMessage = new Preferences();
		}
		return cashErrorMessage;
	}

	/**
	 * 保存POS机器sn
	 * @param sn
	 *
	 */
	public static void setPOSSN(Context context,String sn){
		SharedPreferences share = context.getSharedPreferences("cashCode", Context.MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString("sn", sn);
		editor.commit();
	}
	
	/**
	 * 获取POS sn
	 *
	 *
	 */
	public static String getPOSSN(Context context){
		SharedPreferences share = context.getSharedPreferences("cashCode", Context.MODE_PRIVATE);
		return	share.getString("sn", null);
	}
	
	/**
	 * 保存出钞模块机器sn
	 * @param sn
	 *
	 */
	public static void setMachineSN(Context context,String sn){
		SharedPreferences share = context.getSharedPreferences("cashCode", Context.MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString("MachineSN", sn);
		editor.commit();
	}
	/**
	 * 获取出钞模块 sn
	 *
	 *
	 */
	public static String getMachineSN(Context context){
		SharedPreferences share = context.getSharedPreferences("cashCode", Context.MODE_PRIVATE);
		return	share.getString("MachineSN", null);
	}
	
	/**
	 * 保存出钞模块类型
	 * @param context
	 * @param type
	 */
	public static void setCDDtype(Context context,int type){
		SharedPreferences share = context.getSharedPreferences("CDM", Context.MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putInt("type", type);
		editor.commit();
	}
	
	/**
	 * 获取出钞模块类型
	 * @param context
	 * @return
	 */
	public static int getCDMType(Context context){
		SharedPreferences share = context.getSharedPreferences("CDM", Context.MODE_PRIVATE);
		return	share.getInt("type", 0);
	}
	/**
	 * 获取逻辑码和物理码对应的广电出钞模块错误描述
	 * @param context
	 * @param logicCode
	 * @param PhyCode
	 * @return
	 */
	public   String getCashlogicCodeMessage(Context context, String logicCode, String PhyCode){
		if (null == logicCode || null == PhyCode || logicCode.equals("0"))
			return "";
		try {
			log.info("[Preferences]  logicCode:" + logicCode + ",  PhyCode:" + PhyCode);
			JSONObject obj =Preferences.getCashErrorMessageInstance(). readFromRaw(context);
			JSONObject PhyCodeObj = obj.getJSONObject(logicCode);
			return  PhyCodeObj.getString(PhyCode);
		}catch (Exception e) {
			log.error("[Preferences] logicCode:" + logicCode + "  PhyCode:" + PhyCode+",错误："+e.toString());
		}
		return null;
	}
	
	public void setCashCode(Context context, int logicCode, int PhyCode) {
		log.error("[Preferences] :AndroidHost  \r  logicCode:" + logicCode + "  PhyCode:" + PhyCode);
		setCashCode(context,logicCode+"",PhyCode+"");
	}
	/**
	 * 记录逻辑码、物理码对应的错误信息
	 * 
	 * @param context
	 * @param logicCode
	 * @param PhyCode
	 */
	public void setCashCode(Context context, String logicCode, String PhyCode) {
		if (null == logicCode || null == PhyCode )return;
		if(logicCode.equals("0")&&PhyCode.equals("0")) {//正常情况
				log.info("正常情况,logicCode:" + logicCode + "  PhyCode:" + PhyCode);
				SharedPreferences share = context.getSharedPreferences("cashCode", Context.MODE_PRIVATE);
				Editor editor = share.edit();
				editor.putString("logicCode", logicCode);
				editor.putString("PhyCode", PhyCode);
				editor.putString("ErrorMessage", "正常");// 提示
				editor.putString("ErrorType", "正常");
				editor.putString("ErrorFlag", "0");
				editor.putString("Time", CommonConstants.SDF1.format(new Date()));
				editor.commit();
		}else{
			try {
				log.info("logicCode:" + logicCode + "  PhyCode:" + PhyCode);
				JSONObject obj = readFromRaw(context);
				JSONObject PhyCodeObj = obj.getJSONObject(logicCode);
				String errorMessage = PhyCodeObj.getString(PhyCode);
				String errorType = PhyCodeObj.getString("errorType");
				String errorFlag = PhyCodeObj.getString("errorFlag");
				SharedPreferences share = context.getSharedPreferences("cashCode", Context.MODE_PRIVATE);
				Editor editor = share.edit();
				editor.putString("logicCode", logicCode);
				editor.putString("PhyCode", PhyCode);
				editor.putString("ErrorMessage", errorMessage);// 提示
				editor.putString("ErrorType", errorType);
				editor.putString("ErrorFlag", errorFlag);
				editor.putString("Time", CommonConstants.SDF1.format(new Date()));
				editor.commit();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 记录逻辑码、物理码对应的错误信息
	 * 
	 * @param context
	 * @param cashInitState
	 *            初始化状态
	 * @param logicCode
	 *            逻辑代码
	 * @param PhyCode
	 *            物理代码
	 */
	public void setCashCode(Context context, boolean cashInitState, String logicCode, String PhyCode) {
		if (null == logicCode || null == PhyCode)
			return;
		try {
			JSONObject obj = readFromRaw(context);
			JSONObject PhyCodeObj = obj.getJSONObject(logicCode);
			String errorMessage = PhyCodeObj.getString(PhyCode);
			String errorType = PhyCodeObj.getString("errorType");
			String errorFlag = PhyCodeObj.getString("errorFlag");
			SharedPreferences share = context.getSharedPreferences("cashCode", Context.MODE_PRIVATE);
			Editor editor = share.edit();
			editor.putBoolean("cashInitState", cashInitState);
			editor.putString("logicCode", logicCode);
			editor.putString("PhyCode", PhyCode);
			editor.putString("ErrorMessage", errorMessage);// 提示
			editor.putString("ErrorType", errorType);
			editor.putString("ErrorFlag", errorFlag);
			editor.putString("Time", CommonConstants.SDF1.format(new Date()));
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 记录出钞模块、电动门、led状态
	 * 
	 *
	 */
	public void setElectricityDoorState(Context context, boolean cashInitState, boolean ElectricityDoorState,
			boolean LEDStateSuccess) {
		SharedPreferences share = context.getSharedPreferences("cashCode", Context.MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putBoolean("cashInitState", cashInitState);
		editor.putBoolean("ElectricityDoorState", ElectricityDoorState);
		editor.putBoolean("LEDStateSuccess", LEDStateSuccess);
		editor.putString("Time", CommonConstants.SDF1.format(new Date()));
		editor.commit();
	}

	public void setCashInitState(Context context, boolean cashInitState) {
		SharedPreferences share = context.getSharedPreferences("cashCode", Context.MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putBoolean("cashInitState", cashInitState);
		editor.putString("Time", CommonConstants.SDF1.format(new Date()));
		editor.commit();
	}
	
	/**
	 * 设置电动门状态
	 * @param context
	 * @param ElectricityDoorState  电动门状态
	 */
	public void setElectricityDoorState(Context context, boolean ElectricityDoorState) {
		SharedPreferences share = context.getSharedPreferences("cashCode", Context.MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putBoolean("ElectricityDoorState", ElectricityDoorState);
		editor.putString("Time", CommonConstants.SDF1.format(new Date()));
		editor.commit();
	}
	
	/**
	 * 获取逻辑码、物理码对应的错误信息
	 * 
	 * @return
	 */
	public CashCode getErrorMessage(Context context) {
		SharedPreferences share = context.getSharedPreferences("cashCode", Context.MODE_PRIVATE);
		CashCode cashCode = new CashCode();
		String message = share.getString("ErrorMessage", "钞箱未初始化");
		String logicCode = share.getString("logicCode", "-1");
		String phyCode = share.getString("PhyCode", "-1");
		String errorType = share.getString("ErrorType", "0");
		String errorFlag = share.getString("ErrorFlag", "0");
		String time = share.getString("Time", "000000");
		boolean cashInitState = share.getBoolean("cashInitState", true);
		boolean electricityDoorState = share.getBoolean("ElectricityDoorState", true);
		boolean LEDState = share.getBoolean("LEDStateSuccess", true);
		cashCode.setCashInitState(cashInitState);
		cashCode.setlogicCode(logicCode);
		cashCode.setPhyCode(phyCode);
		cashCode.setMessage(message);
		cashCode.setErrorFlag(errorFlag);
		cashCode.setErrorType(errorType);
		cashCode.setElectricityDoorState(electricityDoorState);
		cashCode.setLEDState(LEDState);
		cashCode.setTime(time);
		return cashCode;
	}
	
	
	public CashCode getErrorMessage(Context context,int logicCode,int PhyCode) {
		CashCode cashCode=new CashCode();
		if (0 == logicCode || 0 == PhyCode){
			cashCode.setErrorFlag("0");
			cashCode.setlogicCode(logicCode+"");
			cashCode.setPhyCode(PhyCode+"");
			return cashCode;
		}
		try {
			JSONObject obj = readFromRaw(context);
			JSONObject PhyCodeObj = obj.getJSONObject(logicCode+"");
			String errorMessage = PhyCodeObj.getString(PhyCode+"");
			String errorType = PhyCodeObj.getString("errorType");
			String errorFlag = PhyCodeObj.getString("errorFlag");
			SharedPreferences share = context.getSharedPreferences("cashCode", Context.MODE_PRIVATE);
			Editor editor = share.edit();
			editor.putString("logicCode", logicCode+"");
			editor.putString("PhyCode", PhyCode+"");
			editor.putString("ErrorMessage", errorMessage);// 提示
			editor.putString("ErrorType", errorType);
			editor.putString("ErrorFlag", errorFlag);
			editor.putString("Time", CommonConstants.SDF1.format(new Date()));
			editor.commit();
			cashCode.setMessage(errorMessage);
			cashCode.setErrorFlag(errorFlag);
			cashCode.setErrorType(errorType);
			cashCode.setlogicCode(logicCode+"");
			cashCode.setPhyCode(PhyCode+"" );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cashCode;
	}
	/**
	 * 从raw中读取txt,返回json对象
	 */
	private JSONObject readFromRaw(Context context) {
		try {
			InputStream is = context.getResources().openRawResource(R.raw.package1);
			String text = readTextFromSDcard(is);
			return new JSONObject(text);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 按行读取txt
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 */
	private String readTextFromSDcard(InputStream is) throws Exception {
		InputStreamReader reader = new InputStreamReader(is);
		BufferedReader bufferedReader = new BufferedReader(reader);
		StringBuffer buffer = new StringBuffer();
		String str;
		while ((str = bufferedReader.readLine()) != null) {
			buffer.append(str);
			buffer.append("\n");
		}
		return buffer.toString();
	}

	public class CashCode {
		boolean cashInitState;// 钞箱初始化状态I
		String logicCode;
		String PhyCode;
		// 错误信息
		String message;
		// 类型：正常  警告码,设备错  操作错  钱箱错  传输错
		String errorType;
		// 时间
		String time;
		// errorFlag 0: 警告码，1:设备错,2:操作错,3:钱箱错,4:传输错
		String ErrorFlag;
		boolean ElectricityDoorState;// 电动门状态
		boolean LEDState;// LED等状态

		public boolean isCashInitState() {
			return cashInitState;
		}

		public void setCashInitState(boolean cashInitState) {
			this.cashInitState = cashInitState;
		}

		public String getlogicCode() {
			return logicCode;
		}

		public void setlogicCode(String logicCode) {
			logicCode = logicCode;
		}

		public String getPhyCode() {
			return PhyCode;
		}

		public void setPhyCode(String phyCode) {
			PhyCode = phyCode;
		}

		public String getMessage() {
			return message;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getErrorFlag() {
			return ErrorFlag;
		}

		public void setErrorFlag(String errorFlag) {
			ErrorFlag = errorFlag;
		}

		public String getErrorType() {
			return errorType;
		}

		public void setErrorType(String errorType) {
			this.errorType = errorType;
		}

		public boolean isElectricityDoorState() {
			return ElectricityDoorState;
		}

		public void setElectricityDoorState(boolean electricityDoorState) {
			ElectricityDoorState = electricityDoorState;
		}

		public boolean isLEDState() {
			return LEDState;
		}

		public void setLEDState(boolean lEDState) {
			LEDState = lEDState;
		}

		@Override
		public String toString() {
			return "cashInitState  : " + cashInitState + "  , LEDStateSuccess : " + LEDState + "  , ElectricityDoorState  : "
					+ ElectricityDoorState + "  ,logicCode : " + logicCode + "  PhyCode : " + PhyCode + "  message : "
					+ message + "  errorType : " + errorType + "   , ErrorFlag : " + ErrorFlag
					+ "  , time : " + time;

		}
	}
}
