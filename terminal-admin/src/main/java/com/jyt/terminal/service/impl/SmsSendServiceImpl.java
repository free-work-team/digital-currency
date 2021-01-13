package com.jyt.terminal.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.dao.SmsSendMapper;
import com.jyt.terminal.dto.QuerySmsDTO;
import com.jyt.terminal.dto.SMSAuthInDTO;
import com.jyt.terminal.dto.SmsSendInDTO;
import com.jyt.terminal.dto.TradeRetDTO;
import com.jyt.terminal.exception.TerminalException;
import com.jyt.terminal.model.Order;
import com.jyt.terminal.model.SmsSend;
import com.jyt.terminal.service.ISmsSendService;
import com.jyt.terminal.util.SmsUtil;

/**
 * <p>
 * 短信发送服务实现类
 * </p>
 *
 * @author tangfq
 * @since 2020-12-27
 */
@Service
public class SmsSendServiceImpl extends ServiceImpl<SmsSendMapper, SmsSend> implements ISmsSendService {
	
	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Override
	public List<Map<String, Object>> getSmsList(Page<Map<String, Object>> page, QuerySmsDTO querySmsDTO) {
		List<Map<String, Object>> list = baseMapper.getSMSList(page, querySmsDTO);
		return list;
	}
	
	@Override
	public TradeRetDTO sendSMS(SmsSendInDTO smsSendInDTO) {
		TradeRetDTO result1=new TradeRetDTO();
		result1.setTradeStatus(1);//默认失败
		String smsCode=SmsUtil.generateCode();
		String phone=smsSendInDTO.getMobile();
		try {
			String smsText=SmsUtil.generateSmsContent(phone,smsCode);
			
			//生成短信bean
			SmsSend sms=generateSmsObject(phone,smsCode,smsText,smsSendInDTO.getTerminalId());
			
			//插入发送短信表记录
			inserOrUpdateOrder(sms);
			
			//插入成功后调用短信接口,发送返回成功的
			/*Object result=SmsUtil.sendSMS(phone,smsText);
			JSONObject retObj=JSONObject.parseObject(result.toString());*/
			String resStatus="0";//retObj.getString("status");//0表示成功
			
			if(resStatus.equals("0")) {
				result1.setExtraInfo(smsCode);
				result1.setTradeStatus(0);				
				sms.setChRetCode(resStatus);
				//sms.setChTradeNo(retObj.getString("messageid"));
				sms.setChTradeNo("20210112111");
				sms.setTradeStatus(1);				
			}else {
				sms.setTradeStatus(2);
				sms.setChRetCode(resStatus);
			}

			sms.setSendTime(new Date());
			boolean isUpdate=inserOrUpdateOrder(sms);
			LOGGER.info("操作数据库:{}",isUpdate);
		}catch(Exception e) {
			LOGGER.error("操作数据库失败:{}",e.getMessage());
		}
		
		return result1;
	}

	@Override
	public boolean inserOrUpdateOrder(SmsSend sms) {
		boolean flag = true;
    	
		SmsSend o = baseMapper.getByOrderNo(sms.getOrderNo());
		
    	if(o == null){
    		flag = insert(sms);
    	}else{
    		flag = updateById(sms);
    	}
    	if(!flag){
    		LOGGER.info("修改短信订单数据，插入或更新失败:{}",sms);
    		throw new TerminalException(BizExceptionEnum.FAIL);
    	}
    	return flag;
	}
	
	@Override
	public TradeRetDTO sendSMSAuth(SMSAuthInDTO smsAuthInDTO) {
		return null;
	}

	@Override
	public TradeRetDTO confirmSMSAuth(String orderNo, String authCode) {
		return null;
	}

	private boolean checkValidTime(Date sendTime, int validMinutes) {
		Date now = new Date();
		if (now.getTime() > sendTime.getTime() + 1000 * 60 * validMinutes) {
			return false;
		} else {
			return true;
		}
	}
	
	public static void main(String[] args) {
		SmsUtil.generateCode();
	}
	
	/**
	 * 生成插入短信表的bean
	 * @param phone     手机号
	 * @param smsCode   验证码
	 * @param text      短信内容
	 * @return 已赋值的bean
	 */
	private static SmsSend generateSmsObject(String phone,String smsCode,String text,String terminalId) {
		SmsSend sms=new SmsSend();
		sms.setMobile(phone);
		sms.setChannelNo("001");
		sms.setSendTime(new Date());
		sms.setOrderNo(SmsUtil.getOrderIdByTime());
		sms.setSmsContent(text);
		sms.setSmsAuthId(Long.parseLong(smsCode));
		sms.setMerchantId(terminalId);
		sms.setChState("1");
		sms.setTradeStatus(3);
		return sms;
	}
	
	
	
}
