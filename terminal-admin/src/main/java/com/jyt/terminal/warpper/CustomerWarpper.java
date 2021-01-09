package com.jyt.terminal.warpper;

import java.util.Map;

import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.jyt.terminal.commom.LanBaseControllerWarpper;
import com.jyt.terminal.commom.enums.BitEnum.CardType;
import com.jyt.terminal.commom.enums.BitEnum.CustomerStatus;
import com.jyt.terminal.util.ToolUtil;

public class CustomerWarpper extends LanBaseControllerWarpper{
	
    public CustomerWarpper(Object list) {
        super(list);
    }
    
    
    
	@Override
	protected void warpTheMap(String currentType,Map<String, Object> map) {
		if(MapUtils.isEmpty(map)) {
			return;
		}
		map.putAll(ToolUtil.toCamelCase(map));
		map.put("statusDesc", CustomerStatus.getName(currentType,(Integer) map.get("status")));
		map.put("cardTypeDesc", CardType.valueOf((Integer) map.get("card_type")));
	}
}
