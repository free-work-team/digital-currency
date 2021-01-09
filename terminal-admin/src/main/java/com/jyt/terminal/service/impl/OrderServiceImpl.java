package com.jyt.terminal.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.dao.OrderMapper;
import com.jyt.terminal.dto.OrderDTO;
import com.jyt.terminal.exception.TerminalException;
import com.jyt.terminal.model.Order;
import com.jyt.terminal.service.IOrderService;

/**
 * 
 * @author sunshubin
 * @date 2019年4月29日
 */
@Service
@Transactional
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements  IOrderService{

	
	
	private Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Override
	public boolean inserOrUpdateOrder(List<Order> orderList) {
		boolean flag = true;
    	for (Order order : orderList) {
    		
    		Order o = selectOne(new EntityWrapper<Order>().eq("trans_id", order.getTransId()));
    		if(o == null){
    			order.setUpdateTime(new Date());
    			flag = insert(order);
    		}else{
    			order.setUpdateTime(new Date());
    			flag = updateById(order);
    		}
    		if(!flag){
    			log.info("上传订单数据，插入或更新失败:{}",order);
    			throw new TerminalException(BizExceptionEnum.FAIL);
    		}
		}
    	return flag;
	}

	@Override
	public List<Map<String, Object>> getOrderList(Page<Order> page, OrderDTO orderDTO) {
		
		return baseMapper.getOrderList(page,orderDTO);
	}

	@Override
	public List<Order> getProfitSum(OrderDTO orderDTO) {
		return baseMapper.getProfitSum(orderDTO);
	}
	
	
	
}
