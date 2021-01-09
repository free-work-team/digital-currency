package com.jyt.terminal.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.jyt.terminal.model.EmailTemplate;

public interface IEmailTemplateService extends IService<EmailTemplate>{
	
	/**
	 * 分页查询列表
	 * @param page
	 * @param emailTemplate
	 * @return
	 */
	List<Map<String,Object>> getEmailTemplateList(Page<Map<String,Object>> page,EmailTemplate emailTemplate);

}
