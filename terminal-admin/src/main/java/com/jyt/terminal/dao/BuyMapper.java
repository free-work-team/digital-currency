package com.jyt.terminal.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.dto.BuyDTO;
import com.jyt.terminal.model.Buy;
import com.jyt.terminal.model.TransJoinOrder;
import com.jyt.terminal.model.TradeStatistics;


/**
 * <p>
 * 比特币交易流水表 Mapper 接口
 * </p>
 *
 * @author sunshubin
 * @since 2019-04-29
 */
public interface BuyMapper extends BaseMapper<Buy> {
	
	/**
	 * 分页查询购买流水
	 */
	public List<Map<String,Object>> getBuyList(@Param("page")Page<Buy> page,@Param("buy")BuyDTO buyDTO);
	
	/**
	 * 查询购买详情
	 */
	public Map<String,Object> getBuyDetail(@Param("id")Integer id);
	
	/**
	 * 购买流水汇总
	 */
	List<TransJoinOrder> getBuyStatisticsList(@Param("terminalNo")String terminalNo,@Param("cryptoCurrency")String cryptoCurrency,@Param("currency")String currency);
	
	/**
	 * 购买流水汇总
	 */
	List<String> getBuyStatisticsTerNo();
	/**
	 * 获取存在币种类型
	 */
	List<String> getBuyCryptoCurrencys();
	/**
	 * 获取存在法币币种类型
	 */
	List<String> getBuyCurrencys();

}