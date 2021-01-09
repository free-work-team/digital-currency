package com.jyt.terminal.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.jyt.terminal.dto.QuerySmsDTO;
import com.jyt.terminal.dto.SMSAuthInDTO;
import com.jyt.terminal.dto.SmsSendInDTO;
import com.jyt.terminal.dto.TradeRetDTO;
import com.jyt.terminal.model.SmsSend;

/**
 * <p>
 * 短信发送表 服务类
 * </p>
 *
 * @author 李春龙
 * @since 2018-09-23
 */
public interface ISmsSendService extends IService<SmsSend> {
	
	/**
	 * 插入或修改短信记录
	 * @param sms
	 * @return
	 */
	public boolean inserOrUpdateOrder(SmsSend sms);
	
	/**
	 * 纯粹发送短信
	 * 
	 * @param smsSendInDTO
	 * @return
	 */
	TradeRetDTO sendSMS(SmsSendInDTO smsSendInDTO);
	
	/**
	 * 发送验证码短信
	 * 
	 * @param smsSendInDTO
	 * @return
	 */
	TradeRetDTO sendSMSAuth(SMSAuthInDTO smsSendInDTO);
	
	/**
	 * 验证短信验证码
	 * @param merchantId
	 * @param orderNo
	 * @param authCode 验证码
	 * @return
	 */
	TradeRetDTO confirmSMSAuth(String orderNo, String authCode);
	
	/**
	 * 
	 * @param page
	 * @param querySmsDTO
	 * @return
	 */
	public List<Map<String, Object>> getSmsList(Page<Map<String, Object>> page, QuerySmsDTO querySmsDTO);
}
