package com.jyt.terminal.warpper;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.jyt.terminal.commom.BaseControllerWarpper;
import com.jyt.terminal.commom.enums.BitEnum.Currency;
import com.jyt.terminal.commom.enums.BitEnum.OrderStatus;
import com.jyt.terminal.commom.enums.BitEnum.VirtualCurrencyEnum;
import com.jyt.terminal.util.ToolUtil;

public class OrderWarpper extends BaseControllerWarpper{
	
    private static final String String = null;



	public OrderWarpper(Object list) {
        super(list);
    }
    
    
    
	@Override
	protected void warpTheMap(Map<String, Object> map) {
		if(MapUtils.isEmpty(map)) {
			return;
		}
		map.putAll(ToolUtil.toCamelCase(map));
		map.put("transStatusDesc", OrderStatus.valueOf(Integer.valueOf((String) map.get("status"))));
		String funds = (String)map.get("funds");
		if(StringUtils.isNotBlank(funds)){
			map.put("funds", new BigDecimal(funds).setScale(2).toString());
		}else{
			map.put("funds","0.00");
		}
		String size = map.get("size").toString();
		if(VirtualCurrencyEnum.BTC.getEndesc().equals(map.get("cryptoCurrency"))) {
			map.put("coin", size);
		}else if(VirtualCurrencyEnum.ETH.getEndesc().equals(map.get("cryptoCurrency"))) {
			map.put("coin", size);
		}
		for(Currency cur:Currency.values()) {
			if(cur.getMessage().equals(map.get("currency"))) {
				map.put("funds", map.get("funds").toString());
			}
		}
	}
}
