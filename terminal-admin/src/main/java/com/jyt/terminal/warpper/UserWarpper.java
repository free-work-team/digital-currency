package com.jyt.terminal.warpper;

import java.util.Map;

import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.jyt.terminal.commom.LanBaseControllerWarpper;
import com.jyt.terminal.commom.enums.BitEnum.UserStatus;
import com.jyt.terminal.factory.IConstantFactory;
import com.jyt.terminal.util.SpringContextHolder;
import com.jyt.terminal.util.ToolUtil;

public class UserWarpper extends LanBaseControllerWarpper{
	private IConstantFactory constantFactory = SpringContextHolder.getBean(IConstantFactory.class);
    public UserWarpper(Object list) {
        super(list);
    }
    
    
    
	@Override
	protected void warpTheMap(String currentType,Map<String, Object> map) {
		if(MapUtils.isEmpty(map)) {
			return;
		}
		map.putAll(ToolUtil.toCamelCase(map));
		map.put("statusDesc", UserStatus.getName(currentType,(Integer) map.get("status")));
	}
}
