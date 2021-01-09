package com.jyt.terminal.controller.admin;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.jyt.terminal.commom.enums.BitException;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.util.ToolUtil;

import io.swagger.annotations.ApiOperation;

/**
 * 
* Title: ImgController  
* Description:   图片
* @author tiantianer 
* @date 2018年11月13日
 */
@Controller
@RequestMapping("/image")
public class ImgController extends BaseController {
	@Value("${upload-path}")
    private String path;
	
	/**
     * 查看图片
	 * @throws IOException 
     */
   /* @ApiOperation(value="查看图片",notes="查看图片")
	@RequestMapping("/preview")
	@ResponseBody
	public ResponseEntity<Resource> preview(@RequestParam String imgPath) {
        if(ToolUtil.isEmpty(imgPath)) {
        	throw new BitException(BizExceptionEnum.DB_RESOURCE_NULL);
        }
        imgPath = "\\home\\tomcat\\img\\customer\\2019\\8\\29\\1567065061746VONY.jpg";
		File file = new File(imgPath);
		//String path = "E:\\img\\customer\\2019\\8\\29\\1567074227696YQET.jpg";
		// 设置ContentType
		FileSystemResource resource = new FileSystemResource(file);
		logger.info("图片路径："+resource);
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("image", "jpeg");
		headers.setContentType(mediaType);
		return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
	}*/
	
