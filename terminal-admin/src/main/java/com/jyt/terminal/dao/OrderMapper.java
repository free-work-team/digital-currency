package com.jyt.terminal.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.dto.OrderDTO;
import com.jyt.terminal.dto.SellDTO;
import com.jyt.terminal.model.Order;
import com.jyt.terminal.model.Withdraw;


/**
 * <p>
 * 比特币订单表 Mapper 接口
 * </p>
 *
 * @author sunshubin
 * @since 2019-09-19
 */
public interface OrderMapper extends BaseMapper<Order> {
	
	/**
	 * 分页查询订单流水
	 */
	public List<Map<String,Object>> getOrderList(@Param("page")Page<Order> page, @Param("order")OrderDTO orderDto);
	
	/**
	 * 查询卖币交易利润
	 * @param orderDTO
	 * @return
	 */
	List<Order> getProfitSum(@Param("entity")OrderDTO orderDTO);

}