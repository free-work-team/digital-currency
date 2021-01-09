package com.jyt.terminal.warpper;

import java.util.Map;

import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.jyt.terminal.commom.BaseControllerWarpper;
import com.jyt.terminal.commom.enums.BitEnum.DeviceStatus;
import com.jyt.terminal.util.ToolUtil;

public class DeviceWarpper extends BaseControllerWarpper{
	
    public DeviceWarpper(Object list) {
        super(list);
    }
    
    
    
	@Override
	protected void warpTheMap(Map<String, Object> map) {
		if(MapUtils.isEmpty(map)) {
			return;
		}
		map.putAll(ToolUtil.toCamelCase(map));
		map.put("statusDesc", DeviceStatus.valueOf((Integer) map.get("status")));
		/*map.put("custTypeDesc", CustType.valueOf((Integer) map.get("custType")));
		map.put("certificateNo", EncryptionUtils.decrypt((String) map.get("certificateNo")));
		map.put("mobileNo", EncryptionUtils.decrypt((String) map.get("mobileNo")));*/
	}
}
