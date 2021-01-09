package com.jyt.terminal.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.dto.TradeStatisticsDTO;
import com.jyt.terminal.model.TradeStatistics;


/**
 * 比特币交易流水统计表 Mapper 接口
 * @className TradeStatisticsMapper
 * @author wangwei
 * @date 2019年8月27日
 *
 */
public interface TradeStatisticsMapper extends BaseMapper<TradeStatistics> {
	
	/**
	 * 首页统计图查询
	 */
	List<TradeStatistics> queryStatisticsInfo(@Param("entity")TradeStatisticsDTO tradeStatisticsDTO);

	List<Map<String, Object>> getTradeStatisticsList(@Param("page")Page<TradeStatistics> page, @Param("entity")TradeStatisticsDTO tradeStatisticsDTO);

	Map<String, Object> getTradeStatisticsSum(@Param("entity")TradeStatisticsDTO tradeStatisticsDTO);
	

}