package com.jyt.hardware.utils;

 

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
 
public class CommonConstants {
	
	 
	public static int log_INFO = 0;
	public static int log_DEBUG = 1;
	public static int log_WARNING = 2;
	public static int log_ERROR = 3;
 

	
	// 日志引擎 日期格式化的格式
	@SuppressLint("SimpleDateFormat")
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
	@SuppressLint("SimpleDateFormat")
	public static final SimpleDateFormat SDF1 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");  
	@SuppressLint("SimpleDateFormat")
	public static final SimpleDateFormat SDF2 = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");  
}