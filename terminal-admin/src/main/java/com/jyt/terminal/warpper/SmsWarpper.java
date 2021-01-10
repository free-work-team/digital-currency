package com.jyt.terminal.warpper;


import java.util.Map;
import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.jyt.terminal.commom.LanBaseControllerWarpper;
import com.jyt.terminal.commom.enums.SmsEnum.SmsStatus;
import com.jyt.terminal.util.ToolUtil;

public class SmsWarpper extends LanBaseControllerWarpper{

	public SmsWarpper(Object obj) {
		super(obj);
	}

	@Override
	protected void warpTheMap(String currentType, Map<String, Object> map) {
		if(MapUtils.isEmpty(map)) {
			return;
		}
		map.putAll(ToolUtil.toCamelCase(map));
		map.put("TRADE_STATUS", SmsStatus.getName(currentType, (Integer) map.get("TRADE_STATUS")));			
	}

}
