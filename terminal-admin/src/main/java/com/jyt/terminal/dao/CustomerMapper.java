package com.jyt.terminal.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.dto.CustomerDTO;
import com.jyt.terminal.model.Customer;
import com.jyt.terminal.model.SmsSend;


/**
 * 客户信息管理
 * @className CustomerMapper
 * @author wangwei
 * @date 2019年8月27日
 *
 */
public interface CustomerMapper extends BaseMapper<Customer> {
	
	/**
	 * 分页查询客户信息列表
	 * @param page
	 * @param customerDTO
	 * @return
	 */
	List<Map<String, Object>> getCustomerList(@Param("page")Page<Map<String, Object>> page, @Param("entity")CustomerDTO customerDTO);
	
	/**
	 * 分页查询客户信息列表
	 * @param page
	 * @param customerDTO
	 * @return
	 */
	List<Map<String, Object>> getKycReviewList(@Param("page")Page<Map<String, Object>> page, @Param("entity")CustomerDTO customerDTO);
	
	/**
     * 根据订单号查询短信记录
     */
	Customer getByIdCardObserve(@Param("idCardObserve") String idCardObserve);
		
	
	
	
}