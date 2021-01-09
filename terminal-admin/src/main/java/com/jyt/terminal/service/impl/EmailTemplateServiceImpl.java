package com.jyt.terminal.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jyt.terminal.dao.EmailTemplateMapper;
import com.jyt.terminal.model.EmailTemplate;
import com.jyt.terminal.service.IEmailTemplateService;

@Service
public class EmailTemplateServiceImpl extends ServiceImpl<EmailTemplateMapper, EmailTemplate> implements IEmailTemplateService{

	@Autowired
	private EmailTemplateMapper emailTemplateMapper;
	@Override
	public List<Map<String, Object>> getEmailTemplateList(Page<Map<String, Object>> page, EmailTemplate emailTemplate) {
		return emailTemplateMapper.getEmailTemplateList(page, emailTemplate);
	}

	
	
}
