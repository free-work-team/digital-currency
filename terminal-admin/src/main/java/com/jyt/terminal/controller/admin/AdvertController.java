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
import com.jyt.terminal.model.Advert;
import com.jyt.terminal.service.IAdvertService;
import com.jyt.terminal.util.ShiroKit;
import com.jyt.terminal.util.Tip;

@Controller
@RequestMapping("/advert")
public class AdvertController extends BaseController{

	private static String PREFIX = "/system/advert/";
	
	@Autowired
	private IAdvertService advertService;
	
	/**
     * 跳转到自定义发送邮箱设置页面
     */
    @RequestMapping("")
	@Permission
	public String index(Model model) {
    	Advert entity = advertService.selectOne(new EntityWrapper<Advert>());
    
    	model.addAttribute("entity", entity);
        return PREFIX + "advertSetting.html";
    }
	
	/**
	 * 提交新增
	 */
	@RequestMapping("/add")
	@Permission
	@ResponseBody
	public Tip add(@Valid Advert advert) {
		
		advert.setCreateUser(ShiroKit.getUser().getAccount());
		advert.setCreateTime(new Date());
		this.advertService.insert(advert);
		return SUCCESS_TIP;
	}
	
	/**
	 * 提交修改
	 */
	@RequestMapping("/edit")
	@Permission
	@ResponseBody
	public Tip edit(@Valid Advert advert) {
		Advert entity = advertService.selectOne(new EntityWrapper<Advert>());
		entity.setAdvertTitle(advert.getAdvertTitle());
		entity.setAdvertContent(advert.getAdvertContent());
		
		entity.setUpdateUser(ShiroKit.getUser().getAccount());
		entity.setUpdateTime(new Date());
		this.advertService.updateById(entity);
		return SUCCESS_TIP;
	}
    
}
