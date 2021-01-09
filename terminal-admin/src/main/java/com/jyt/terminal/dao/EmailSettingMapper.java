package com.jyt.terminal.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.dto.EmailSettingDTO;
import com.jyt.terminal.model.EmailSetting;

/**
 * 邮箱设置接口
 * @className EmailSettingMapper
 * @author wangwei
 * @date 2019年9月10日
 *
 */
public interface EmailSettingMapper extends BaseMapper<EmailSetting>{

	/**
	 * 分页查询列表
	 * @param page
	 * @param emailSettingDTO
	 * @return
	 */
	List<Map<String, Object>> getEmailSettingList(@Param("page")Page<Map<String, Object>> page,
			@Param("entity")EmailSettingDTO emailSettingDTO);
	
}
