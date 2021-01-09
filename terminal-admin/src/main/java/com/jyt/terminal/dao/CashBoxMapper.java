package com.jyt.terminal.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.dto.CashBoxDTO;
import com.jyt.terminal.model.CashBox;


/**
 * 钞箱(拿出、放回) Mapper接口
 * @className CashBoxMapper
 * @author wangwei
 * @date 2019年11月8日
 *
 */
public interface CashBoxMapper extends BaseMapper<CashBox> {
	
	/**
	 * 分页查询列表
	 */
	public List<Map<String,Object>> getCashBoxList(@Param("page")Page<Map<String, Object>> page,@Param("entity")CashBoxDTO cashBoxDTO);
	
}