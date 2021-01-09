package com.jyt.terminal.warpper;

import java.math.BigDecimal;
import java.util.Map;

import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.jyt.terminal.commom.LanBaseControllerWarpper;
import com.jyt.terminal.commom.enums.BitEnum.Currency;
import com.jyt.terminal.commom.enums.BitEnum.Exchange;
import com.jyt.terminal.commom.enums.BitEnum.ExchangeStrategy;
import com.jyt.terminal.commom.enums.BitEnum.HotWallet;
import com.jyt.terminal.commom.enums.BitEnum.RedeemStatus;
import com.jyt.terminal.commom.enums.BitEnum.TradeStatus;
import com.jyt.terminal.commom.enums.BitEnum.VirtualCurrencyEnum;
import com.jyt.terminal.util.ArithUtil;
import com.jyt.terminal.util.ToolUtil;

public class SellWarpper extends LanBaseControllerWarpper{
	
    public SellWarpper(Object list) {
        super(list);
    }
    
    
    
	@Override
	protected void warpTheMap(String currentType,Map<String, Object> map) {
		if(MapUtils.isEmpty(map)) {
			return;
		}
		map.putAll(ToolUtil.toCamelCase(map));
		map.put("transStatusDesc", TradeStatus.getName(currentType,(Integer) map.get("transStatus")));
		map.put("redeemStatusDesc", RedeemStatus.getName(currentType,(Integer) map.get("redeemStatus")));
		map.put("channelDesc", HotWallet.valueOf((Integer) map.get("channel")));
		map.put("cash", new BigDecimal((String)map.get("cash")).setScale(2).toString());
		if(ToolUtil.isEmpty(map.get("strategy"))){
			map.put("strategy", Exchange.valueOf(Exchange.NO.getCode()));
		}else{
			map.put("strategy", Exchange.valueOf(Integer.valueOf((String)map.get("strategy"))));
		}
		Double coin = Double.valueOf((String)map.get("amount"))/Math.pow(10,8);
		if(VirtualCurrencyEnum.BTC.getEndesc().equals(map.get("cryptoCurrency"))) {
			map.put("coin", ArithUtil.doubleToStr(coin));
		}else if(VirtualCurrencyEnum.ETH.getEndesc().equals(map.get("cryptoCurrency"))) {
			map.put("coin", ArithUtil.doubleToStr(coin));
		}
		for(Currency cur:Currency.values()) {
			if(cur.getMessage().equals(map.get("currency"))) {
				map.put("cash", map.get("cash").toString());
				map.put("priceDesc", map.get("price").toString());
			}
		}
	}
}
