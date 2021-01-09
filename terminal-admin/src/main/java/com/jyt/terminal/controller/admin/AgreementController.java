package com.jyt.terminal.controller.admin;

import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.jyt.terminal.commom.Permission;
import com.jyt.terminal.model.Agreement;
import com.jyt.terminal.service.IAgreementService;
import com.jyt.terminal.util.ShiroKit;
import com.jyt.terminal.util.Tip;

@Controller
@RequestMapping("/agreement")
public class AgreementController extends BaseController{

	private static String PREFIX = "/system/agreement/";
	
	@Autowired
	private IAgreementService agreementService;
	
	/**
     * 跳转到自定义协议页面
     */
    @RequestMapping("")
	@Permission
	public String index(Model model) {
    	Agreement entity = agreementService.selectOne(new EntityWrapper<Agreement>());
    	model.addAttribute("entity", entity);
        return PREFIX + "agreement.html";
    }
	
	/**
	 * 提交新增
	 */
	@RequestMapping("/add")
	@Permission
	@ResponseBody
	public Tip add(@Valid Agreement agreement) {
		
		agreement.setCreateUser(ShiroKit.getUser().getAccount());
		agreement.setCreateTime(new Date());
		this.agreementService.insert(agreement);
		return SUCCESS_TIP;
	}
	
	/**
	 * 提交修改
	 */
	@RequestMapping("/edit")
	@Permission
	@ResponseBody
	public Tip edit(@Valid Agreement agreement) {
		Agreement entity = agreementService.selectOne(new EntityWrapper<Agreement>());
		entity.setAgreementTitle(agreement.getAgreementTitle());
		entity.setAgreementContent(agreement.getAgreementContent());
		entity.setPrivacyPolicyTitle(agreement.getPrivacyPolicyTitle());
		entity.setPrivacyPolicyContent(agreement.getPrivacyPolicyContent());
		
		entity.setUpdateUser(ShiroKit.getUser().getAccount());
		entity.setUpdateTime(new Date());
		this.agreementService.updateById(entity);
		return SUCCESS_TIP;
	}
    
}
