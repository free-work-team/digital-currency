package com.jyt.terminal.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.model.EmailTemplate;


/**
 * 邮件模板
 * @className EmailTemplateMapper
 * @author wangwei
 * @date 2019年9月27日
 *
 */
public interface EmailTemplateMapper extends BaseMapper<EmailTemplate> {
	
	/**
	 * 分页查询列表
	 * @param page
	 * @param emailTemplate
	 * @return
	 */
	public List<Map<String,Object>> getEmailTemplateList(@Param("page")Page<Map<String, Object>> page,@Param("entity")EmailTemplate emailTemplate);

}