package com.jyt.terminal.service.impl;

import static org.hamcrest.CoreMatchers.theInstance;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jyt.terminal.commom.node.ZTreeNode;
import com.jyt.terminal.dao.AgencySettingMapper;
import com.jyt.terminal.dto.QueryAgencySettingDTO;
import com.jyt.terminal.model.AgencySetting;
import com.jyt.terminal.service.IAgencySettingService;

@Service
public class AgencySettingServiceImpl extends ServiceImpl<AgencySettingMapper, AgencySetting> implements IAgencySettingService{

	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Override
	public List<Map<String, Object>> getAgencyList(Page<Map<String, Object>> page,
			QueryAgencySettingDTO queryAgencyDTO) {
		List<Map<String, Object>> list = baseMapper.getAgencyList(page, queryAgencyDTO);
		return list;
	}

	@Override
	public AgencySetting getAgencyByPhone(QueryAgencySettingDTO queryAgencyDTO) {
		AgencySetting agency=baseMapper.getAgencyByPhone(queryAgencyDTO);
		return agency;
	}

	@Override
	public AgencySetting getAgencyByEmail(QueryAgencySettingDTO queryAgencyDTO) {
		AgencySetting agency=baseMapper.getAgencyByEmail(queryAgencyDTO);
		return agency;
	}

	@Override
	public List<ZTreeNode> agencyTreeList() {
		return this.baseMapper.agencyTreeList();
	}

	@Override
	public List<ZTreeNode> agencyTreeListByRoleId(Integer agencyId) {
		return this.baseMapper.agencyTreeListById(agencyId);
	}

	
	
}
