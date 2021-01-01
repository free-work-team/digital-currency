package com.jyt.hardware.printer.utils;

import java.util.Hashtable;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

public class BytesUtil {
	//字节流转16进制字符串
	public static String getHexStringFromBytes(byte[] data) {
		if (data == null || data.length <= 0) { 
			return null; 
		} 
		String hexString = "0123456789ABCDEF"; 
		int size = data.length * 2; 
		StringBuilder sb = new StringBuilder(size); 
		for (int i = 0; i < data.length; i++) { 
			sb.append(hexString.charAt((data[i] & 0xF0) >> 4)); 
			sb.append(hexString.charAt((data[i] & 0x0F) >> 0)); 
		} 
		return sb.toString(); 
	} 
	//单字符转字节 
	private static byte charToByte(char c) { 
		return (byte) "0123456789ABCDEF".indexOf(c); 
	} 
	//16进制字符串转字节数组 
	@SuppressLint("DefaultLocale") 
	public static byte[] getBytesFromHexString(String hexstring){ 
		if(hexstring == null || hexstring.equals("")){ 
			return null; 
		} 
		hexstring = hexstring.replace(" ", ""); 
		hexstring = hexstring.toUpperCase(); 
		int size = hexstring.length()/2; 
		char[] hexarray = hexstring.toCharArray(); 
		byte[] rv = new byte[size]; 
		for(int i=0; i<size; i++){ 
			int pos = i * 2; 
			rv[i] = (byte) (charToByte(hexarray[pos]) << 4 | charToByte(hexarray[pos + 1])); 
		} 
		return rv; 
	} 
	//十进制字符串转字节数组 
	@SuppressLint("DefaultLocale") 
	public static byte[] getBytesFromDecString(String decstring){ 
		if(decstring == null || decstring.equals("")){ 
			return null; 
		} 
		decstring = decstring.replace(" ", ""); 
		int size = decstring.length()/2;
		char[] decarray = decstring.toCharArray(); 
		byte[] rv = new byte[size]; 
		for(int i=0; i<size; i++){ 
			int pos = i * 2; 
			rv[i] = (byte) (charToByte(decarray[pos])*10 + charToByte(decarray[pos + 1])); 
		} 
		return rv; 
	}
	//字节数组组合操作1 
	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) { 
		byte[] byte_3 = new byte[byte_1.length + byte_2.length]; 
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length); 
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length); 
		return byte_3; 
	} 
	//字节数组组合操作
	public static byte[] byteMerger(byte[][] byteList) { 
		int length = 0; 
		for (int i = 0; i < byteList.length; i++) { 
			length += byteList[i].length; 
		} 
		byte[] result = new byte[length]; 
		int index = 0; 
		for (int i = 0; i < byteList.length; i++) { 
			byte[] nowByte = byteList[i]; 
			for (int k = 0; k < byteList[i].length; k++) {
				result[index] = nowByte[k]; index++; 
			} 
		} 
		for (int i = 0; i < index; i++) {
			// CommonUtils.logWuwei("", "result[" + i + "] is " + result[i]);
		} 
		return result; 
	} 
	//生成表格字节流 
	public static byte[] initTable(int h, int w){
		int hh = h * 32; int ww = w * 4; 
		byte[] data = new byte[ hh * ww + 5];
		data[0] = (byte)ww;//xL 
		data[1] = (byte)(ww >> 8);//xH 
		data[2] = (byte)hh; 
		data[3] = (byte)(hh >> 8); 
		int k = 4; int m = 31; 
		for(int i=0; i<h; i++){ 
			for(int j=0; j<w; j++){ 
				data[k++] = (byte)0xFF; 
				data[k++] = (byte)0xFF;
				data[k++] = (byte)0xFF; 
				data[k++] = (byte)0xFF; 
			} 
			if(i == h-1) m =30; 
			for(int t=0; t< m; t++){ 
				for(int j=0; j<w-1; j++){ 
					data[k++] = (byte)0x80; 
					data[k++] = (byte)0; 
					data[k++] = (byte)0; 
					data[k++] = (byte)0;
				} 
				data[k++] = (byte)0x80; 
				data[k++] = (byte)0; 
				data[k++] = (byte)0; 
				data[k++] = (byte)0x01; 
			} 
		} 
		for(int j=0; j<w; j++){ 
			data[k++] = (byte)0xFF;
			data[k++] = (byte)0xFF; 
			data[k++] = (byte)0xFF; 
			data[k++] = (byte)0xFF; 
		} 
		data[k++] = 0x0A;
		return data; 
	} 
