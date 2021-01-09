package com.jyt.terminal.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.jyt.terminal.dto.OrderDTO;
import com.jyt.terminal.model.Order;


/**
 * <p>
 * 比特币订单表 服务类
 * </p>
 *
 * @author sunshubin
 * @since 2019-09-19
 */
public interface IOrderService extends IService<Order> {
	
	/**
	 * 分页查询订单
	 */
	public List<Map<String,Object>> getOrderList(Page<Order> page,OrderDTO orderDTO);
	
	public boolean inserOrUpdateOrder(List<Order> orderList);
	
	/**
	 * 查询卖币交易利润
	 * @param orderDTO
	 * @return
	 */
	List<Order> getProfitSum(OrderDTO orderDTO);

}
