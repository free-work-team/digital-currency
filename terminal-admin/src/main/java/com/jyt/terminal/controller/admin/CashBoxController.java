package com.jyt.terminal.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jyt.terminal.commom.Permission;
import com.jyt.terminal.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.commom.PageFactory;
import com.jyt.terminal.commom.enums.BitEnum.CashBoxStatus;
import com.jyt.terminal.dto.CashBoxDTO;
import com.jyt.terminal.service.ICashBoxService;
import com.jyt.terminal.warpper.CashBoxWarpper;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/cashBox")
public class CashBoxController extends BaseController{
	
	private static String PREFIX = "/system/cashBox/";
	
	@Autowired
	private ICashBoxService cashBoxService;
	
	@RequestMapping(value = "",method = RequestMethod.GET)
	@Permission
	public String index(Model model){
		//钞箱状态枚举
    	List<Map<String,Object>> cashBoxStatus = new ArrayList<Map<String,Object>>();
  		for(CashBoxStatus param:CashBoxStatus.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("v1", param.getV1());
  			value.put("v2", param.getV2());
  			cashBoxStatus.add(value);
  		}
  		model.addAttribute("cashBoxStatus",cashBoxStatus);
		return PREFIX + "cashBox.html";
	}
	
	/**
     * 分页查询列表信息
     */
    @RequestMapping(value = "",method = RequestMethod.POST)
	@Permission
    @ResponseBody
    public Object list(HttpServletRequest request, CashBoxDTO cashBoxDTO) {
		Map<String, String> respParam = Utils.getAllRequestParam(request);
		String currentType = respParam.get("currentType");
    	Page<Map<String,Object>> page = new PageFactory<Map<String,Object>>().defaultPage();
        List<Map<String,Object>> result = this.cashBoxService.getCashBoxList(page, cashBoxDTO);
        page.setRecords((List<Map<String,Object>>) new CashBoxWarpper(result).warp(currentType));
        return super.packForBT(page);
    }

}
