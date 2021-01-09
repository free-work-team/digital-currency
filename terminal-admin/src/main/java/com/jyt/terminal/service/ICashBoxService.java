package com.jyt.terminal.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.jyt.terminal.dto.CashBoxDTO;
import com.jyt.terminal.model.CashBox;

public interface ICashBoxService extends IService<CashBox>{
	
	/**
	 * 分页查询列表
	 * @param page
	 * @param cashBoxDTO
	 * @return
	 */
	public List<Map<String,Object>> getCashBoxList(Page<Map<String,Object>> page,CashBoxDTO cashBoxDTO);

}
