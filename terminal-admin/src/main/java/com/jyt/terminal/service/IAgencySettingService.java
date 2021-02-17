package com.jyt.terminal.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.jyt.terminal.commom.node.ZTreeNode;
import com.jyt.terminal.dto.QueryAgencySettingDTO;
import com.jyt.terminal.model.AgencySetting;


public interface IAgencySettingService extends IService<AgencySetting>{

	
	/**
	 * 查询代理商
	 * @param page
	 * @param queryAgencyDTO
	 * @return
	 */
	public List<Map<String, Object>> getAgencyList(Page<Map<String, Object>> page, QueryAgencySettingDTO queryAgencyDTO);
	
	public AgencySetting getAgencyByPhone(QueryAgencySettingDTO queryAgencyDTO);
	
	public AgencySetting getAgencyByEmail(QueryAgencySettingDTO queryAgencyDTO);
	
	/**
	 * 获取代理商列表树
	 *
	 * @return
	 * @date 2021年2月15日 上午10:32:04
	 */
	List<ZTreeNode> agencyTreeList();

	/**
	 * 获取代理商列表树
	 *
	 * @return
	 * @date 2021年2月15日 上午10:32:04
	 */
	List<ZTreeNode> agencyTreeListByRoleId(Integer agencyIds);
}
