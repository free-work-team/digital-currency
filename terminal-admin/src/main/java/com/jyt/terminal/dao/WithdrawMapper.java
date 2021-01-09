package com.jyt.terminal.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.dto.SellDTO;
import com.jyt.terminal.model.TradeStatistics;
import com.jyt.terminal.model.TransJoinOrder;
import com.jyt.terminal.model.Withdraw;


/**
 * <p>
 * 比特币提现流水表 Mapper 接口
 * </p>
 *
 * @author sunshubin
 * @since 2019-04-29
 */
public interface WithdrawMapper extends BaseMapper<Withdraw> {
	/**
	 * 分页查询卖币流水
	 */
	public List<Map<String,Object>> getSellList(@Param("page")Page<Withdraw> page, @Param("sell")SellDTO sellDTO);
	
	/**
	 * 查询提现详情
	 */
	public Map<String, Object> getWithdrawDetail(@Param("id")Integer id);
	
	/**
	 * 卖出流水汇总
	 */
	List<TransJoinOrder> getWithdrawStatisticsList(@Param("terminalNo")String terminalNo,@Param("cryptoCurrency")String cryptoCurrency,@Param("currency")String currency);
	
	/**
	 * 卖出流水汇总
	 */
	List<String> getWithdrawStatisticsTerNo();
	/**
	 * 获取存在加密币种类型
	 */
	List<String> getWithdrawCryptoCurrencys();
	/**
	 * 获取存在法币币种类型
	 */
	List<String> getWithdrawCurrencys();
	
}