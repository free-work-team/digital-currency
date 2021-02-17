package com.jyt.terminal.warpper;

import java.util.Map;
import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.jyt.terminal.commom.LanBaseControllerWarpper;
import com.jyt.terminal.commom.enums.BitEnum.UserStatus;
import com.jyt.terminal.util.ToolUtil;

public class AgencySettingWarpper extends LanBaseControllerWarpper{
	
	public AgencySettingWarpper(Object obj) {
		super(obj);
	}

	@Override
	protected void warpTheMap(String currentType, Map<String, Object> map) {
		if(MapUtils.isEmpty(map)) {
			return;
		}
		map.putAll(ToolUtil.toCamelCase(map));
		map.put("agency_statue", UserStatus.getName(currentType, (Integer) map.get("agency_statue")));
		
	}


}
