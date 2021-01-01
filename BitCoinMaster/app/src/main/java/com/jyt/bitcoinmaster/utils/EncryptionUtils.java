package com.jyt.bitcoinmaster.utils;


import org.apache.commons.lang.StringUtils;

/**
 * 加密解密工具
 * 
 * @author lcl
 */
public class EncryptionUtils {
	/**
	 * 最新加密版本（注：只可使用3位）
	 */
	private static final String NEWEST_VERSION = "V2_";

	private static final String key = "R23y/sFg7XDo1TFR3MJm9XivqFSHhaXGwuz+59A6kWo=";

	/**
	 * AES对称加密
	 * 
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String content) throws Exception {
		if(StringUtils.isBlank(content)) {
			return content;
		}
		return NEWEST_VERSION + EncryptUtilsV2.encrypt(content,key);
	}

	/**
	 * AES 对称解密
	 * 
	 * @param content 加密内容
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String content) throws Exception {
		if(StringUtils.isBlank(content)) {
			return content;
		}
		if(content.length() < 3) {
			return EncryptUtilsV1.decrypt(content);
		}
		if(content.startsWith(NEWEST_VERSION)) {
			return EncryptUtilsV2.decrypt(content.substring(3),key);
		} else {
			return EncryptUtilsV1.decrypt(content);
		}
	}
	
}