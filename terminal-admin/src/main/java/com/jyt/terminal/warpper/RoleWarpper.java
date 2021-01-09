package com.jyt.terminal.warpper;

import java.util.Map;

import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.jyt.terminal.commom.LanBaseControllerWarpper;
import com.jyt.terminal.commom.enums.BitEnum.RoleStatus;
import com.jyt.terminal.factory.IConstantFactory;
import com.jyt.terminal.util.SpringContextHolder;
import com.jyt.terminal.util.ToolUtil;

public class RoleWarpper extends LanBaseControllerWarpper{
	private IConstantFactory constantFactory = SpringContextHolder.getBean(IConstantFactory.class);
    public RoleWarpper(Object list) {
        super(list);
    }
    
    
    
	@Override
	protected void warpTheMap(String currentType,Map<String, Object> map) {
		if(MapUtils.isEmpty(map)) {
			return;
		}
		map.putAll(ToolUtil.toCamelCase(map));
		map.put("statusDesc", RoleStatus.getName(currentType,(Integer) map.get("status")));
		/*map.put("custSourceDesc", CustSource.valueOf((Integer) map.get("custSource")));
		map.put("custTypeDesc", CustType.valueOf((Integer) map.get("custType")));
		map.put("certificateNo", EncryptionUtils.decrypt((String) map.get("certificateNo")));
		map.put("mobileNo", EncryptionUtils.decrypt((String) map.get("mobileNo")));*/
	}
}
