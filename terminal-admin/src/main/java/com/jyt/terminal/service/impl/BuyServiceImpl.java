package com.jyt.terminal.service.impl;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.dao.BuyMapper;
import com.jyt.terminal.dto.BuyDTO;
import com.jyt.terminal.exception.TerminalException;
import com.jyt.terminal.model.Buy;
import com.jyt.terminal.model.TradeStatistics;
import com.jyt.terminal.model.TransJoinOrder;
import com.jyt.terminal.service.IBuyService;
import com.jyt.terminal.util.HttpClientUtil;

/**
 * 
 * @author sunshubin
 * @date 2019年4月29日
 */
@Service
public class BuyServiceImpl extends ServiceImpl<BuyMapper, Buy> implements  IBuyService{
	private Logger LOGGER = LoggerFactory.getLogger(BuyServiceImpl.class);
	

	@Override
	public List<Map<String, Object>> getBuyList(Page<Buy> page, BuyDTO buyDTO) {
		return baseMapper.getBuyList(page,buyDTO);
	}

	@Override
	public Map<String, Object> getBuyDetail(Integer id) {
		return baseMapper.getBuyDetail(id);
	}
	
	@Override
	public List<TradeStatistics> getBuyStatisticsList() {
		Map<String, String> currencyMap = new HashMap<String, String>();
		
		
		List<TradeStatistics> list = new ArrayList<TradeStatistics>();
		List<String> terminals = this.baseMapper.getBuyStatisticsTerNo();
		List<String> cryptoCurrencys = this.baseMapper.getBuyCryptoCurrencys();
		List<String> currencys = this.baseMapper.getBuyCurrencys();
		for (String terminalNo : terminals) {
			for (String cryptoCur : cryptoCurrencys) {
				for (String cur : currencys) {	
					TradeStatistics statistics = new TradeStatistics();
					statistics.setDate(getDate());
					statistics.setTerminalNo(terminalNo);
					statistics.setCryptoCurrency(cryptoCur);
					statistics.setCurrency(cur);
					BigDecimal amount = new BigDecimal(0);
					BigDecimal cash = new BigDecimal(0);
					BigDecimal fee = new BigDecimal(0);
					BigDecimal cFee = new BigDecimal(0);
					BigDecimal profit = new BigDecimal(0);
					Integer count = 0;
				
					List<TransJoinOrder> buyStatisticsList = baseMapper.getBuyStatisticsList(terminalNo,cryptoCur,cur);
					for (TransJoinOrder buy : buyStatisticsList) {
						amount = amount.add(new BigDecimal(buy.getAmount()));
						cash = cash.add(new BigDecimal(buy.getCash()));
						BigDecimal feeBit = new BigDecimal(buy.getFee()).divide(new BigDecimal(100000000));
						fee = fee.add(feeBit.multiply(new BigDecimal(buy.getPrice())));
						if(StringUtils.isBlank(buy.getcFee())){
							buy.setcFee(new BigDecimal(0).toString());
						}
						cFee = cFee.add(new BigDecimal(buy.getcFee()));
						count++;
						if(StringUtils.isBlank(buy.getTransId())){
							//交易所数据为空
							BigDecimal profitBit = (new BigDecimal(buy.getFee()).subtract(new BigDecimal(buy.getcFee()))).divide(new BigDecimal(100000000));
							profit = profit.add(profitBit.multiply(new BigDecimal(buy.getPrice())));
						}else{
							if("2".equals(buy.getStatus())){
								//交易所交易成功
								String orderCurrency = buy.getOrderCurrency().split("-")[1];
								if(!orderCurrency.equals(buy.getTranCurrency())){
									//钞箱币种与交易所不同
									String rate = currencyMap.get(orderCurrency);
									if(StringUtils.isBlank(rate)){
										String resp ="";
										try {
											
											resp = HttpClientUtil.doHttpGet("https://api.coinbase.com/v2/exchange-rates?currency="+orderCurrency);
										} catch (Exception e) {
											LOGGER.error("查询汇率换算异常:{}",e);
											return null;
										}
										JSONObject respJson = JSONObject.parseObject(resp);
										rate = respJson.getJSONObject("data").getJSONObject("rates").getString(buy.getTranCurrency());
										currencyMap.put(orderCurrency, rate);
									}
								
									buy.setExecutedValue((new BigDecimal(buy.getExecutedValue()).multiply(new BigDecimal(rate))).toString());
								}
							
								profit = profit.add(new BigDecimal(buy.getFunds()).subtract(new BigDecimal(buy.getExecutedValue())).subtract(new BigDecimal(buy.getFillFees())));
							}else{
								//交易所交易失败
								BigDecimal profitBit = (new BigDecimal(buy.getFee()).subtract(new BigDecimal(buy.getcFee()))).divide(new BigDecimal(100000000));
								profit = profit.add(profitBit.multiply(new BigDecimal(buy.getPrice())));
							}
						}
					}
					statistics.setAmount(amount);
					statistics.setCash(cash);
					statistics.setFee(fee.setScale(2,BigDecimal.ROUND_HALF_UP));
					statistics.setcFee(cFee);
					statistics.setTradeType(1);
					statistics.setProfit(profit.setScale(2,BigDecimal.ROUND_HALF_UP));
					statistics.setCount(count);
					list.add(statistics);
				}
			}
		}		
		return list;
	}

	@Override
	public boolean inserOrUpdateBuy(List<Buy> buyList) {
		boolean flag = true;
    	for (Buy buy : buyList) {
    		Buy entity = selectOne(new EntityWrapper<Buy>().eq("trans_id", buy.getTransId()));
    		if(entity == null){
    			buy.setUpdateTime(new Date());
    			flag = insert(buy);
    		}else{
    			buy.setId(entity.getId());
    			buy.setUpdateTime(new Date());
    			flag = updateById(buy);
    		}
    		if(!flag){
    			LOGGER.info("上传买入数据，插入或更新失败:{}",JSONObject.toJSONString(buy));
    			throw new TerminalException(BizExceptionEnum.FAIL);
    		}
		}
    	return flag;
	}
	
	private String getDate(){
		Date date=new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(calendar.DATE, -1);
		date = calendar.getTime();
		SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
		String dateString = format.format(date);
		return dateString;
	}
	
}