//	/** * 生成二维码字节流 * * @param data * @param size * @return */
//	public static byte[] getZXingQRCode(String data, int size) { 
//		try { 
//			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
//			hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); 
//			//图像数据转换，使用了矩阵转换 
//			BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, size, size, hints);
//			//
//			System.out.println("bitmatrix height:" + bitMatrix.getHeight() + " width:" + bitMatrix.getWidth()); 
//			return getBytesFromBitMatrix(bitMatrix); 
//		} catch (WriterException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//		return null; 
//	} 
//	public static byte[] getBytesFromBitMatrix(BitMatrix bits) { 
//		if (bits == null) return null; 
//		int h = bits.getHeight(); 
//		int w = (bits.getWidth() + 7) / 8; 
//		byte[] rv = new byte[h * w + 4]; 
//		rv[0] = (byte) w;//xL 
//		rv[1] = (byte) (w >> 8);//xH 
//		rv[2] = (byte) h;
//		rv[3] = (byte) (h >> 8); 
//		int k = 4; 
//		for (int i = 0; i < h; i++) { 
//			for (int j = 0; j < w; j++) {
//				for (int n = 0; n < 8; n++) { 
//					byte b = getBitMatrixColor(bits, j * 8 + n, i);
//					rv[k] += rv[k] + b; 
//				} 
//				k++; 
//			} 
//		} 
//		return rv; 
//	}
//	private static byte getBitMatrixColor(BitMatrix bits, int x, int y) { 
//		int width = bits.getWidth(); 
//		int height = bits.getHeight(); 
//		if (x >= width || y >= height || x < 0 || y < 0) return 0; 
//		if (bits.get(x, y)) { 
//			return 1; 
//		} else { 
//			return 0; 
//		} 
//	} 
	/** * 将bitmap图转换为头四位有宽高的光栅位图 */ 
	public static byte[] getBytesFromBitMap(Bitmap bitmap) { 
		int width = bitmap.getWidth(); 
		int height = bitmap.getHeight(); 
		int bw = (width - 1) / 8 + 1; 
		byte[] rv = new byte[height * bw + 4]; 
		rv[0] = (byte) bw;//xL 
		rv[1] = (byte) (bw >> 8);//xH 
		rv[2] = (byte) height; 
		rv[3] = (byte) (height >> 8); 
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < height; i++) { 
			for (int j = 0; j < width; j++) {
				int clr = pixels[width * i + j];
				int red = (clr & 0x00ff0000) >> 16; 
			int green = (clr & 0x0000ff00) >> 8; 
		int blue = clr & 0x000000ff; 
		byte gray = (RGB2Gray(red, green, blue)); 
		rv[(width * i + j) / 8 + 4] = (byte) (rv[(width * i + j) / 8 + 4] | (gray << (7 - ((width * i + j) % 8)))); 
			} 
		}
		return rv; 
	}
	/** * 将bitmap转成按mode指定的N点行数据 */ 
	public static byte[] getBytesFromBitMap(Bitmap bitmap, int mode) {
		int width = bitmap.getWidth(); 
		int height = bitmap.getHeight(); 
		int[] pixels = new int[width*height]; 
		if(mode == 0 || mode == 1){ 
			byte[] res = new byte[width*height/8 + 5*height/8];
			bitmap.getPixels(pixels, 0, width, 0, 0, width, height); 
			for(int i = 0; i < height/8; i++){ res[0 + i*(width+5)] = 0x1b;
			res[1 + i*(width+5)] = 0x2a;
			res[2 + i*(width+5)] = (byte) mode; 
			res[3 + i*(width+5)] = (byte) (width%256); 
			res[4 + i*(width+5)] = (byte) (width/256); 
			for(int j = 0; j < width; j++){ byte gray = 0;
			for(int m = 0; m < 8; m++){ int clr = pixels[j + width*(i*8+m)]; 
			int red = (clr & 0x00ff0000) >> 16; 
			int green = (clr & 0x0000ff00) >> 8; 
			int blue = clr & 0x000000ff; 
			gray = (byte) ((RGB2Gray(red, green, blue)<<(7-m))|gray); 
			} 
			res[5 + j + i*(width+5)] = gray; 
			} 
			} 
			return res; 
		}else if(mode == 32 || mode == 33){ 
			byte[] res = new byte[width*height/8 + 5*height/24]; 
			bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
			for(int i = 0; i < height/24; i++){ 
				res[0 + i*(width*3+5)] = 0x1b; 
				res[1 + i*(width*3+5)] = 0x2a; 
				res[2 + i*(width*3+5)] = (byte) mode; 
				res[3 + i*(width*3+5)] = (byte) (width%256); 
				res[4 + i*(width*3+5)] = (byte) (width/256); 
				for(int j = 0; j < width; j++){ 
					for(int n = 0; n < 3; n++){
						byte gray = 0; 
						for(int m = 0; m < 8; m++){
							int clr = pixels[j + width*(i*24 + m + n*8)]; 
							int red = (clr & 0x00ff0000) >> 16; 
						int green = (clr & 0x0000ff00) >> 8; 
				int blue = clr & 0x000000ff; 
				gray = (byte) ((RGB2Gray(red, green, blue)<<(7-m))|gray); 
						} 
						res[5 + j*3 + i*(width*3+5) + n] = gray; 
					} 
				} 
			} 
			return res;
		}else{ 
			return new byte[]{0x0A}; 
		} 
	} 
	private static byte RGB2Gray(int r, int g, int b) { 
		return (false ? ((int) (0.29900 * r + 0.58700 * g + 0.11400 * b) > 200) : ((int) (0.29900 * r + 0.58700 * g + 0.11400 * b) < 200)) ? (byte) 1 : (byte) 0; 
	}
	/** * 生成间断性黑块数据 * @param w : 打印纸宽度, 单位点 * @return */ 
	public static byte[] initBlackBlock(int w){ 
		int ww = (w + 7)/8 ; 
		int n = (ww + 11)/12; 
		int hh = n * 24; 
		byte[] data = new byte[ hh * ww + 5]; 
		data[0] = (byte)ww;//xL 
		data[1] = (byte)(ww >> 8);//xH 
		data[2] = (byte)hh; 
		data[3] = (byte)(hh >> 8); 
		int k = 4; 
		for(int i=0; i < n; i++){ 
			for(int j=0; j<24; j++){ 
				for(int m =0; m<ww; m++){ 
					if(m/12 == i){ 
						data[k++] = (byte)0xFF; 
					}else{ 
						data[k++] = 0; 
					} 
				} 
			} 
		} 
		data[k++] = 0x0A;
		return data; 
	} 
	/** * 生成一大块黑块数据 * @param h : 黑块高度, 单位点 * @param w : 黑块宽度, 单位点, 8的倍数 * @return */ 
	public static byte[] initBlackBlock(int h, int w){ 
		int hh = h; 
		int ww = (w - 1)/8 + 1; 
		byte[] data = new byte[ hh * ww + 6];
		data[0] = (byte)ww;//xL
		data[1] = (byte)(ww >> 8);//xH
		data[2] = (byte)hh; 
		data[3] = (byte)(hh >> 8); 
		int k = 4; 
		for(int i=0; i<hh; i++){ 
			for(int j=0; j<ww; j++){ 
				data[k++] = (byte)0xFF;
			} 
		} 
		data[k++] = 0x00;
		data[k++] = 0x00; 
		return data;
	} 
	//基本覆盖热敏小票打印中所有epson指令的操作 
	public static byte[] customData(){
		byte[] rv = new byte[]{
				(byte) 0xB4, (byte) 0xF2, (byte) 0xD3, (byte) 0xA1, (byte) 0xBB, (byte) 0xFA, (byte) 0xD7, (byte) 0xD4, (byte) 0xBC, (byte) 0xEC, 0x0A, 
				//打印机自检 
				0x1F, 0X1B, 0x1F, 0x53, 
				//初始化打印机 
				0x1B, 0x40,
				//分割线--- 
				0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D, 0x0A, 
				(byte) 0xB8, (byte) 0xC4, (byte) 0xB1, (byte) 0xE4, (byte) 0xD7, (byte) 0xD6, (byte) 0xBC, (byte) 0xE4, (byte) 0xBE, (byte) 0xE0,0x0A, 
				0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x0A, 
				//设置字符右间距
				0x30,0x2E, 0x30, 0x3A,(byte) 0xB2, (byte) 0xE2, (byte) 0xCA, (byte) 0xD4, (byte) 0xB2, (byte) 0xE2, (byte) 0xCA, (byte) 0xD4, (byte) 0xB2, (byte) 0xE2, (byte) 0xCA, (byte) 0xD4, 0x0A,

				0x30,0x2E, 0x35, 0x3A, 0x1B, 0x20, 0x0C,(byte) 0xB2, (byte) 0xE2, (byte) 0xCA, (byte) 0xD4, (byte) 0xB2, (byte) 0xE2, (byte) 0xCA, (byte) 0xD4, (byte) 0xB2, (byte) 0xE2, (byte) 0xCA, (byte) 0xD4, 0x0A,

				0x1B, 0x20, 0x00, 0x31,0x2E, 0x30, 0x3A, 0x1B, 0x20, 0x18, (byte) 0xB2, (byte) 0xE2, (byte) 0xCA, (byte) 0xD4, (byte) 0xB2, (byte) 0xE2, (byte) 0xCA, (byte) 0xD4, (byte) 0xB2, (byte) 0xE2, (byte) 0xCA, (byte) 0xD4, 0x0A,

				0x1B, 0x20, 0x00, 0x32,0x2E, 0x30, 0x3A, 0x1B, 0x20, 0x30, (byte) 0xB2, (byte) 0xE2, (byte) 0xCA, (byte) 0xD4, (byte) 0xB2, (byte) 0xE2, (byte) 0xCA, (byte) 0xD4, (byte) 0xB2, (byte) 0xE2, (byte) 0xCA, (byte) 0xD4, 0x0A,
				0x1B, 0x20, 0x00, 
				//分割线=== 
				0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D, 0x0A,
				(byte) 0xD7, (byte) 0xD6, (byte) 0xCC, (byte) 0xE5, (byte) 0xD0, (byte) 0xA7, (byte) 0xB9, (byte) 0xFB, (byte) 0xC9, (byte) 0xCC, (byte) 0xC3, (byte) 0xD7, (byte) 0xBF, (byte) 0xC6, (byte) 0xBC, (byte) 0xBC, 0x0A,
				0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D, 0x0A, 
				//字体效果 商米科技
				//设置加粗、倍高、倍宽、下划线、反白
				0x1B, 0x21, 0x08, (byte) 0xBC, (byte) 0xD3, (byte) 0xB4, (byte) 0xD6, (byte) 0xC9, (byte) 0xCC, (byte) 0xC3, (byte) 0xD7, (byte) 0xBF, (byte) 0xC6, (byte) 0xBC, (byte) 0xBC, (byte) 0xC9, (byte) 0xCC, (byte) 0xC3, (byte) 0xD7, (byte) 0xBF, (byte) 0xC6, (byte) 0xBC, (byte) 0xBC, 0x0A,
				0x1B, 0x21, 0x00, 
				0x1B, 0x45, 0x01, (byte) 0xBC, (byte) 0xD3, (byte) 0xB4, (byte) 0xD6, (byte) 0xC9, (byte) 0xCC, (byte) 0xC3, (byte) 0xD7, (byte) 0xBF, (byte) 0xC6, (byte) 0xBC, (byte) 0xBC, (byte) 0xC9, (byte) 0xCC, (byte) 0xC3, (byte) 0xD7, (byte) 0xBF, (byte) 0xC6, (byte) 0xBC, (byte) 0xBC, 0x0A,
				0x1B, 0x45, 0x00,
				0x1B, 0x21, 0x10, (byte) 0xB1, (byte) 0xB6, (byte) 0xB8, (byte) 0xDF, (byte) 0xC9, (byte) 0xCC, (byte) 0xC3, (byte) 0xD7, (byte) 0xBF, (byte) 0xC6, (byte) 0xBC, (byte) 0xBC, (byte) 0xC9, (byte) 0xCC, (byte) 0xC3, (byte) 0xD7, (byte) 0xBF, (byte) 0xC6, (byte) 0xBC, (byte) 0xBC, 0x0A, 
				0x1B, 0x21, 0x20, (byte) 0xB1, (byte) 0xB6, (byte) 0xBF, (byte) 0xED, (byte) 0xC9, (byte) 0xCC, (byte) 0xC3, (byte) 0xD7, (byte) 0xBF, (byte) 0xC6, (byte) 0xBC, (byte) 0xBC, (byte) 0xC9, (byte) 0xCC, (byte) 0xC3, (byte) 0xD7, (byte) 0xBF, (byte) 0xC6, (byte) 0xBC, (byte) 0xBC, 0x0A,
				0x1D, 0x21, 0x11, (byte) 0xC9, (byte) 0xCC, (byte) 0xC3, (byte) 0xD7, (byte) 0xBF, (byte) 0xC6, (byte) 0xBC, (byte) 0xBC, 0x1D, 0x21, 0x22, (byte) 0xC9, (byte) 0xCC, (byte) 0xC3, (byte) 0xD7, (byte) 0xBF, (byte) 0xC6, (byte) 0xBC, (byte) 0xBC, 0x0A,
				0x1D, 0x21, 0x00, 
				0x1B, 0x21, (byte) 0x80, (byte) 0xCF, (byte) 0xC2, (byte) 0xBB, (byte) 0xAE, (byte) 0xCF, (byte) 0xDF, (byte) 0xC9, (byte) 0xCC, (byte) 0xC3, (byte) 0xD7, (byte) 0xBF, (byte) 0xC6, (byte) 0xBC, (byte) 0xBC, (byte) 0xC9, (byte) 0xCC, (byte) 0xC3, (byte) 0xD7, (byte) 0xBF, (byte) 0xC6, (byte) 0xBC, (byte) 0xBC, 0x0A,
				0x1B, 0x21, 0x00,
				0x1B, 0x2D, 0x01, (byte) 0xCF, (byte) 0xC2, (byte) 0xBB, (byte) 0xAE, (byte) 0xCF, (byte) 0xDF, (byte) 0xC9, (byte) 0xCC, (byte) 0xC3, (byte) 0xD7, (byte) 0xBF, (byte) 0xC6, (byte) 0xBC, (byte) 0xBC, (byte) 0xC9, (byte) 0xCC, (byte) 0xC3, (byte) 0xD7, (byte) 0xBF, (byte) 0xC6, (byte) 0xBC, (byte) 0xBC, 0x0A,
				0x1B, 0x2D, 0x00,
				0x1D, 0x42, 0x01,0x1B, 0x21, 0x08, (byte) 0xB7, (byte) 0xB4, (byte) 0xB0, (byte) 0xD7, (byte) 0xC9, (byte) 0xCC, (byte) 0xC3, (byte) 0xD7, (byte) 0xBF, (byte) 0xC6, (byte) 0xBC, (byte) 0xBC, (byte) 0xC9, (byte) 0xCC, (byte) 0xC3, (byte) 0xD7, (byte) 0xBF, (byte) 0xC6, (byte) 0xBC, (byte) 0xBC, 0x0A,
				0x1D, 0x42, 0x00,0x1B, 0x21, 0x00, 
				//分割线*** 
				0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A, 0x0A, 
				(byte) 0xC5, (byte) 0xC5, (byte) 0xB0, (byte) 0xE6, (byte) 0xCE, (byte) 0xBB, (byte) 0xD6, (byte) 0xC3, 0x0A, 0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A, 0x0A,
				//排版位置 //设置绝对位置和设置相对位置 
				(byte) 0xD5, (byte) 0xE2, (byte) 0xBE, (byte) 0xE4, (byte) 0xBB, (byte) 0xB0, (byte) 0xB4, (byte) 0xD3, (byte) 0xD0, (byte) 0xD0, (byte) 0xCA, (byte) 0xD7, (byte) 0xBF, (byte) 0xAA, (byte) 0xCA, (byte) 0xBC, 0x0A, 
				(byte) 0xBE, (byte) 0xF8, (byte) 0xB6, (byte) 0xD4, (byte) 0xCE, (byte) 0xBB, (byte) 0xD6, (byte) 0xC3, 0x1B, 0x24, (byte) 0xC0, 0x00, (byte) 0xC6, (byte) 0xAB, (byte) 0xD2, (byte) 0xC6, 0x38, (byte) 0xB8, (byte) 0xF6, (byte) 0xD7, (byte) 0xD6, 0x0A, 
				(byte) 0xCF, (byte) 0xE0, (byte) 0xB6, (byte) 0xD4, (byte) 0xCE, (byte) 0xBB, (byte) 0xD6, (byte) 0xC3, 0x1B, 0x5C, 0x30, 0x00, (byte) 0xC6, (byte) 0xAB, (byte) 0xD2, (byte) 0xC6, 0x32, (byte) 0xB8, (byte) 0xF6, (byte) 0xD7, (byte) 0xD6, 0x0A, 
				//设置对齐方式 
				(byte) 0xBE, (byte) 0xD3, (byte) 0xD7, (byte) 0xF3, 0x0A, 
				0x1B, 0x61, 0x01, (byte) 0xBE, (byte) 0xD3, (byte) 0xD6, (byte) 0xD0, 0x0A, 
				0x1B, 0x61, 0x02, (byte) 0xBE, (byte) 0xD3, (byte) 0xD3, (byte) 0xD2, 0x0A,
				0x1B, 0x61, 0x00,
				//设置左边距
				0x1D, 0x4C, 0x30, 0x00, (byte) 0xC9, (byte) 0xE8, (byte) 0xD6, (byte) 0xC3, (byte) 0xD7, (byte) 0xF3, (byte) 0xB1, (byte) 0xDF, (byte) 0xBE, (byte) 0xE0, 0x34, 0x38, (byte) 0xCF, (byte) 0xF1, (byte) 0xCB, (byte) 0xD8, 0x0A,
				//设置打印区域宽度 
				0x1D, 0x57, (byte) 0xF0, 0x00,
				(byte) 0xB8, (byte) 0xC4, (byte) 0xB1, (byte) 0xE4, (byte) 0xBF, (byte) 0xC9, (byte) 0xB4, (byte) 0xF2, (byte) 0xD3, (byte) 0xA1, (byte) 0xC7, (byte) 0xF8, (byte) 0xD3, (byte) 0xF2, (byte) 0xCE, (byte) 0xAA, 0x32, 0x34, 0x30, (byte) 0xCF, (byte) 0xF1, (byte) 0xCB, (byte) 0xD8, 0x0A,
				//设置左边距
				0x1D, 0x4C, 0x60, 0x00, 
				(byte) 0xC9, (byte) 0xE8, (byte) 0xD6, (byte) 0xC3, (byte) 0xD7, (byte) 0xF3, (byte) 0xB1, (byte) 0xDF, (byte) 0xBE, (byte) 0xE0, 0x39, 0x36, (byte) 0xCF, (byte) 0xF1, (byte) 0xCB, (byte) 0xD8, 0x0A, 
				//设置打印区域宽度 
				0x1D, 0x57, (byte) 0x78, 0x00, 
				(byte) 0xB8, (byte) 0xC4, (byte) 0xB1, (byte) 0xE4, (byte) 0xBF, (byte) 0xC9, (byte) 0xB4, (byte) 0xF2, (byte) 0xD3, (byte) 0xA1, (byte) 0xC7, (byte) 0xF8, (byte) 0xD3, (byte) 0xF2, (byte) 0xCE, (byte) 0xAA, 0x31, 0x32, 0x30, (byte) 0xCF, (byte) 0xF1, (byte) 0xCB, (byte) 0xD8, 0x0A,
				0x1D, 0x4C, 0x00, 0x00,
				0x1D, 0x57, (byte) 0x80, 0x01, 
				//水平制表符-跳格 
				(byte) 0xC4, (byte) 0xAC, 0x09, (byte) 0xC8, (byte) 0xCF, 0x09, (byte) 0xCC, (byte) 0xF8, 0x09, (byte) 0xB8, (byte) 0xF1, 0x09, 0x0A, 
				0x1B, 0x44, 0x01, 0x02, 0x04, 0x08, 0x0A,0x00,
				(byte) 0xD7, (byte) 0xD4, 0x09, (byte) 0xB6, (byte) 0xA8, 0x09, (byte) 0xD2, (byte) 0xE5, 0x09, (byte) 0xCC, (byte) 0xF8, 0x09, (byte) 0xB8, (byte) 0xF1, 0x09, 0x0A, 
				//设置行高 
				0x1B, 0x33, 0x60, (byte) 0xC9, (byte) 0xE8, (byte) 0xD6, (byte) 0xC3, (byte) 0xD0, (byte) 0xD0, (byte) 0xB8, (byte) 0xDF, 0x3A, 0x39, 0x36, (byte) 0xB5, (byte) 0xE3, (byte) 0xD0, (byte) 0xD0, 0x0A,
				0x1B, 0x33, 0x40, (byte) 0xC9, (byte) 0xE8, (byte) 0xD6, (byte) 0xC3, (byte) 0xD0, (byte) 0xD0, (byte) 0xB8, (byte) 0xDF, 0x3A, 0x36, 0x34, (byte) 0xB5, (byte) 0xE3, (byte) 0xD0, (byte) 0xD0, 0x0A, 
				0x1B, 0x33, 0x00, (byte) 0xC9, (byte) 0xE8, (byte) 0xD6, (byte) 0xC3, (byte) 0xD0, (byte) 0xD0, (byte) 0xB8, (byte) 0xDF, 0x3A, 0x30, (byte) 0xB5, (byte) 0xE3, (byte) 0xD0, (byte) 0xD0, 0x0A, 
				0x1B, 0x32, (byte) 0xC4, (byte) 0xAC, (byte) 0xC8, (byte) 0xCF, (byte) 0xD0, (byte) 0xD0, (byte) 0xB8, (byte) 0xDF, 0x0A,
				//分割线--- 
				0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D, 0x0A,
				(byte) 0xD6, (byte) 0xAE, (byte) 0xBA, (byte) 0xF3, (byte) 0xBD, (byte) 0xAB, (byte) 0xD7, (byte) 0xDF, (byte) 0xD6, (byte) 0xBD, 0x0A, 
				0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D, 0x0A,
				//打印并走纸 
				0x1B, 0x4A, 0x40, (byte) 0xD7, (byte) 0xDF, (byte) 0xD6, (byte) 0xBD, 0x36, 0x34, (byte) 0xB5, (byte) 0xE3, (byte) 0xD0, (byte) 0xD0, 0x0A,
				0x1B, 0x4A, 0x60, (byte) 0xD7, (byte) 0xDF, (byte) 0xD6, (byte) 0xBD, 0x39, 0x36, (byte) 0xB5, (byte) 0xE3, (byte) 0xD0, (byte) 0xD0, 0x0A, 
				0x1B, 0x64, 0x0A, (byte) 0xD7, (byte) 0xDF, (byte) 0xD6, (byte) 0xBD, 0x31, 0x30, (byte) 0xD0, (byte) 0xD0, 0x0A, 
				0x1B, 0x64, 0x01, (byte) 0xD7, (byte) 0xDF, (byte) 0xD6, (byte) 0xBD, 0x31, (byte) 0xD0, (byte) 0xD0, 0x0A, 
				//分割线=== 
				0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D, 0x0A, 
				(byte) 0xB4, (byte) 0xF2, (byte) 0xD3, (byte) 0xA1, (byte) 0xCC, (byte) 0xF5, (byte) 0xC2, (byte) 0xEB, 0x0A,
				0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D, 0x0A, 
				//打印条码 
				0x1D, 0x48, 0x02, 
				//upca 
				0x1B, 0x61, 0x00, 0x1D, 0x68, 0x20, 0x1D, 0x77, 0x02,
				0x1D, 0x6B, 0x41, 0x0c, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x32, 0x0A,
				0x1D, 0x6B, 0x00, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x32, 0x00, 0x0A,
				//upce
				0x1B, 0x61, 0x01, 0x1D, 0x68, 0x40, 0x1D, 0x77, 0x04,
				0x1D, 0x6B, 0x42, 0x08, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x0A,
				0x1D, 0x6B, 0x01, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x00, 0x0A,
				//ean13 
				0x1B, 0x61, 0x02, 0x1D, 0x68, 0x60, 0x1D, 0x77, 0x02,
				0x1D, 0x6B, 0x43, 0x0D, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x32, 0x0A,
				0x1D, 0x6B, 0x02, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x32, 0x00, 0x0A, 
				//ean8 
				0x1B, 0x61, 0x00, 0x1D, 0x68, (byte) 0x80, 0x1D, 0x77, 0x05,
				0x1D, 0x6B, 0x44, 0x08, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x0A,
				0x1D, 0x6B, 0x03, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x00, 0x0A, 
				//code39
				0x1B, 0x61, 0x01, 0x1D, 0x68, (byte) 0xA0, 0x1D, 0x77, 0x02,
				0x1D, 0x6B, 0x45, 0x0A, 0x33, 0x36, 0x39, 0x53, 0x55, 0x4E, 0x4D, 0x49, 0x25, 0x24, 0x0A,
				0x1D, 0x6B, 0x04, 0x33, 0x36, 0x39, 0x53, 0x55, 0x4E, 0x4D, 0x49, 0x25, 0x24, 0x00, 0x0A, 
				//itf 
				0x1B, 0x61, 0x02, 0x1D, 0x68, (byte) 0xC0, 0x1D, 0x77, 0x03,
				0x1D, 0x6B, 0x46, 0x0C, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x0A,
				0x1D, 0x6B, 0x05, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x00, 0x0A, 
				//codebar
				0x1B, 0x61, 0x00, 0x1D, 0x68, (byte) 0xE0, 0x1D, 0x77, 0x03,
				0x1D, 0x6B, 0x47, 0x0A, 0x41, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x41, 0x0A,
				//0x1D, 0x6B, 0x06, 0x41, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x41, 0x00, 0x0A, 
				//code93 
				0x1B, 0x61, 0x01, 0x1D, 0x68, (byte) 0xFF, 0x1D, 0x77, 0x02,
				0x1D, 0x6B, 0x48, 0x0C, 0x53, 0x55, 0x4E, 0x4D, 0x49, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x0A,
				//code128 
				0x1B, 0x61, 0x00, 0x1D, 0x68, (byte) 0xB0, 0x1D, 0x77, 0x02,
				0x1D, 0x6B, 0x49, 0x0A, 0x7B, 0x41, 0x53, 0x55, 0x4E, 0x4D, 0x49, 0x30, 0x31, 0x32, 0x0A,
				0x1D, 0x6B, 0x49, 0x0C, 0x7B, 0x42, 0x53, 0x55, 0x4E, 0x4D, 0x49, 0x73, 0x75, 0x6E, 0x6D, 0x69, 0x0A,
				0x1D, 0x6B, 0x49, 0x0B, 0x7B, 0x43, 0x01, 0xc, 0x17, 0x22, 0x2d, 0x38, 0x4e, 0x59, 0x5a, 0x0A, 
				//分割线***
				0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A, 0x0A,
				(byte) 0xB4, (byte) 0xF2, (byte) 0xD3, (byte) 0xA1, (byte) 0xB6, (byte) 0xFE, (byte) 0xCE, (byte) 0xAC, (byte) 0xC2, (byte) 0xEB, 0x0A,
				0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A,0x2A, 0x2A, 0x2A, 0x2A, 0x0A, 
				//打印二维码 
				0x1B, 0x61, 0x01, 
				0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x43, 0x09, 
				0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x45, 0x32,
				0x1D, 0x28, 0x6B, 0x0B, 0x00, 0x31, 0x50, 0x30,
				(byte) 0xC9, (byte) 0xCC, (byte) 0xC3, (byte) 0xD7, (byte) 0xBF, (byte) 0xC6, (byte) 0xBC, (byte) 0xBC, 0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x51, 0x30, 0x0A,
				0x1B, 0x61, 0x00,
				//分割线--- （之后将实现打印光栅位图的方法，再次之前使用1b 61 01 居中） 
				0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D, 0x0A,
				(byte) 0xB4, (byte) 0xF2, (byte) 0xD3, (byte) 0xA1, (byte) 0xB9, (byte) 0xE2, (byte) 0xD5, (byte) 0xA4, (byte) 0xCD, (byte) 0xBC, (byte) 0xCF, (byte) 0xF1, 0x0A,
				0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D,0x2D,0x2D,0x2D, 0x2D,0x2D,0x2D,0x2D,0x2D, 0x0A,
				0x1B, 0x61, 0x01, 
		}; 
		return rv; 
	} 
	//部分字符打印的分割线标志 
	public static byte[] wordData(){ 
		byte rv[] = new byte[]{ 
				//分割线=== 
				0x1B, 0x61, 0x00, 0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D, 0x0A, 
				(byte) 0xD7, (byte) 0xD6, (byte) 0xB7, (byte) 0xFB, (byte) 0xBC, (byte) 0xAF, (byte) 0xC9, (byte) 0xE8, (byte) 0xD6, (byte) 0xC3, 0x0A,
				0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D,0x3D, 0x3D, 0x3D, 0x3D, 0x0A,
				0x1C, 0x26, 0x1C, 0x43, 0x00, 
		}; 
		return rv; 
	}
}