	@ApiOperation(value="查看图片",notes="查看图片")
	@RequestMapping("/preview")
	@ResponseBody
	public void preview(HttpServletRequest request,HttpServletResponse response,@RequestParam String imgPath) throws IOException {
        if(ToolUtil.isEmpty(imgPath)) {
        	throw new BitException(BizExceptionEnum.DB_RESOURCE_NULL);
        }
  		imgPath = path + imgPath;
  		
  		logger.info("显示图片");
		// 前台传来的图片路径id
		if (imgPath != null && !"".equals(imgPath)) {
			// 从配置文件中获取图片存放文件�?
			/*Properties props = ConfigUtil.getGetProperties("parameter");
			String imageUrl = props.getProperty("indexImageUrl");
			// 获得图片全路�?
			String pic_path = imageUrl + File.separator + id;// 图片路径
			 */			
			String pic_path = imgPath;// 图片路径
			FileInputStream is = null;
			OutputStream toClient = null;
			// 判断该文件是否存在?
			File file=new File(pic_path);  
			if(file.exists()) {
				// 以流的方式读取图片?
				try {
					is = new FileInputStream(pic_path);
					int i = is.available(); // 得到文件大小
					byte data[] = new byte[i];
					is.read(data); // 读数�?
					toClient = response.getOutputStream(); // 得到向客户端输出二进制数据的对象
					toClient.write(data); // 输出数据
					logger.info("显示图片成功！");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
						toClient.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			logger.info("图片路径不存在！");
		}
	}
    
	/**
	 * 上传kyc图片
	 * @param request
	 * @param file
	 * @param imgWidth
	 * @param imgHeight
	 * @param fileHeader
	 * @return
	 */
	@RequestMapping(value = "/commonImage_upload")
	@ResponseBody
	public String commonImage_upload(HttpServletRequest request, @RequestParam(value="FileData", required=false) MultipartFile file) {
		String filePath = "";
		if (file == null) {
			logger.error("上传的文件为空");
			return filePath;
		}
		if (file.getSize() > 20000000) {
			logger.error("上传的文件大于20M");
			return filePath;
		}
		String format = file.getOriginalFilename().split("\\.")[1];
		// 拼接写入文件路径
		String filenametem = System.currentTimeMillis()+ createRandom(4) + "."+format;
		Calendar calendar = Calendar.getInstance();
		StringBuilder buffer = new StringBuilder("/img/cust");
		buffer.append("/").append(calendar.get(Calendar.YEAR)).append("/");
		buffer.append(calendar.get(Calendar.MONTH) + 1).append("/");
		buffer.append(calendar.get(Calendar.DATE)).append("/");
		buffer.append(filenametem);
		String filename = buffer.toString();
		if (file.getSize() > 0) {
			try {
				filePath = toUpload(file, path, filename);
				//String result = commonCreateImgUrl(request, filename);
				String result = filename;
				logger.info("得到可访问图片路径:" + result);
				return result;
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				return "0";
			}
		} else {
			logger.info("上传的图片文件大小为0");
			return "0";
		}
	}

	/**
	 * 返回访问图片地址
	 *
	 * @param request
	 * @param imgurl
	 * @return
	 */
	private static String commonCreateImgUrl(HttpServletRequest request, String imgurl) {
		//获取域名
		StringBuffer url = request.getRequestURL();
		String requestUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).toString();
		String urlHeader = "/en/img";
		return requestUrl + urlHeader + imgurl;
	}

	/**
	 * 重置img尺寸
	 * @param file
	 * @param path
	 * @param filename
	 * @param imgWidth
	 * @param imgHeight
	 * @return
	 * @throws IOException
	 */
    public String  resizeImage(MultipartFile file, String path,String filename,int imgWidth,int imgHeight) throws IOException {
        filename = filename.replaceAll("\\\\", "/");   
        String filePath = path + File.separator + filename;
        BufferedImage prevBuffere = ImageIO.read(file.getInputStream());

		BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		image = g2d.getDeviceConfiguration().createCompatibleImage(imgWidth, imgHeight, Transparency.TRANSLUCENT);
		g2d.dispose();
		g2d = image.createGraphics();
		Image from = prevBuffere.getScaledInstance(imgWidth, imgHeight, BufferedImage.SCALE_AREA_AVERAGING);
		g2d.drawImage(from, 0, 0, null);
		g2d.dispose();
		File fileItem =new File(filePath);
		if  (!fileItem .exists()  && !fileItem .isDirectory()){
			fileItem.mkdirs();
		}
		ImageIO.write(image, "png", fileItem);

        return filePath;
     }

	/**
	 * 原图上传
	 *
	 * @param file
	 * @param path
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public String toUpload(MultipartFile file, String path, String filename) throws IOException {
		filename = filename.replaceAll("\\\\", "/");
		String filePath = path + File.separator + filename;
		File targetFile = new File(filePath);
		//注意，判断父级路径是否存在
		if (!targetFile.getParentFile().exists()) {
			targetFile.getParentFile().mkdirs();
		}
		//保存
		file.transferTo(targetFile);
		return filePath;
	}
	/**
     * 获取图片路径
     */
    @RequestMapping(value = "/showPicture")
	public void showPicture(HttpServletRequest request,
			HttpServletResponse response,String path) {
    	logger.info("显示图片");
		// 前台传来的图片路径id
		String id = null;
		try {
			// 解决连接中汉字乱码问�?
			id = new String(request.getParameter("id").getBytes("iso-8859-1"),
					"utf-8");
		} catch (UnsupportedEncodingException e) {
			// 如果报错则设置id为null不处理该链接
			id = null;
			e.printStackTrace();
		}
		if (id != null && !"".equals(id)) {
			// 从配置文件中获取图片存放文件�?
			/*Properties props = ConfigUtil.getGetProperties("parameter");
			String imageUrl = props.getProperty("indexImageUrl");
			// 获得图片全路�?
			String pic_path = imageUrl + File.separator + id;// 图片路径
*/			String pic_path = id;// 图片路径
			FileInputStream is = null;
			OutputStream toClient = null;
			// 判断该文件是否存在?
			File file=new File(pic_path);  
			if(file.exists()) {
				// 以流的方式读取图片?
				try {
					is = new FileInputStream(pic_path);
					int i = is.available(); // 得到文件大小
					byte data[] = new byte[i];
					is.read(data); // 读数�?
					toClient = response.getOutputStream(); // 得到向客户端输出二进制数据的对象
					toClient.write(data); // 输出数据
					logger.info("显示图片成功！");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
						toClient.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
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
