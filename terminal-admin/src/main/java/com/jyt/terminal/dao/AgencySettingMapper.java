package com.jyt.terminal.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.commom.node.ZTreeNode;
import com.jyt.terminal.model.AgencySetting;


/**
 * <p>
 * 代理商分润配置表 Mapper 接口
 * </p>
 *
 * @author tangfq
 * @since 2021-02-07
 */
public interface AgencySettingMapper extends BaseMapper<AgencySetting> {

	List<Map<String, Object>> getAgencyList(@Param("page")Page<Map<String, Object>> page,@Param("agency")AgencySetting queryAgencyDTO);
	
	AgencySetting getAgencyByPhone(@Param("agency")AgencySetting queryAgencyDTO);
	
	AgencySetting getAgencyByEmail(@Param("agency")AgencySetting queryAgencyDTO);
	
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
	List<ZTreeNode> agencyTreeListById(@Param("agencyId") Integer agencyIds);
	
	
}
