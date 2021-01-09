package com.jyt.terminal.warpper;

import java.util.Map;

import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.jyt.terminal.commom.LanBaseControllerWarpper;
import com.jyt.terminal.commom.enums.BitEnum.Exchange;
import com.jyt.terminal.commom.enums.BitEnum.HotWallet;
import com.jyt.terminal.commom.enums.BitEnum.WayEnum;
import com.jyt.terminal.util.ToolUtil;

public class TerminalWarpper extends LanBaseControllerWarpper{
	
    public TerminalWarpper(Object list) {
        super(list);
    }
    
	@Override
	protected void warpTheMap(String currentType,Map<String, Object> map) {
		if(MapUtils.isEmpty(map)) {
			return;
		}
		map.putAll(ToolUtil.toCamelCase(map));
		map.put("hotWalletDesc", HotWallet.valueOf((Integer) map.get("hotWallet")));
		map.put("exchange", Exchange.valueOf((Integer) map.get("exchange")));
		map.put("wayDesc", WayEnum.getName(currentType, (Integer) map.get("way")));
	}
}
