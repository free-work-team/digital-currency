/**
 * Copyright (c) 2021, All Rights Reserved.
 *
*/
package com.jyt.terminal.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.controller.api.dto.CustomerKycRequest;
import com.jyt.terminal.controller.api.dto.KycResponse;
import com.jyt.terminal.controller.api.dto.SmsRequest;
import com.jyt.terminal.controller.api.dto.SmsResponse;
import com.jyt.terminal.dto.SmsSendInDTO;
import com.jyt.terminal.dto.TradeRetDTO;
import com.jyt.terminal.model.Customer;
import com.jyt.terminal.model.SmsSend;
import com.jyt.terminal.service.ISmsSendService;

/**
 * 终端请求后台系统短信接口<br/>
 *
 * @author tangfq
 * @Date 2021年1月2日 下午4:39:40
 * @since jdk 1.8
 *  
 */
@RestController
@RequestMapping(value="api/sms")
public class SmsApiController {

    private Logger log = LoggerFactory.getLogger(ApiController.class);
    
	@Autowired
	public ISmsSendService smsSendService;
		
	@RequestMapping(value = "/sendSMS",method = RequestMethod.POST)
    public ResponseEntity<?> sendSms(@RequestBody SmsRequest request) {
    	log.info("终端机号：{},手机号：{}",request.getTermNo(),request.getPhone());
    	SmsSendInDTO sms=new SmsSendInDTO();
    	sms.setTerminalId(request.getTermNo());
    	sms.setMobile(request.getPhone());
    	//发送短信
    	TradeRetDTO resDto  = smsSendService.sendSMS(sms);
    	
        if(resDto.getTradeStatus()==0)
        	return ResponseEntity.ok(new SmsResponse(BizExceptionEnum.SUCCESS,resDto.getExtraInfo()));     	
        else
        	return ResponseEntity.ok(new SmsResponse(BizExceptionEnum.FAIL,""));
	}
	
	
}
