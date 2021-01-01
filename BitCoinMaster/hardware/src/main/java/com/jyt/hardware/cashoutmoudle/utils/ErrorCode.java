package com.jyt.hardware.cashoutmoudle.utils;

public class ErrorCode {
	/**
	 * 未知主机
	 */
public static final int UnknownHost=996;
public static final String UnknownHostMessage="未知主机";
/**
 *  数据校验不合法(crc错)
 */
public static final int ErrorData=998;
public static final String ErrorDataMessage="数据校验不合法(crc错)";
/**
 * 数据头校验不合法
 */
public static final int ErrorHead=994;
public static final String ErrorHeadMessage="数据头校验不合法";
/**
 * 数据长度不正确
 */
public static final int ErrorLength=993;
public static final String ErrorLengthMessage="数据长度不正确";
/**
 * 不正确的命令
 */
public static final int ErrorCommond=992;
public static final String ErrorCommondMessage="不正确的命令";
/**
 * 超时
 */
public static final int TimeOut=999;
public static final String TimeOutMessage="出钞服务超时";
/**
 * io异常
 */
public static final int IOException=995;
public static final String IOExceptionMessage="io异常";
/**
 * 解析返回数据异常
 */
public static final int ParsingException=991;
public static final String ParsingExceptionMessage="解析返回数据异常";
/**
 * key为null
 */
public static final int KeyIsNull=997;
public static final String KeyIsNullMessage="key为null";

/**
 * 接收数据非法
 */
public static final int ErrorReData=990;
public static final String ErrorReDataMessage="接收数据非法";

 
/**
 * 冠字码不完整
 */
public static final  String SNErrorMessage="863";
public static final String SNErrorMessageInfo="冠字码不完整";

	public static String getErrorMessage(int code) {
		String message = null;
		switch (code) {
		case ErrorReData:
			message = ErrorReDataMessage;
			break;

		case KeyIsNull:
			message = KeyIsNullMessage;
			break;
		case ParsingException:
			message = ParsingExceptionMessage;
			break;
		case IOException:
			message = IOExceptionMessage;
			break;
		case TimeOut:
			message = TimeOutMessage;
			break;
		case ErrorCommond:
			message = ErrorCommondMessage;
			break;
		case ErrorLength:
			message = ErrorLengthMessage;
			break;
		case ErrorHead:
			message = ErrorHeadMessage;
			break;
		case ErrorData:
			message = ErrorDataMessage;
			break;
		case UnknownHost:
			message = UnknownHostMessage;
			break;
		default:
			message = "未知";
			break;
		}
		return message;
	}
}
