/**
 * Copyright (c) 2021, All Rights Reserved.
 *
*/
package com.jyt.terminal.service.impl;


import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jyt.terminal.service.IOcrService;
import com.jyt.terminal.util.Const;
import com.jyt.terminal.util.hwocr.ocr.HWOcrClientToken;

/**
 * 实现华为OCR识别接口<br/>
 *
 * @author tangfq
 * @Date 2021年1月17日 下午12:04:34
 * @since jdk 1.8
 *  
 */
@Service
public class OcrServiceImpl implements IOcrService {

	private Logger log = LoggerFactory.getLogger(OcrServiceImpl.class);
	
	@Override
	public String callOcr(int certificateType,String path) {
		String regionName="",httpUri="",ocrContent="";
		 
		if(certificateType==1) {//id-card
			regionName="cn-north-1";
			httpUri = "/v1.0/ocr/id-card";			
		}else if(certificateType==2){//passport
			regionName="cn-north-4";
			httpUri = "/v1.0/ocr/passport";
		}else {
			return ocrContent;
		}
		
		JSONObject params = new JSONObject();
		try {
			HWOcrClientToken ocrClient= new HWOcrClientToken(Const.TEST_DOMAIN_NAME, Const.TEST_USER_NAME, Const.TEST_PASS_WORD,regionName);
			HttpResponse response=ocrClient.RequestOcrServiceBase64(httpUri, path, params);
			
			String content = IOUtils.toString(response.getEntity().getContent(), "utf-8");
			if(null==content) {
				return ocrContent;
			}
			log.info("返回数据的解析内容：{}",content);
			if(content.contains("result")) {
				JSONObject json=JSONObject.parseObject(content);
				JSONObject json0=JSONObject.parseObject(json.getString("result"));
				ocrContent=json0.toString();
			}else {
				return ocrContent;
			}
			
		}
		catch (Exception e) {
			log.info("OCR报错：{}",e);
			return ocrContent;		
		}
		return ocrContent;		
	}

	
	
}
