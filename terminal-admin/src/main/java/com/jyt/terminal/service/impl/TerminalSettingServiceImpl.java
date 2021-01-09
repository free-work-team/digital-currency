package com.jyt.terminal.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jyt.terminal.commom.enums.BitEnum.IsSend;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.dao.CryptoSettingsMapper;
import com.jyt.terminal.dao.TerminalSettingMapper;
import com.jyt.terminal.dto.QueryTermDTO;
import com.jyt.terminal.exception.TerminalException;
import com.jyt.terminal.model.TerminalSetting;
import com.jyt.terminal.service.ITerminalSettingService;
import com.jyt.terminal.util.MD5Util;
import com.jyt.terminal.util.ShiroKit;

/**
 * 
 * @author sunshubin
 * @date 2019年5月5日
 */
@Service
public class TerminalSettingServiceImpl extends ServiceImpl<TerminalSettingMapper, TerminalSetting> implements  ITerminalSettingService{

	
	private Logger log = LoggerFactory.getLogger(TerminalSettingServiceImpl.class);
	
	@Autowired
	private TerminalSettingMapper terminalSettingMapper;
	@Autowired
	private CryptoSettingsMapper cryptoSettingsMapper;
	
	@Override
	public boolean validate(String terminalNo, String password ){
		// TODO Auto-generated method stub
		 TerminalSetting setting =terminalSettingMapper.getByTermNo(terminalNo);
		 
		 if (setting == null ){ 
			 log.info("登录失败，用户名不存在，用户名：{}",terminalNo,password);
			 return false;
		 }
 
		 if(setting.getStatus()!=1 ) {
			 log.info("登录失败，状态不可用，用户名：{}，密码：{}",terminalNo,password);
         	return false;
		 }
	      String pwd = ShiroKit.md5(password, setting.getSalt());
	       if(!StringUtils.equals(setting.getPassword(), pwd)) {
	         log.info("登录失败，用户名或密码错，用户名：{}，密码：{}",terminalNo,password);
	          return false;
	       }
	            
		return true;
	}
	
	@Override
	public int changePwd(String terminalNo, String oldPwd, String newPwd) {
		if (newPwd.equals(oldPwd)) {
			throw new TerminalException(BizExceptionEnum.TWO_PWD_NOT_MATCH);
		}
    	TerminalSetting setting = this.baseMapper.getByTermNo(terminalNo);
		String oldMd5 = MD5Util.md5(oldPwd, setting.getSalt());
		if (setting.getPassword().equals(oldMd5)) {
			String newMd5 = MD5Util.md5(newPwd, setting.getSalt());
			 return this.baseMapper.changePwd(terminalNo, newMd5);
		} else {
			throw new TerminalException(BizExceptionEnum.OLD_PWD_NOT_RIGHT);
		}
	}

	@Override
	public List<Map<String, Object>> getTermList(Page<Map<String, Object>> page, QueryTermDTO queryTermDTO) {
		List<Map<String, Object>> list = terminalSettingMapper.getTermList(page,queryTermDTO);
		return list;
	}

	@Override
	public int setStatus(Integer userId, int status) {
		return this.baseMapper.setStatus(userId, status);
	}

	@Override
	public TerminalSetting getByTermNo(String terminalNo) {
		TerminalSetting setting = this.baseMapper.getByTermNo(terminalNo);
    	if(null == setting){
    		log.info("终端机号{}参数设置不存在",terminalNo);
    		throw new TerminalException(BizExceptionEnum.NO_PARAM_SETTING);
    		//return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.NO_PARAM_SETTING));
    	}
    	if(setting.getIsSend() == IsSend.YES.getCode()){
    		log.info("终端机号{}参数设置未更新",terminalNo);
    		throw new TerminalException(BizExceptionEnum.PARAM_SETTING_IS_SEND);
    	}
    	//更新为已发送
    	boolean rsp = terminalSettingMapper.isSend(setting.getId(),IsSend.YES.getCode());
    	if(!rsp){
    		log.info("更新发送状态失败,终端机号:{}",terminalNo);
    		throw new TerminalException(BizExceptionEnum.UPDATE_IS_SEND_FAIL);
    	}
    	setting.setIsSend(IsSend.YES.getCode());
		return setting;
	}
	@Override
	public int getTermListCount(QueryTermDTO queryTermDTO) {
		// TODO Auto-generated method stub
		return terminalSettingMapper.getTermListCount(queryTermDTO);
	}

	@Override
	public Integer updateSendStatus(List<Integer> termIds, Integer status) {
		return terminalSettingMapper.updateSendStatus(termIds,status);
	}

	
}
