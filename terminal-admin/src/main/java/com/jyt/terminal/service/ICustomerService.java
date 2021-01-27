package com.jyt.terminal.service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.jyt.terminal.dto.CustomerDTO;
import com.jyt.terminal.model.Customer;

/**
 * 客户信息 服务接口
 * @className ICustomerService
 * @author wangwei
 * @date 2019年8月27日
 *
 */
public interface ICustomerService extends IService<Customer> {
	/**
	 * 分页查询客户信息
	 * @param page
	 * @param customerDTO
	 * @return
	 */
	List<Map<String,Object>> getCustomerList(Page<Map<String,Object>> page,CustomerDTO customerDTO);
	/**
	 * 分页查询待审核的kyc信息
	 * @param page
	 * @param customerDTO
	 * @return
	 */
	List<Map<String,Object>> getKycReviewList(Page<Map<String,Object>> page,CustomerDTO customerDTO);
	
	/**
	 * 发送验证码
	 * @param customer
	 */
	String sendCustomerEmail(Customer customer);
	
	Customer getByIdCardObserve(String idCardObserv);

}
