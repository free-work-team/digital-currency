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
import com.jyt.terminal.commom.enums.SmsEnum.SmsStatus;
import com.jyt.terminal.dto.QuerySmsDTO;
import com.jyt.terminal.service.ISmsSendService;
import com.jyt.terminal.util.Utils;
import com.jyt.terminal.warpper.SmsWarpper;
import com.jyt.terminal.warpper.TerminalWarpper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Controller
@RequestMapping("/sms")
public class SmsController extends BaseController{
	
	private static String PREFIX = "/system/sms/";
	
	@Autowired
	public ISmsSendService smsSendService;
		
	/**
     * 跳转到查看短信列表
     */
    @RequestMapping("")
	@Permission
    public String index(Model model) {
    	//短信状态枚举
    	List<Map<String,Object>> smsOrderStatuList = new ArrayList<Map<String,Object>>();
  		for(SmsStatus param:SmsStatus.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("cndesc", param.getCndesc());
  			value.put("endesc", param.getEndesc());
  			smsOrderStatuList.add(value);
  		}
  		model.addAttribute("smsOrderStatuList",smsOrderStatuList);
  		
        return PREFIX + "sms.html";
    }
    
	/**
     * 查询短信列表
     */
    @ApiOperation(value="查看发送的短信列表",notes="分页查询发送短信列表")
	@RequestMapping(value = "",method = RequestMethod.POST)
	@ResponseBody
	@Permission
	public Object list(@ApiParam(name="查询短信对象",required=true) HttpServletRequest request, QuerySmsDTO querySmsDTO) {
		Map<String, String> respParam = Utils.getAllRequestParam(request);
		String currentType = respParam.get("currentType");
    	Page<Map<String, Object>> page = new PageFactory<Map<String, Object>>().defaultPage();
        List<Map<String, Object>> result = this.smsSendService.getSmsList(page, querySmsDTO);
        page.setRecords((List<Map<String, Object>>) new SmsWarpper(result).warp(currentType));
        return super.packForBT(page);
    }
	
    
	
	
}
