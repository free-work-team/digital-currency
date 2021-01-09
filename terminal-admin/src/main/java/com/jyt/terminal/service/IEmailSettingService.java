package com.jyt.terminal.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.jyt.terminal.dto.EmailSettingDTO;
import com.jyt.terminal.model.EmailSetting;

/**
 * 设置发送邮箱接口
 * @className IEmailSettingService
 * @author wangwei
 * @date 2019年9月10日
 *
 */
public interface IEmailSettingService extends IService<EmailSetting>{
	
	List<Map<String,Object>> getEmailSettingList(Page<Map<String,Object>> page, EmailSettingDTO emailSettingDTO);

}
