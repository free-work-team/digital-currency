package com.jyt.terminal.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.jyt.terminal.commom.enums.BitEnum.TradeType;
import com.jyt.terminal.model.TerminalSetting;
import com.jyt.terminal.service.ITerminalSettingService;


/**
 * 总览信息
 *
 * @author fengshuonan
 * @Date 2017年3月4日23:05:54
 */
@Controller
@RequestMapping("/blackboard")
public class BlackboardController extends BaseController {

    /*@Autowired
    private INoticeService noticeService;*/
	@Autowired
    private ITerminalSettingService terminalService;

    /**
     * 跳转到黑板
     */
    @RequestMapping("")
    public String blackboard(Model model) {
        // List<Map<String, Object>> notices = noticeService.list(null);
    	List<Map<String, Object>> notices = new ArrayList<Map<String,Object>>();
        model.addAttribute("noticeList", notices);
        
        List<TerminalSetting> terminalList = terminalService.selectList(new EntityWrapper<TerminalSetting>().groupBy("terminal_no"));
        model.addAttribute("terminalList",terminalList);
        //渠道类型枚举
  		/*List<Map<String,Object>> channeltype = new ArrayList<Map<String,Object>>();
  		for(ChannelType param:ChannelType.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("message", param.getMessage());
  			channeltype.add(value);
  		}
  		model.addAttribute("channeltype",channeltype);*/
        //交易类型枚举
  		List<Map<String,Object>> tradeType = new ArrayList<Map<String,Object>>();
  		for(TradeType param:TradeType.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("message", param.getMessage());
  			tradeType.add(value);
  		}
  		model.addAttribute("tradeType",tradeType);
        return "/blackboard.html";
    }
}
