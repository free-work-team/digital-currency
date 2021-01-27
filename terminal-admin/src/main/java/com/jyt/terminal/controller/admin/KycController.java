package com.jyt.terminal.controller.admin;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.jyt.terminal.auth.util.JwtTokenUtil;
import com.jyt.terminal.commom.enums.BitEnum;
import com.jyt.terminal.commom.enums.BitEnum.CustomerStatus;
import com.jyt.terminal.config.properties.JwtProperties;
import com.jyt.terminal.commom.enums.BitException;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.commom.enums.EmailTemplateConstant;
import com.jyt.terminal.model.Agreement;
import com.jyt.terminal.model.Customer;
import com.jyt.terminal.model.EmailSetting;
import com.jyt.terminal.model.EmailTemplate;
import com.jyt.terminal.model.NotifyMailbox;
import com.jyt.terminal.service.IAgreementService;
import com.jyt.terminal.service.ICustomerService;
import com.jyt.terminal.service.IEmailSettingService;
import com.jyt.terminal.service.IEmailTemplateService;
import com.jyt.terminal.service.INotifyMailboxService;
import com.jyt.terminal.service.IOcrService;
import com.jyt.terminal.service.impl.EmailService;
import com.jyt.terminal.util.DateTime;
import com.jyt.terminal.util.DateTimeKit;
import com.jyt.terminal.util.Tip;
import com.jyt.terminal.util.ToolUtil;

@Controller
@RequestMapping("/kyc")
public class KycController extends BaseController{
	
	private static String PREFIX = "/";
	
	private Logger log = LoggerFactory.getLogger(KycController.class);
	
	
	@Value("${upload-path}")
    private String path;
	
	@Autowired
	public ICustomerService customerService;
	
	@Autowired
	private IEmailSettingService emailSettingService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private INotifyMailboxService notifyMailboxService;
	
	@Autowired
	private IEmailTemplateService emailTemplateService;
	@Autowired
	private IAgreementService agreementService;
	
	@Autowired
	private IOcrService iocrService;
	
	
	@RequestMapping("")
    public String index(Model model) {
		String kycId=getPara("kycId");
		if(StringUtils.isEmpty(kycId))
			return PREFIX + "kycFriendly.html";
		else
			model.addAttribute("kycId",kycId);
		
		log.info("kycId:{}",kycId);
				
		Agreement agreement = agreementService.selectOne(new EntityWrapper<Agreement>());
		model.addAttribute("agreement",agreement);
		//证件类型枚举
		List<Map<String,Object>> cardTypes = new ArrayList<Map<String,Object>>();
		for(BitEnum.CardType param:BitEnum.CardType.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			value.put("label", param.getMessage());
			cardTypes.add(value);
		}
		model.addAttribute("cardTypes",cardTypes);
        return PREFIX + "KYC.html";
    }
	
	@RequestMapping("tosuccess")
    public String tosuccess() {
        return PREFIX + "success.html";
    }
	
	@RequestMapping("/checkEmail")
	@ResponseBody
	public Tip checkEmail(String email) {
		Customer cust = customerService.selectOne(new EntityWrapper<Customer>().eq("e_mail", email));
		if(ToolUtil.isEmpty(cust)){
			SUCCESS_TIP.setCode(200);
			SUCCESS_TIP.setMessage("Success!");
		}else{
			if(cust.getStatus().equals(CustomerStatus.AUDIT_PASSED.getCode())){
				SUCCESS_TIP.setCode(500);
				SUCCESS_TIP.setMessage("Information already exists");
			}else{
				SUCCESS_TIP.setCode(200);
				SUCCESS_TIP.setMessage("Success!");
			}
		}
		return SUCCESS_TIP;
	}
	
	@RequestMapping("/add")
	@ResponseBody
	public Tip add(String customerInfo) {
		try {
			String startTime=DateTimeKit.format(new Date(), DateTimeKit.NORM_DATETIME_MS_PATTERN);
			Customer model = JSON.parseObject(customerInfo, Customer.class);
			log.info("进入add,打印model:{},{}",model,startTime);
			String realPath="";
			
			if(ToolUtil.isEmpty(model.getIdCardObverse())){
				log.info("kycId为空");
				SUCCESS_TIP.setCode(500);
				SUCCESS_TIP.setMessage("kycId is null!");
				return SUCCESS_TIP;
			}
			
			if(model.getCardType()==1) {
				realPath=path+model.getIdCardPositive();
			}else if(model.getCardType()==2){
				realPath=path+model.getIdPassport();
			}else {
				SUCCESS_TIP.setCode(500);
				SUCCESS_TIP.setMessage("pls choose card type!");
				return SUCCESS_TIP;
			}
			/*log.info("开始调用华为OCR:{}",startTime);
			String ocrContent="";//iocrService.callOcr(model.getCardType(), realPath);
			DateTime dt1=new DateTime();
			log.info("结束调用华为OCR:{}",dt1.toMsStr());
			
			if(ToolUtil.isEmpty(ocrContent)){
				SUCCESS_TIP.setCode(500);
				SUCCESS_TIP.setMessage("image ocr failure,pls re-upload the image!");
				return SUCCESS_TIP;
			}else {
				model.setIdCardHandheld(ocrContent);
			}*/

			Customer cust = customerService.getByIdCardObserve(model.getIdCardObverse());
			
			if(ToolUtil.isEmpty(cust)){
				model.setStatus(CustomerStatus.AUDIT.getCode());
				model.setCreateUser("system");
				model.setCreateTime(new Date());
		    	customerService.insert(model);
		    	
		    	SUCCESS_TIP.setCode(200);
				SUCCESS_TIP.setMessage("Success!");
			}else{
				if(!cust.getStatus().equals(CustomerStatus.AUDIT_PASSED.getCode())){
					cust.setCustName(model.getCustName());
					cust.setEmail(model.getEmail());
					cust.setIdCardPositive(model.getIdCardPositive());
					cust.setIdCardObverse(model.getIdCardObverse());
					cust.setIdCardHandheld(model.getIdCardHandheld());
					cust.setIdPassport(model.getIdPassport());
					cust.setStatus(CustomerStatus.AUDIT.getCode());
					cust.setCardType(model.getCardType());
					cust.setAuditOpinion("");
					cust.setUpdateTime(new Date());
					cust.setUpdateUser("system");
					
					customerService.updateById(cust);
					
					SUCCESS_TIP.setCode(200);
					SUCCESS_TIP.setMessage("Success!");
				}else{
					SUCCESS_TIP.setCode(500);
					SUCCESS_TIP.setMessage("Information already exists");
				}
			}
			String endTime1=DateTimeKit.format(new Date(), DateTimeKit.NORM_DATETIME_MS_PATTERN);
			log.info("把调用华为OCR识别结果存入表结束:{}",endTime1);
			return SUCCESS_TIP;
		} catch (Exception e) {
			log.error("kyc保存信息异常:{}",e);
			throw new BitException(BizExceptionEnum.SERVER_ERROR);			
		}		
	}
	
