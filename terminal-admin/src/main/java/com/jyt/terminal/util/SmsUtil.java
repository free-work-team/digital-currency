/**
 * Copyright (c) 2021, All Rights Reserved.
 *
*/
package com.jyt.terminal.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import com.jyt.terminal.util.https.HttpsClientUtil;

/**
 * 发送短信生成类<br/>
 *
 * @author tangfq
 * @Date 2021年1月3日 上午8:34:49
 * @since jdk 1.8
 *  
 */
public class SmsUtil {

	private static final String API_KEY="bgmmznbm";
	private static final String API_SECRET="x9n96ufP";
	private static final String SEND_SMS_URL="https://api.paasoo.com/json";
	private static final String SMS_HEANDER="BTM";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SmsUtil.class);
	
	
	public static void getMessageStatus(String mesId){
		String SEND_MessageStatus_URL="https://api.paasoo.com/dlr";
		// 参数值
        Object [] params = new Object[]{"key","secret","messageid"};
        
        //参数名
        Object [] values = new Object[]{API_KEY,API_SECRET,mesId};
        
        //获取参数对象
        List<NameValuePair> paramsList = HttpsClientUtil.getParams(params, values);
        //发送get
        Object result=null;
        //发送post
		try {
			result = HttpsClientUtil.sendGet(SEND_MessageStatus_URL, paramsList,0);
			//result2 = HttpsClientUtil.sendPost(url, paramsList);
		} catch (Exception e) {
			LOGGER.info("获取短信订单状态报错:{}",e.getMessage());
		}
        /*查询账户余额返回信息：[{"type":"dlr","messageid":"0101c9-fafe97-d000","to":"8613618369980",
         * "status":0,"drStatus":0,"drStatuscode":"delivered","price":0.007,"counts":1,"network":"46000"}]
         */
		LOGGER.info("查询消息发送状态信息：{}" + result);
	}
	
	public static void AccountBalance(){
		String SEND_ACCOUNTBALACE_URL="https://api.paasoo.com/balance";
		// 参数值
        Object [] params = new Object[]{"key","secret"};
        
        //参数名
        Object [] values = new Object[]{API_KEY,API_SECRET};
        
        //获取参数对象
        List<NameValuePair> paramsList = HttpsClientUtil.getParams(params, values);
        //发送get
        Object result=null;
        //发送post
		try {
			result = HttpsClientUtil.sendGet(SEND_ACCOUNTBALACE_URL, paramsList,0);
			//result2 = HttpsClientUtil.sendPost(url, paramsList);
		} catch (Exception e) {
			e.printStackTrace();
		}
        /*查询账户余额返回信息：{"balance":"0.972","currency":"EUR"}*/
		LOGGER.info("查询账户余额返回信息：{}" + result);
	}
	
	public static Object sendSMS(String phone,String smsContent){
		//参数值
        Object [] params = new Object[]{"key","secret","from","to","text"};
        //参数名
        Object [] values = new Object[]{API_KEY,API_SECRET,SMS_HEANDER,phone,smsContent};
        //获取参数对象
        List<NameValuePair> paramsList = HttpsClientUtil.getParams(params, values);
        //发送get
        Object result=null;
		try {
			result = HttpsClientUtil.sendGet(SEND_SMS_URL, paramsList,0);
			//result2 = HttpsClientUtil.sendPost(url, paramsList);
		} catch (Exception e) {
			LOGGER.info("发送短信报错:{}",e.getMessage());
		}
        
		LOGGER.info("GET返回信息：{}" + result);
		return result;
		
	}
	
	/**
	 * 构造发送的短信内容
	 * @param phone   手机号:国际区号+电话号码，例如中国为：8615900000000 国际区号之前不需要0或是00，
	 *                     并且国际区号与电话号码中间不能有0.
	 * @param smsCode 验证码
	 * @return        待发送的短信验证码
	 */
	public static String generateSmsContent(String phone,String smsCode) {
		StringBuilder sb=new StringBuilder();
		//根据手机号判断是国内还是国外，如果是国内手机号,则走模板,反之则走自定义模块
		if(phone.contains("86")) {
			//sb.append("【无限云】您的验证码为：").append(smsCode).append("，请在一分钟内使用，谢谢！");
			sb.append("【Hunghui】Your verification code is ").append(smsCode).
				append(". Please don't disclose your code.");
		}else {
			sb.append("BTM: ").append(smsCode).
			   append(" is your verification code. Do not share this code with anyone.[PIN]");
		}
		
		return sb.toString();
	}
	
	/**
	 * 生成6位长度的短信验证码
	 */
	public static String generateCode() {
        Long codeL = System.nanoTime();
        String codeStr = Long.toString(codeL);
        String verifyCode = codeStr.substring(codeStr.length() - 6);
        return verifyCode;
    }
	
	/**
	 * 生成发送短信的订单号
	 * @return
	 */
	public static String getOrderIdByTime() {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        String newDate=sdf.format(new Date());
        String result="";
        Random random=new Random();
        for(int i=0;i<3;i++){
            result+=random.nextInt(10);
        }
        return newDate+result;
    }
	
	public static void main(String[] args) {
		String phone="8613618369980";
		String smsCode=generateCode();
		String text=generateSmsContent(phone,smsCode);
		//sendSMS(phone,text);
		Object t="{\"messageid\":\"0121d7-04e6ef-8000\",\"status\":\"0\"}";
		JSONObject js=JSONObject.parseObject(t.toString());
		System.out.println(js.get("messageid")+","+js.get("status"));
		
	}
}
