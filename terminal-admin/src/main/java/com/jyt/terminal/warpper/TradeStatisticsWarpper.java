package com.jyt.terminal.warpper;

import java.math.BigDecimal;
import java.util.Map;

import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.jyt.terminal.commom.BaseControllerWarpper;
import com.jyt.terminal.commom.enums.BitEnum.VirtualCurrencyEnum;
import com.jyt.terminal.util.ToolUtil;

public class TradeStatisticsWarpper extends BaseControllerWarpper{
	
	
	public TradeStatisticsWarpper(Object list) {
    	super(list);
    	
    }
    
	@Override
	protected void warpTheMap(Map<String, Object> map) {
		if(MapUtils.isEmpty(map)) {
			return;
		}
	
		map.putAll(ToolUtil.toCamelCase(map));
		map.put("cash", ((BigDecimal) map.get("cash")).setScale(2).toString());
		map.put("fee", ((BigDecimal) map.get("fee")).setScale(2).toString());
		map.put("profit", ((BigDecimal) map.get("profit")).setScale(2).toString());
		Double coin = Double.valueOf(map.get("amount").toString())/Math.pow(10,8);
		if(VirtualCurrencyEnum.BTC.getEndesc().equals(map.get("cryptoCurrency"))) {
			map.put("coin", new BigDecimal(coin.toString()).setScale(8)+"BTC");
		}else if(VirtualCurrencyEnum.ETH.getEndesc().equals(map.get("cryptoCurrency"))) {
			map.put("coin", new BigDecimal(coin.toString()).setScale(8)+"ETH");
		}
	}
}
