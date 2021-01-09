package com.jyt.terminal.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jyt.terminal.commom.enums.EmailTemplateConstant;
import com.jyt.terminal.dao.CustomerMapper;
import com.jyt.terminal.dto.CustomerDTO;
import com.jyt.terminal.model.Customer;
import com.jyt.terminal.model.EmailSetting;
import com.jyt.terminal.model.EmailTemplate;
import com.jyt.terminal.service.ICustomerService;

/**
 * 客户信息
 * @className CustomerServiceImpl
 * @author wangwei
 * @date 2019年8月27日
 *
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements  ICustomerService {

	@Autowired
	private CustomerMapper customerMapper;
	@Autowired
	private EmailService emailService;
	@Autowired
	private EmailSettingServiceImpl emailSettingService;
	@Autowired
	private EmailTemplateServiceImpl emailTemplateService;
	
	@Override
	public List<Map<String, Object>> getCustomerList(Page<Map<String, Object>> page, CustomerDTO customerDTO) {
		return customerMapper.getCustomerList(page,customerDTO);
	}

	@Override
	public String sendCustomerEmail(Customer customer) {
		Map<String, Object> map = new HashMap<>();
		String verificationCode = createRegCode(6);
		map.put("verificationCode", verificationCode);
		EmailTemplate emailTemplate = emailTemplateService.selectOne(new EntityWrapper<EmailTemplate>().eq("code", EmailTemplateConstant.KYC_SEND_CODE));
		String result = emailTemplate.getTemplate();
		result = result.replace("#code#", verificationCode);
		map.put("result", result);
		EmailSetting emailSetting = emailSettingService.selectOne(new EntityWrapper<>());
		emailService.sendMail("email_template.html", "Verification Code", map,customer.getEmail(),emailSetting);
		return verificationCode;
	}
	
	//生成验证码
    private String createRegCode(int range) {
		String str = "0123456789";
		StringBuilder sb = new StringBuilder(range);
		for (int i = 0; i < range; i++) {
			//nextInt(int n) 该方法的作用是生成一个随机的int值，该值介于[0,n)的区间，也就是0到n之间的随机int值，包含0而不包含n。
			char ch = str.charAt(new Random().nextInt(str.length()));
			sb.append(ch);
		}
		return sb.toString();
	}

	@Override
	public List<Map<String, Object>> getKycReviewList(Page<Map<String, Object>> page, CustomerDTO customerDTO) {
		return customerMapper.getKycReviewList(page,customerDTO);
	}

}
