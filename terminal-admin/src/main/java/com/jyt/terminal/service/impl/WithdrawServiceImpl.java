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
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.dao.WithdrawMapper;
import com.jyt.terminal.dto.SellDTO;
import com.jyt.terminal.exception.TerminalException;
import com.jyt.terminal.model.TradeStatistics;
import com.jyt.terminal.model.TransJoinOrder;
import com.jyt.terminal.model.Withdraw;
import com.jyt.terminal.service.IWithdrawService;
import com.jyt.terminal.util.HttpClientUtil;

/**
 * 
 * @author sunshubin
 * @date 2019年4月29日
 */
@Service
@Transactional
public class WithdrawServiceImpl extends ServiceImpl<WithdrawMapper, Withdraw> implements  IWithdrawService{

	
	private Logger log = LoggerFactory.getLogger(WithdrawServiceImpl.class);

	@Override
	public List<Map<String, Object>> getWithdrawList(Page<Withdraw> page, SellDTO sellDTO) {
		// TODO Auto-generated method stub
		return baseMapper.getSellList(page,sellDTO);
	}

	@Override
	public Map<String, Object> getWithdrawDetail(Integer id) {
		return baseMapper.getWithdrawDetail(id);
	}

	@Override
	public List<TradeStatistics> getWithdrawStatisticsList() {
		Map<String, String> currencyMap = new HashMap<String, String>();
		
		List<TradeStatistics> list = new ArrayList<TradeStatistics>();
		List<String> terminals = this.baseMapper.getWithdrawStatisticsTerNo();
		List<String> cryptoCurrencys = this.baseMapper.getWithdrawCryptoCurrencys();
		List<String> currencys = this.baseMapper.getWithdrawCurrencys();
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
			
				List<TransJoinOrder> withdrawStatisticsList = baseMapper.getWithdrawStatisticsList(terminalNo,cryptoCur,cur);
				for (TransJoinOrder withdraw : withdrawStatisticsList) {
					amount = amount.add(new BigDecimal(withdraw.getAmount()));
					cash = cash.add(new BigDecimal(withdraw.getCash()));
					BigDecimal feeBit = new BigDecimal(withdraw.getFee()).divide(new BigDecimal(100000000));
					fee = fee.add(feeBit.multiply(new BigDecimal(withdraw.getPrice())));
					if(StringUtils.isBlank(withdraw.getcFee())){
						withdraw.setcFee(new BigDecimal(0).toString());
					}
					cFee = cFee.add(new BigDecimal(withdraw.getcFee()));
					count++;
					if(StringUtils.isBlank(withdraw.getTransId())){
						//交易所数据为空
						BigDecimal profitBit = (new BigDecimal(withdraw.getFee()).subtract(new BigDecimal(withdraw.getcFee()))).divide(new BigDecimal(100000000));
						profit = profit.add(profitBit.multiply(new BigDecimal(withdraw.getPrice())));
					}else{
						if("2".equals(withdraw.getStatus())){
							//交易所交易成功
							String orderCurrency = withdraw.getOrderCurrency().split("-")[1];
							if(!orderCurrency.equals(withdraw.getTranCurrency())){
								//钞箱币种与交易所不同
								String rate = currencyMap.get(orderCurrency);
								if(StringUtils.isBlank(rate)){
									String resp ="";
									try {
										
										resp = HttpClientUtil.doHttpGet("https://api.coinbase.com/v2/exchange-rates?currency="+orderCurrency);
									} catch (Exception e) {
										log.error("查询汇率换算异常:{}",e);
										return null;
									}
									JSONObject respJson = JSONObject.parseObject(resp);
									rate = respJson.getJSONObject("data").getJSONObject("rates").getString(withdraw.getTranCurrency());
									currencyMap.put(orderCurrency, rate);
								}
							
								withdraw.setExecutedValue((new BigDecimal(withdraw.getExecutedValue()).multiply(new BigDecimal(rate))).toString());
							}
							
							profit = profit.add(new BigDecimal(withdraw.getExecutedValue()).subtract(new BigDecimal(withdraw.getFunds())).subtract(new BigDecimal(withdraw.getFillFees())));
						}else{
							//交易所交易失败
							BigDecimal profitBit = (new BigDecimal(withdraw.getFee()).subtract(new BigDecimal(withdraw.getcFee()))).divide(new BigDecimal(100000000));
							profit = profit.add(profitBit.multiply(new BigDecimal(withdraw.getPrice())));
						}
					}
				}
				statistics.setAmount(amount);
				statistics.setCash(cash);
				statistics.setFee(fee.setScale(2,BigDecimal.ROUND_HALF_UP));
				statistics.setcFee(cFee);
				statistics.setTradeType(2);
				statistics.setProfit(profit.setScale(2,BigDecimal.ROUND_HALF_UP));
				statistics.setCount(count);
				list.add(statistics);
			}
			}
		}		
		return list;
	}

	@Override
	public boolean inserOrUpdateWithdraw(List<Withdraw> withdrawList) {
		boolean flag = true;
    	for (Withdraw withdraw : withdrawList) {
    		Withdraw with = selectOne(new EntityWrapper<Withdraw>().eq("trans_id", withdraw.getTransId()));
    		if(with == null){
    			withdraw.setUpdateTime(new Date());
    			flag = insert(withdraw);
    		}else{
    			/*with.setcFee(withdraw.getcFee());
    			with.setTransStatus(withdraw.getTransStatus());
    			with.setRedeemStatus(withdraw.getRedeemStatus());
    			with.setRemark(withdraw.getRemark());
    			with.setOutCount(withdraw.getOutCount());
    			with.setUpdateTime(new Date());*/
    			withdraw.setId(with.getId());
    			withdraw.setUpdateTime(new Date());
    			flag = updateById(withdraw);
    		}
    		if(!flag){
    			log.info("上传提现数据，插入或更新失败:{}",withdraw);
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
