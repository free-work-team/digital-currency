package com.jyt.terminal.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jyt.terminal.dao.AgencyProfitDetailMapper;
import com.jyt.terminal.dto.QueryAgencyProfitDTO;
import com.jyt.terminal.model.AgencyProfitDetail;
import com.jyt.terminal.service.IAgencyProfitService;

@Service
public class AgencyProfitServiceImpl extends ServiceImpl<AgencyProfitDetailMapper, AgencyProfitDetail> implements IAgencyProfitService{

	@Override
	public List<Map<String, Object>> getAgencyProfitList(Page<Map<String, Object>> page,
			QueryAgencyProfitDTO queryAgencyDTO) {
		List<Map<String, Object>> list = baseMapper.getAgencyProfitList(page, queryAgencyDTO);
		return list;
	}

	
	
	
}
