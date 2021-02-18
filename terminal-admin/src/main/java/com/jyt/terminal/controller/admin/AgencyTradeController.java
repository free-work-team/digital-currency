package com.jyt.terminal.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.commom.PageFactory;
import com.jyt.terminal.commom.Permission;
import com.jyt.terminal.commom.enums.BitEnum.UserStatus;
import com.jyt.terminal.dto.QueryAgencyProfitDTO;
import com.jyt.terminal.service.IAgencyProfitService;
import com.jyt.terminal.util.Utils;
import com.jyt.terminal.warpper.AgencyProfitWarpper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Controller
@RequestMapping("/agencyTrade")
public class AgencyTradeController extends BaseController{

private static String PREFIX = "/system/agencyTrade/";
	
	@Autowired
	public IAgencyProfitService agencyProfitService;
	
	/**
     * 跳转到查看代理商分润记录列表
     */
    @RequestMapping("")
	@Permission
    public String index(Model model) {
    	//
    	List<Map<String,Object>> agencyStatuList = new ArrayList<Map<String,Object>>();
  		for(UserStatus param:UserStatus.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("value", param.getValue());
  			value.put("cndesc", param.getCndesc());
  			value.put("endesc", param.getEndesc());
  			agencyStatuList.add(value);
  		}
  		model.addAttribute("agencyStatuList",agencyStatuList);
  		
        return PREFIX + "agencyTrade.html";
    }
    
	/**
     * 查询短信列表
     */
    @SuppressWarnings("unchecked")
	@ApiOperation(value="查看代理商分润记录列表",notes="分页查询代理商分润记录列表")
	@RequestMapping(value = "",method = RequestMethod.POST)
	@ResponseBody
	@Permission
	public Object list(@ApiParam(name="查询代理商分润记录",required=true) HttpServletRequest request, QueryAgencyProfitDTO querySmsDTO) {
		Map<String, String> respParam = Utils.getAllRequestParam(request);
		String currentType = respParam.get("currentType");
    	Page<Map<String, Object>> page = new PageFactory<Map<String, Object>>().defaultPage();
        List<Map<String, Object>> result = this.agencyProfitService.getAgencyProfitList(page, querySmsDTO);
        page.setRecords((List<Map<String, Object>>) new AgencyProfitWarpper(result).warp(currentType));
        return super.packForBT(page);
    }
	
	
}