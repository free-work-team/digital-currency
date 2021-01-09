package com.jyt.terminal.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jyt.terminal.dao.CashBoxMapper;
import com.jyt.terminal.dto.CashBoxDTO;
import com.jyt.terminal.model.CashBox;
import com.jyt.terminal.service.ICashBoxService;

@Service
public class CashBoxServiceImpl extends ServiceImpl<CashBoxMapper, CashBox> implements ICashBoxService{

	@Autowired
	private CashBoxMapper cashBoxMapper;
	
	@Override
	public List<Map<String, Object>> getCashBoxList(Page<Map<String, Object>> page, CashBoxDTO cashBoxDTO) {
		return cashBoxMapper.getCashBoxList(page, cashBoxDTO);
	}

}
