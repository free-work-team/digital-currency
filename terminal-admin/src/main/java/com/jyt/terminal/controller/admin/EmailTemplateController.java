package com.jyt.terminal.controller.admin;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.jyt.terminal.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.commom.PageFactory;
import com.jyt.terminal.commom.Permission;
import com.jyt.terminal.model.EmailTemplate;
import com.jyt.terminal.service.IEmailTemplateService;
import com.jyt.terminal.util.ShiroKit;
import com.jyt.terminal.util.Tip;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/emailTemplate")
public class EmailTemplateController extends BaseController{

private static String PREFIX = "/system/emailTemplate/";
	
	@Autowired
	public IEmailTemplateService emailTemplateService;
	
	/**
     * 跳转到邮件模板页面
     */
    @RequestMapping(value = "",method = RequestMethod.GET)
	@Permission
    public String index(Model model) {
        return PREFIX + "emailTemplate.html";
    }
    
    /**
     * 分页查询邮件模板列表
     */
    @ApiOperation(value="查询邮件模板列表",notes="分页查询邮件模板列表")
    @RequestMapping(value = "",method = RequestMethod.POST)
	@Permission
	@ResponseBody
    public Object list(@ApiParam(name="查询邮件模板列表",required=true) HttpServletRequest request, EmailTemplate emailTemplate) {
		Map<String, String> respParam = Utils.getAllRequestParam(request);
		String currentType = respParam.get("currentType");
        Page<Map<String,Object>> page = new PageFactory<Map<String,Object>>().defaultPage();
        List<Map<String,Object>> result = this.emailTemplateService.getEmailTemplateList(page, emailTemplate);
        page.setRecords(result);
        return super.packForBT(page);
    }
	
    /**
     * 跳转修改页面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/edit/{id}",method = RequestMethod.GET)
	@Permission
    public String openEditView(@PathVariable Integer id,Model model){
    	EmailTemplate emailTemplate = emailTemplateService.selectById(id);
    	model.addAttribute("emailTemplate", emailTemplate);
    	return PREFIX + "emailTemplate_edit.html";
    }
    
    /**
	 * 提交修改
	 */
	@RequestMapping(value = "/edit",method = RequestMethod.POST)
	@Permission
	@ResponseBody
	public Tip edit(@Valid EmailTemplate emailTemplate) {
		EmailTemplate entity = emailTemplateService.selectById(emailTemplate.getId());
		entity.setCode(emailTemplate.getCode());;
		entity.setName(emailTemplate.getName());
		entity.setTemplate(emailTemplate.getTemplate());
		entity.setUpdateUser(ShiroKit.getUser().getAccount());
		entity.setUpdateTime(new Date());
		this.emailTemplateService.updateById(entity);
		return SUCCESS_TIP;
	}
	/**
	 * 跳转详情
	 */
	@RequestMapping("/details/{id}")
	@Permission
    public String details(@PathVariable Integer id,Model model){
    	EmailTemplate emailTemplate = emailTemplateService.selectById(id);
    	model.addAttribute("emailTemplate", emailTemplate);
    	return PREFIX + "emailTemplate_details.html";
    }
}
