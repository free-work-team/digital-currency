package com.jyt.terminal.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jyt.terminal.commom.enums.BitException;
import com.jyt.terminal.commom.enums.TradeRetCodeEnum;
import com.jyt.terminal.support.StrKit;

/**
 * 加密解密工具
 * 
 * @author lcl
 */
public class EncryptionUtils {
	private static Logger logger = LoggerFactory.getLogger(EncryptionUtils.class);
	/**
	 * 最新加密版本（注：只可使用3位）
	 */
	private static final String NEWEST_VERSION = "V2_";
	
	/**
	 * AES对称加密
	 * 
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String content) throws BitException {
		try {
			if(StrKit.isBlank(content)) {
				return content;
			}
			return NEWEST_VERSION + EncryptUtilsV2.encrypt(content);
		} catch (Exception e) {
			logger.error("加密错误",e);
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
			if(StrKit.isBlank(content)) {
				return content;
			}
			if(content.length() < 3) {
				return EncryptUtilsV1.decrypt(content);
			}
			if(content.startsWith(NEWEST_VERSION)) {
				return EncryptUtilsV2.decrypt(content.substring(3));
			} else {
				return EncryptUtilsV1.decrypt(content);
			}
		} catch (Exception e) {
			logger.error("解密错误",e);
			throw new BitException(TradeRetCodeEnum.TRADE_ERROR_DECRYPT_ERROR);
		}
		
	}
	
}