	private void sendMerchantEmail() {
		NotifyMailbox box = notifyMailboxService.selectOne(new EntityWrapper<>());
		if(box!=null){
			EmailTemplate template = emailTemplateService.selectOne(new EntityWrapper<EmailTemplate>().eq("code", EmailTemplateConstant.KYC_SEND_MERCHANT));
			Map<String, Object> map = new HashMap<>();
			map.put("custname", "Partner");
			String result = template.getTemplate();
			map.put("result", result);
			EmailSetting emailSetting = emailSettingService.selectOne(new EntityWrapper<>());
			emailService.sendMail("email_template.html", "BTC Terminal Notice", map,box.getMailBox(),emailSetting);
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
	@RequestMapping(value = "/kycImage_upload")
	@ResponseBody
	public String commonImage_upload(HttpServletRequest request, @RequestParam(value="FileData", required=false) MultipartFile file) {
		String startTime=DateTimeKit.format(new Date(), DateTimeKit.NORM_DATETIME_MS_PATTERN);
		logger.info("开始上传图片:{}",startTime);
		String filePath = "";
		Integer imgWidth = null;
		Integer imgHeight = null;
		if (file == null) {
			logger.error("上传的文件为空");
			return filePath;
		}
		if (file.getSize() > 20000000) {
			logger.error("上传的文件大于20M");
			return filePath;
		}
		try {
			BufferedImage bufferedImage = ImageIO.read(file.getInputStream());// 通过MultipartFile得到InputStream，从而得到BufferedImage
            if (bufferedImage == null) {
            	logger.error("上传的文件为空");
            	return filePath;
            }
            imgWidth = bufferedImage.getWidth();
            imgHeight = bufferedImage.getHeight();
            /*if(imgHeight>imgWidth) {
            	logger.error("上传的图片方向为竖直");
            	return "上传的图片方向为竖直,请上传横向的图片";
            }*/
		} catch (Exception e1) {
			logger.error("获取图片宽高失败！{}",e1.getMessage());
			//e1.printStackTrace();
		}
	    
	    //String format = file.getOriginalFilename().split("\\.")[1];
		String format = "jpg";
		//拼接写入文件路径
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
				//filePath = resizeImage(file, path, filename,imgWidth,imgHeight);
				//String result = commonCreateImgUrl(request, filename);
				String result = filename;
				String endTime=DateTimeKit.format(new Date(), DateTimeKit.NORM_DATETIME_MS_PATTERN);				
				logger.info("得到可访问图片路径:{},{}",filePath,endTime);
				return result;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return "0";
			}
		} else {
			logger.info("上传的图片文件大小为0");
			return "0";
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
	 * 重置img尺寸
	 * @param file
	 * @param path
	 * @param filename
	 * @param imgWidth
	 * @param imgHeight
	 * @return
	 * @throws IOException
	 */
    public String resizeImage(MultipartFile file, String path,String filename,int imgWidth,int imgHeight) throws IOException {
        filename = filename.replaceAll("\\\\", "/");   
        String filePath = path + File.separator + filename;
        //String format = filename.split("\\.")[1];
        String format = "jpg";
        BufferedImage prevBuffere = ImageIO.read(file.getInputStream());
        Image prevImage =  prevBuffere.getScaledInstance(imgWidth, imgHeight, Image.SCALE_AREA_AVERAGING);
//      double width = prevImage.getWidth();
//      double height = prevImage.getHeight();
        BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_BGR);
        Graphics graphics = image.createGraphics();  
        graphics.drawImage(prevImage, 0, 0, imgWidth, imgHeight, null);
		File fileItem =new File(filePath);
		if  (!fileItem .exists()  && !fileItem .isDirectory()){
			fileItem.mkdirs();
		}
        ImageIO.write(image, format, fileItem);
        return filePath;
     }
    
	/**
	 * PC端访问跳转友好界面
	 */
	@RequestMapping("/friendly")
    public String kycFriendly() {
        return PREFIX + "kycFriendly.html";
    }
	
}
