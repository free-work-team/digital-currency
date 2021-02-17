package com.jyt.terminal.controller.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.commom.PageFactory;
import com.jyt.terminal.commom.Permission;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.commom.enums.BitEnum.UserStatus;
import com.jyt.terminal.commom.node.ZTreeNode;
import com.jyt.terminal.dto.QueryAgencySettingDTO;
import com.jyt.terminal.exception.TerminalException;
import com.jyt.terminal.model.AgencySetting;
import com.jyt.terminal.service.IAgencySettingService;
import com.jyt.terminal.util.Tip;
import com.jyt.terminal.util.ToolUtil;
import com.jyt.terminal.util.Utils;
import com.jyt.terminal.warpper.AgencySettingWarpper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Controller
@RequestMapping("/agencySettings")
public class AgencySettingController extends BaseController{

	
	private static String PREFIX = "/system/agencySetting/";
	
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Autowired
	public IAgencySettingService agencySettingService;
	
	/**
     * 跳转到查看代理商列表
     */
    @RequestMapping("")
	@Permission
    public String index(Model model) {
    	//短信状态枚举
    	List<Map<String,Object>> agencyStatuList = new ArrayList<Map<String,Object>>();
  		for(UserStatus param:UserStatus.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("value", param.getValue());
  			value.put("cndesc", param.getCndesc());
  			value.put("endesc", param.getEndesc());
  			agencyStatuList.add(value);
  		}
  		model.addAttribute("agencyStatuList",agencyStatuList);
  		
        return PREFIX + "agencySetting.html";
    }
    
	/**
     * 查询代理商列表
     */
    @ApiOperation(value="查看代理商列表",notes="分页查询代理商列表")
	@RequestMapping(value = "",method = RequestMethod.POST)
	@ResponseBody
	@Permission
	public Object list(@ApiParam(name="查询代理商对象",required=true) HttpServletRequest request, QueryAgencySettingDTO querySmsDTO) {
		Map<String, String> respParam = Utils.getAllRequestParam(request);
		String currentType = respParam.get("currentType");
    	Page<Map<String, Object>> page = new PageFactory<Map<String, Object>>().defaultPage();
        List<Map<String, Object>> result = this.agencySettingService.getAgencyList(page, querySmsDTO);
        page.setRecords((List<Map<String, Object>>) new AgencySettingWarpper(result).warp(currentType));
        return super.packForBT(page);
    }
    
    
    /**
	 * 跳转到新增代理商页面
	 */
	@RequestMapping(value = "/add",method = RequestMethod.GET)
	@Permission
	public String addView(Model model) {
		//短信状态枚举
    	List<Map<String,Object>> agencyStatuList = new ArrayList<Map<String,Object>>();
  		for(UserStatus param:UserStatus.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("value", param.getValue());
  			value.put("cndesc", param.getCndesc());
  			value.put("endesc", param.getEndesc());
  			agencyStatuList.add(value);
  		}
  		model.addAttribute("agencyStatuList",agencyStatuList);
		model.addAttribute("agencyId", 1);
		return PREFIX + "agency_settings_add.html";
	}
    
	/**
	 * 提交新增代理商
	 */
	@RequestMapping(value = "/add",method = RequestMethod.POST)
	@Permission
	@ResponseBody
	public Tip add(@Valid QueryAgencySettingDTO agencySettingDTO, BindingResult result) {
		if (result.hasErrors()) {
			throw new TerminalException(BizExceptionEnum.REQUEST_NULL);
		}

		if(ToolUtil.isEmpty(agencySettingDTO.getAgencyPhone()))
			throw new TerminalException(BizExceptionEnum.AGENCY_PHONE_IS_NULL);
		
		if(ToolUtil.isEmpty(agencySettingDTO.getAgencyEmail()))
			throw new TerminalException(BizExceptionEnum.AGENCY_PHONE_IS_NULL);
		
		if(ToolUtil.isEmpty(agencySettingDTO.getAgencyName()))
			throw new TerminalException(BizExceptionEnum.AGENCY_NAME_IS_NULL);
		
		// 根据手机号判断代理商是否重复
		AgencySetting agencyByPhone = this.agencySettingService.getAgencyByPhone(agencySettingDTO);
		if (agencyByPhone != null) {
			throw new TerminalException(BizExceptionEnum.AGENCY_PHONE_ALREADY_REGISTERED);
		}
		AgencySetting agencyByEmail = this.agencySettingService.getAgencyByEmail(agencySettingDTO);
		if (agencyByEmail != null) {
			throw new TerminalException(BizExceptionEnum.AGENCY_EMAIL_ALREADY_REGISTERED);
		}
		agencySettingDTO.setCreateDate(new Date());
		agencySettingDTO.setIsParent("1");
		
		boolean isSuccess=this.agencySettingService.insert(agencySettingDTO);
		LOGGER.info("插入是否成功:{}",isSuccess);
		
		return SUCCESS_TIP;
	}
	
	/**
     * 获取代理商列表
     */
    @RequestMapping(value = "/agencyTreeListById/{agencyId}")
    @ResponseBody
    public List<ZTreeNode> agencyTreeListByUserId(@PathVariable Integer agencyId) {
    	AgencySetting agency = this.agencySettingService.selectById(agencyId);
        if (ToolUtil.isEmpty(agency)) {
        	List<ZTreeNode> roleTreeList = this.agencySettingService.agencyTreeList();
            return roleTreeList;
        } else {
            List<ZTreeNode> roleTreeListByUserId = this.agencySettingService.agencyTreeListByRoleId(agencyId);
            return roleTreeListByUserId;
        }    	
    }
	
   
    
    /**
	 * 跳转到编辑代理商页面
	 */
	@RequestMapping(value = "/edit/{agencyId}",method = RequestMethod.GET)
	@Permission
	public String editView(Model model) {
		//短信状态枚举
    	List<Map<String,Object>> agencyStatuList = new ArrayList<Map<String,Object>>();
  		for(UserStatus param:UserStatus.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("value", param.getValue());
  			value.put("cndesc", param.getCndesc());
  			value.put("endesc", param.getEndesc());
  			agencyStatuList.add(value);
  		}
  		model.addAttribute("agencyStatuList",agencyStatuList);
		model.addAttribute("agencyId", 1);
		return PREFIX + "agency_settings_edit.html";
	}
	
	
}
