package com.jyt.bitcoinmaster.utils;

/**
 * @author by Talon, Date on 19/5/16.
 * note:
 */

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesUtils {

    public static final String bm = "UTF-8";

    private static final byte[] DEFAULT_KEY = new byte[] { 11, -31, -91, 0, -32, 0, 50, -73, -98, 34, 2, 111, 90, -94, 97, -61 };

    /**
     * 最新加密版本（注：只可使用3位）
     */
    private static final String NEWEST_VERSION = "V2_";

    /**
     * 字节数组转化为大写16进制字符串
     *
     * @param b
     * @return
     */
    private static String byte2HexStr(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            String s = Integer.toHexString(b[i] & 0xFF);
            if (s.length() == 1) {
                sb.append("0");
            }

            sb.append(s.toUpperCase());
        }

        return sb.toString();
    }

    /**
     * 16进制字符串转字节数组
     *
     * @param s
     * @return
     */
    private static byte[] str2ByteArray(String s) {
        int byteArrayLength = s.length() / 2;
        byte[] b = new byte[byteArrayLength];
        for (int i = 0; i < byteArrayLength; i++) {
            byte b0 = (byte) Integer.valueOf(s.substring(i * 2, i * 2 + 2), 16)
                    .intValue();
            b[i] = b0;
        }

        return b;
    }


    /**
     * AES 加密
     *
     * @param content  明文
     * @param password 生成秘钥的关键字
     * @return
     */

    public static String aesEncrypt(String content, String password) {
        try {

            SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedData = cipher.doFinal(content.getBytes(bm));
            // return new String(encryptedData,bm);
            return Base64.encode(encryptedData);
//          return byte2HexStr(encryptedData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * AES 解密
     *
     * @param content  密文
     * @param
     * @return
     */

    public static String aesDecrypt(String content) {
        content = content.substring(3);
        try {
            byte[] byteMi = Base64.decode(content);
//          byte[] byteMi=  str2ByteArray(content);
            SecretKeySpec key = new SecretKeySpec(DEFAULT_KEY, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedData = cipher.doFinal(byteMi);
            return new String(decryptedData, "utf-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
        try {
            String s = AesUtils.aesDecrypt("V2_C0PZ+oO9lTyHgjSljBWPIA==");
            System.out.print(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
