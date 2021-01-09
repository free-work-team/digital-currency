package com.jyt.terminal.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jyt.terminal.dao.TradeStatisticsMapper;
import com.jyt.terminal.dto.TradeStatisticsDTO;
import com.jyt.terminal.model.TradeStatistics;
import com.jyt.terminal.service.ITradeStatisticsService;

/**
 * 
 * @className TradeStatisticsServiceImpl
 * @author wangwei
 * @date 2019年8月27日
 *
 */
@Service
public class TradeStatisticsServiceImpl extends ServiceImpl<TradeStatisticsMapper, TradeStatistics> implements  ITradeStatisticsService{

	@Autowired
	private TradeStatisticsMapper tradeStatisticsMapper;
	
	@Override
	public List<TradeStatistics> queryStatisticsInfo(TradeStatisticsDTO tradeStatisticsDTO) {
		return tradeStatisticsMapper.queryStatisticsInfo(tradeStatisticsDTO);
	}

	@Override
	public List<Map<String, Object>> getTradeStatisticsList(Page<TradeStatistics> page,
			TradeStatisticsDTO tradeStatisticsDTO) {
		return baseMapper.getTradeStatisticsList(page,tradeStatisticsDTO);
	}

	@Override
	public Map<String, Object> getTradeStatisticsSum(TradeStatisticsDTO tradeStatisticsDTO) {
		return baseMapper.getTradeStatisticsSum(tradeStatisticsDTO);
	}
	
}
