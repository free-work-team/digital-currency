package com.jyt.terminal.warpper;

import java.util.Map;

import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.jyt.terminal.commom.LanBaseControllerWarpper;
import com.jyt.terminal.commom.enums.BitEnum.CryptoSettingsStatusEnum;
import com.jyt.terminal.commom.enums.BitEnum.Exchange;
import com.jyt.terminal.commom.enums.BitEnum.ExchangeStrategy;
import com.jyt.terminal.commom.enums.BitEnum.HotWallet;
import com.jyt.terminal.commom.enums.BitEnum.SellType;
import com.jyt.terminal.commom.enums.BitEnum.VirtualCurrencyEnum;
import com.jyt.terminal.factory.IConstantFactory;
import com.jyt.terminal.util.SpringContextHolder;
import com.jyt.terminal.util.ToolUtil;

public class CryptoSettingsWarpper extends LanBaseControllerWarpper{
	private IConstantFactory constantFactory = SpringContextHolder.getBean(IConstantFactory.class);
    public CryptoSettingsWarpper(Object list) {
        super(list);
    }
    
    @Override
	protected void warpTheMap(String currentType,Map<String, Object> map) {
		if(MapUtils.isEmpty(map)) {
			return;
		}
		map.putAll(ToolUtil.toCamelCase(map));
		map.put("statusDesc", CryptoSettingsStatusEnum.getName(currentType,(Integer) map.get("status")));
		map.put("exchangeStrategyDesc", ExchangeStrategy.getName(currentType,(Integer) map.get("exchange_strategy")));
		map.put("virtualCurrencyDesc", VirtualCurrencyEnum.getName(currentType,(Integer) map.get("virtual_currency")));
		map.put("confirmationsDesc", SellType.valueOf((Integer) map.get("confirmations")));
		map.put("hotWalletDesc", HotWallet.valueOf((Integer) map.get("hotWallet")));
		map.put("exchangeDesc", Exchange.valueOf((Integer) map.get("exchange")));
	}
    
}
