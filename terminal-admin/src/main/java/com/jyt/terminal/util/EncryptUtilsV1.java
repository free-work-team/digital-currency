/**
 * 
 */
package com.jyt.terminal.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jyt.terminal.commom.enums.BitException;
import com.jyt.terminal.commom.enums.TradeRetCodeEnum;

/**

 *
 */
public class EncryptUtilsV1 {
	private static Logger logger = LoggerFactory.getLogger(EncryptUtilsV1.class);
	private static final byte[] DEFAULT_KEY = new byte[] { 65, 68, 52, 50, 70, 54, 54, 57, 55, 66, 48, 51, 75, 66, 55, 53 };

	/**
	 * 默认字符集
	 */
	public static final String CHARSET_NAME = "UTF-8";
	
	/**
	 * AES对称加密
	 * 
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String content) throws BitException {
		try {
			if(StringUtils.isBlank(content)) {
				return null;
			}
			return symmetricncrypt(content, DEFAULT_KEY);
		} catch (Exception e) {
			logger.warn("加密错误",e);
			throw new BitException(TradeRetCodeEnum.TRADE_ERROR_ENCRYPT_ERROR);
		} 
	}

	/**
	 * AES 对称解密
	 * 
	 * @param content 加密内容
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String content) throws BitException {
		try {
			return symmetricDecrypt(content, DEFAULT_KEY);
		} catch (Exception e) {
			logger.error("解密错误",e);
			// 考虑到之前有些数据没有加密，如果出错，原文返回。
			return content;
		} 
		
	}
	
	/**
	 * AES对称加密
	 * 
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String symmetricncrypt(String content) throws Exception {
		return symmetricncrypt(content, DEFAULT_KEY);
	}

	/**
	 * AES 对称解密
	 * 
	 * @param content 加密内容
	 * @return
	 * @throws Exception
	 */
	public static String symmetricDecrypt(String content) throws Exception {
		return symmetricDecrypt(content, DEFAULT_KEY);
	}

	/**
	 * AES 加密
	 * 
	 * @param content 加密内容
	 * @param key 16位秘钥
	 * @return
	 * @throws Exception
	 */
	public static String symmetricncrypt(String content, byte[] key) throws Exception {
		if (key == null || key.length != 16) {
			throw new BitException(TradeRetCodeEnum.TRADE_ERROR_TREQUEST_PARAM_ILLEGAL);
		}
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// "算法/模式/补码方式"
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(content.getBytes(CHARSET_NAME));
		return Base64.getEncoder().encodeToString(encrypted);// 此处使用BASE64做转码功能，同时能起到2次加密的作用。
	}

	/**
	 * AES 解密
	 * 
	 * @param content 加密内容
	 * @param key 16位秘钥
	 * @return
	 * @throws Exception
	 */
	public static String symmetricDecrypt(String content, byte[] key) throws Exception {
		// 判断Key是否为16位
		if (key == null || key.length != 16) {
			throw new BitException(TradeRetCodeEnum.TRADE_ERROR_TREQUEST_PARAM_ILLEGAL);
		}
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] encrypted1 = Base64.getDecoder().decode(content);// 先用base64解密
		byte[] original = cipher.doFinal(encrypted1);
		String originalString = new String(original, CHARSET_NAME);
		return originalString;
	}
	
	public static void main(String[] args) {
		System.out.println(decrypt("sz6ZOF2Hket7q+mPm6HzeUhLUJd6b4y+oEqv/kFQj40="));
	}
}
