package com.jyt.terminal.util;


import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Downloadimg {

	private Logger log = LoggerFactory.getLogger(Downloadimg.class);

	/**
	 * 将图片转化为base64格式数据
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public String download1(String filePath) throws IOException {	
		Base64Utils utils = Base64Utils.getInstance();
		String str = utils.file2Base64(new File(filePath));				
		return str;
	}
	
	/**
	 * 将图片流数据转化为图片并生成到指定路径
	 * @param fileContent
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public boolean uploadImg(String fileContent,String path) throws IOException {
		
		//保存
		Base64Utils utils = Base64Utils.getInstance();
		return utils.base64ToFile(fileContent, new File(path));
	}
	
	/**
	 * 创建图片保存在服务器的路径
	 * @param path
	 * @return
	 */
	public String createPicUrl(String path) {
		String format = "jpg";
		Calendar calendar = Calendar.getInstance();
		StringBuilder buffer = new StringBuilder(path+"/img/cust/kyc");
		buffer.append("/").append(calendar.get(Calendar.YEAR)).append("/");
		buffer.append(calendar.get(Calendar.MONTH) + 1).append("/");
		buffer.append(calendar.get(Calendar.DATE)).append("/");

		String filenametem = System.currentTimeMillis()+ createRandom(4) + "."+format;
		buffer.append(filenametem);
		
		String filePathFull = buffer.toString();		
		File targetFile = new File(filePathFull);
		
		//注意，判断父级路径是否存在
		if (!targetFile.getParentFile().exists()) {
			targetFile.getParentFile().mkdirs();
		}
		return filePathFull;
	}
	
	private String createRandom(int range) {
		String str = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder sb = new StringBuilder(range);
		for (int i = 0; i < range; i++) {
			//nextInt(int n) 该方法的作用是生成一个随机的int值，该值介于[0,n)的区间，也就是0到n之间的随机int值，包含0而不包含n。
			char ch = str.charAt(new Random().nextInt(str.length()));
			sb.append(ch);
		}
		return sb.toString();
	}

}
