package com.jyt.terminal.util;


import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Downloadimg {

	private Logger log = LoggerFactory.getLogger(Downloadimg.class);

	public String download1() throws IOException {
	
		/*String filePath = ".......";
		Bitmap selectedImage =  BitmapFactory.decodeFile(filePath);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		String strBase64=Base64.encodeToString(byteArray, 0);
		
		//下面你只要把这个字符串当成api的返回值，返回给android端就好了
		//下面是android上如何把base64的字符串转换成图片(ImageView默认不能显示base64)

		byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);

		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);*/
		Base64Utils utils = Base64Utils.getInstance();
		String str = utils.file2Base64(new File("D:/jyt/cg.png"));
		
		//utils.base64ToFile(str, new File("D://xx.jpg"));
		
		return str;
	}
	

}
