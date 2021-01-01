package com.jyt.hardware.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DesUtils {
	
	private final static byte[] hex = "0123456789ABCDEF".getBytes();

	private static int parse(char c) {
		if (c >= 'a')
			return (c - 'a' + 10) & 0x0f;
		if (c >= 'A')
			return (c - 'A' + 10) & 0x0f;
		return (c - '0') & 0x0f;
	}
	// 从字节数组到十六进制字符串转换
	public static String Bytes2HexString(byte[] b) {
		byte[] buff = new byte[2 * b.length];
		for (int i = 0; i < b.length; i++) {
			buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
			buff[2 * i + 1] = hex[b[i] & 0x0f];
		}
		return new String(buff);
	}

	// 从十六进制字符串到字节数组转换
	public static byte[] HexString2Bytes(String hexstr) {
		byte[] b = new byte[hexstr.length() / 2];
		int j = 0;
		for (int i = 0; i < b.length; i++) {
			char c0 = hexstr.charAt(j++);
			char c1 = hexstr.charAt(j++);
			b[i] = (byte) ((parse(c0) << 4) | parse(c1));
		}
		return b;
	}

	 

	// 加解密模式
	private	static int mode = Cipher.ENCRYPT_MODE;
	/**
	 * 加解密算法
	 * @param data  加解密数据
	 * @param key   秘钥
	 * @return      加解密结果
	 */
	public static byte[] desCryt(byte[] data, byte[] key){
		byte[] result = null ;
		try {
			SecureRandom sr = new SecureRandom();  
			SecretKeyFactory keyFactory;
			DESKeySpec dks = new DESKeySpec(key);
			keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretkey = keyFactory.generateSecret(dks); 
			//创建Cipher对象
			Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");  
			//初始化Cipher对象  
			cipher.init(mode, secretkey, sr);  
			//加解密  
			result = cipher.doFinal(data); 
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} 
		
		return result;
	}
	
	/**
	 * 字符串转16进账字符
	 * @param s
	 * @return
	 */
	public static String toHexString(String s) {  
        String str = "";  
        for (int i = 0; i < s.length(); i++) {  
            int ch = (int) s.charAt(i);  
            String s4 = Integer.toHexString(ch);  
            str = str + s4;  
        }  
        return str;//0x表示十六进制  
    }  
	
	 /**
	  * 16进制字符串转成byte数组
	 * @param src
	 * @return
	 */
	public static byte[] hexString2Bytes(String src){
		    byte[] ret = new byte[8];
		    byte[] tmp = src.getBytes();
		    for(int i=0; i<8; i++){
		      ret[i] = uniteBytes(tmp[i*2], tmp[i*2+1]);
		    }
		    return ret;
	 }
	 public static byte uniteBytes(byte src0, byte src1) {
		    byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
		    _b0 = (byte)(_b0 << 4);
		    byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
		    byte ret = (byte)(_b0 ^ _b1);
		    return ret;
	}
	 public static String Decrypt(byte[] data, byte[] key) throws Exception {
		byte[] receiveRespond = decrypt(data, key);
		return Bytes2HexString(receiveRespond);
	 }
	 /**
	 * 解密
	 * @param src byte[]
	 * @param password String
	 * @return byte[]
	 * @throws Exception
	 */
	 public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
	 // DES算法要求有一个可信任的随机数源
	 SecureRandom random = new SecureRandom();
	 // 创建一个DESKeySpec对象
	 DESKeySpec desKey = new DESKeySpec(key);
	 // 创建一个密匙工厂
	 SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
	 // 将DESKeySpec对象转换成SecretKey对象
	 SecretKey securekey = keyFactory.generateSecret(desKey);
	 // Cipher对象实际完成解密操作
	 Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
	 // 用密匙初始化Cipher对象
	 cipher.init(Cipher.DECRYPT_MODE, securekey, random);
	 // 真正开始解密操作
	 return cipher.doFinal(src);
	 }
}
