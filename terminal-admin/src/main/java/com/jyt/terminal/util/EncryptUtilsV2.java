package com.jyt.terminal.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.jyt.terminal.commom.enums.BitException;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.support.ResourceKit;
import com.jyt.terminal.support.StrKit;

/**
 * AES加密
 */
public class EncryptUtilsV2 {
	private static final String CHARSET = "UTF8";

	private static final String ALGORITHM = "AES";

	private static Logger log = LoggerFactory.getLogger(EncryptUtilsV2.class);

	// 指定DES加密解密所用的秘钥
	private static Key key = null;

	// 加密用KEY
	private static String DEFALUT_KEY_PATH = "classpath:default-encrypt.key";
	private static String DEFAULT_KEY_MD5 = "4404d1a3015253d16d2894a4abf86752";

	/**
	 * 初始化秘钥
	 */
	private static void init() {
		log.info("开始初始化秘钥");
		SpringEnvKit kit = SpringContextHolder.getBean("springEnvKit");
		String keyPath = kit.getEnv("hru.encrypt.keyPath");
		String keyMd5 = kit.getEnv("hru.encrypt.keyMd5");
		if (StrKit.isBlank(keyPath)) {
			// 生产环境强制必须有秘钥文件
			/*if (SpringContextHolder.SPRING_ACTIVE_PROFILE_PRODUCT.equals(SpringContextHolder.getActiveProfile())) {
				log.error("hru.encrypt.keyPath未配置");
				throw new BitException(BizExceptionEnum.SYS_CONFIG_ERROR_ENCRYPT_KEY_MISSED);
			} else {*/
				keyPath = DEFALUT_KEY_PATH;
				keyMd5 = DEFAULT_KEY_MD5;
			/*}*/
		}
		if(StrKit.isBlank(keyMd5)) {
			log.error("没有配置hru.encrypt.keyPath对应的md5值");
			throw new BitException(BizExceptionEnum.SYS_CONFIG_ERROR_ENCRYPT_KEY_MISSED);
		}
		
		try {
			Resource[] resources = ResourceKit.getResources(keyPath);
			if (resources != null && resources.length == 1) {
				InputStream is = resources[0].getInputStream();
				key = readKey(is, keyMd5);
			} else {
				log.error("hru.encrypt.keyPath配置有误, {}", keyPath);
				throw new BitException(BizExceptionEnum.SYS_CONFIG_ERROR_ENCRYPT_KEY_MISSED);
			}
		} catch (Exception e1) {
			log.error("加载" + keyPath + "配置文件出错", e1);
			throw new BitException(BizExceptionEnum.SYS_CONFIG_ERROR_ENCRYPT_KEY_PARSE_ERROR);
		}
	}

	// 对字符串进行DES加密，返回BASE64编码的加密字符串
	public static String encrypt(String str) {
		try {
			if (key == null) {
				synchronized (DEFALUT_KEY_PATH) {
					if (key == null) {
						init();
					}
				}
			}
			Base64 base64 = new Base64();
			byte[] strBytes = str.getBytes(CHARSET);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] encryptStrBytes = cipher.doFinal(strBytes);
			return base64.encodeToString(encryptStrBytes);
		} catch (Exception e) {
			log.error("加密失败。", e);
			throw new RuntimeException(e);
		}
	}

	// 对BASE64编码的加密字符串进行解密，返回解密后的字符串
	public static String decrypt(String str) {
		Base64 base64 = new Base64();
		try {
			if (key == null) {
				synchronized (DEFALUT_KEY_PATH) {
					if (key == null) {
						init();
					}
				}
			}
			byte[] strBytes = base64.decode(str);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decryptStrBytes = cipher.doFinal(strBytes);
			return new String(decryptStrBytes, CHARSET);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// 对入参的字符串进行加密，打印加密后的串
	public static void main(String[] args) throws Exception {
		String seed;
		String path = null;
		if(args != null && args.length >= 1) {
			seed = args[0];
			if(args.length > 1) {
				path = args[1];
			}
		} else {
			seed = "aeW-$i7Y-kkZ8-mmYn-akx~";
		}
		generateKey(path, seed);
	}

	/**
	 * 模拟接收方 读取文件中的秘钥，进行加解密
	 * 
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 */
	public static SecretKey readKey(InputStream inputStream, String keyMd5) throws IOException, GeneralSecurityException {
		// 1.读取文件中的密钥
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream); // 得到文件的字符流
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader); // 放入读取缓冲区
		String readd;
		StringBuffer stringBuffer = new StringBuffer(100);
		while ((readd = bufferedReader.readLine()) != null) {
			stringBuffer.append(readd);
		}
		IOUtils.closeQuietly(inputStream);
		
		String keystr = stringBuffer.toString();
		String keystrMd5 = MD5Util.encrypt(keystr);
		if(!keyMd5.equals(keystrMd5)) {
			log.error("秘钥文件md5签名错误,expect: {}, actrual: {}", keyMd5, keystrMd5);
			throw new BitException(BizExceptionEnum.SYS_CONFIG_ERROR_ENCRYPT_KEY_NO_MD5_KEY);
		}
		
		keystr = EncryptUtilsV1.decrypt(keystr);

		// 2.通过读取到的key 获取到key秘钥对象
		Base64 base64 = new Base64();
		byte[] keybyte = base64.decode(keystr);
		SecretKeySpec secretKeySpec = new SecretKeySpec(keybyte, ALGORITHM);
		return secretKeySpec;
	}

	/**
	 * 生成并保存秘钥
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static void generateKey(String path, String seed) throws NoSuchAlgorithmException, IOException {
		KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);// 创建AES的Key生产者
		kgen.init(128, new SecureRandom(seed.getBytes()));// 利用用户密码作为随机数初始化128位的key生产者
		SecretKey secretKey = kgen.generateKey();// 根据用户seed，生成一个密钥

		// 2.对生成的密钥key进行编码保存
		Base64 base64 = new Base64();
		String keyEncode = base64.encodeToString(secretKey.getEncoded());
		
		keyEncode = EncryptUtilsV1.encrypt(keyEncode);
		System.out.println("encrypt key encode : \n" + keyEncode);
		System.out.println("\nencrypt key md5 :\n" + MD5Util.encrypt(keyEncode));

		// 3.写入文件保存
		if(path == null) {
			return;
		}
		File file = new File(path);
		OutputStream outputStream = new FileOutputStream(file);
		outputStream.write(keyEncode.getBytes());
		IOUtils.closeQuietly(outputStream);
	}
}
