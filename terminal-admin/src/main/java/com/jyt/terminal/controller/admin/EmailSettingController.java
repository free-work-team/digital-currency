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
import com.jyt.terminal.dto.EmailSettingDTO;
import com.jyt.terminal.model.EmailSetting;
import com.jyt.terminal.service.IEmailSettingService;
import com.jyt.terminal.util.EncryptionUtils;
import com.jyt.terminal.util.ShiroKit;
import com.jyt.terminal.util.Tip;
import com.jyt.terminal.util.ToolUtil;

@Controller
@RequestMapping("/emailSetting")
public class EmailSettingController extends BaseController{

	private static String PREFIX = "/system/emailSetting/";
	
	@Autowired
	private IEmailSettingService emailSettingService;
	
	/**
     * 跳转到自定义发送邮箱设置页面
     */
    @RequestMapping("")
	@Permission
	public String index(Model model) {
    	EmailSetting entity = emailSettingService.selectOne(new EntityWrapper<EmailSetting>());
    	if(ToolUtil.isNotEmpty(entity)){
    		entity.setPassword(EncryptionUtils.decrypt(entity.getPassword()));
    	}
    	model.addAttribute("entity", entity);
        return PREFIX + "emailSetting.html";
    }
	
	/**
	 * 提交新增
	 */
	@RequestMapping("/add")
	@Permission
	@ResponseBody
	public Tip add(@Valid EmailSettingDTO emailSetting) {
		emailSetting.setPassword(EncryptionUtils.encrypt(emailSetting.getPassword()));
		emailSetting.setCreateUser(ShiroKit.getUser().getAccount());
		emailSetting.setCreateTime(new Date());
		this.emailSettingService.insert(emailSetting);
		return SUCCESS_TIP;
	}
	
	/**
	 * 提交修改
	 */
	@RequestMapping("/edit")
	@Permission
	@ResponseBody
	public Tip edit(@Valid EmailSetting emailSetting) {
		EmailSetting entity = emailSettingService.selectOne(new EntityWrapper<EmailSetting>());
		entity.setHost(emailSetting.getHost());
		entity.setEmail(emailSetting.getEmail());
		entity.setPassword(EncryptionUtils.encrypt(emailSetting.getPassword()));
		entity.setUpdateUser(ShiroKit.getUser().getAccount());
		entity.setUpdateTime(new Date());
		this.emailSettingService.updateById(entity);
		return SUCCESS_TIP;
	}
    
}
