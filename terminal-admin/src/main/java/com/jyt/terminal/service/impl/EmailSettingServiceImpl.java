package com.jyt.terminal.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jyt.terminal.dao.EmailSettingMapper;
import com.jyt.terminal.dto.EmailSettingDTO;
import com.jyt.terminal.model.EmailSetting;
import com.jyt.terminal.service.IEmailSettingService;

@Service
public class EmailSettingServiceImpl extends ServiceImpl<EmailSettingMapper, EmailSetting> implements  IEmailSettingService{

	@Autowired
	private EmailSettingMapper emailSettingMapper;
	
	@Override
	public List<Map<String, Object>> getEmailSettingList(Page<Map<String, Object>> page,
			EmailSettingDTO emailSettingDTO) {
		return emailSettingMapper.getEmailSettingList(page,emailSettingDTO);
	}

}
