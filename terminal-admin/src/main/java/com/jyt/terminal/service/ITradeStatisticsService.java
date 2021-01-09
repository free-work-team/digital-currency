package com.jyt.terminal.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.jyt.terminal.dto.TradeStatisticsDTO;
import com.jyt.terminal.model.TradeStatistics;

/**
 * 交易流水统计
 * @className ITradeStatisticsService
 * @author wangwei
 * @date 2019年8月27日
 *
 */
public interface ITradeStatisticsService extends IService<TradeStatistics> {
	
	public List<Map<String,Object>> getTradeStatisticsList(Page<TradeStatistics> page,TradeStatisticsDTO tradeStatisticsDTO);
	
	public Map<String,Object> getTradeStatisticsSum(TradeStatisticsDTO tradeStatisticsDTO);
	/**
	 * 首页统计图查询
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	List<TradeStatistics> queryStatisticsInfo(TradeStatisticsDTO tradeStatisticsDTO);

}
