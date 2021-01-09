package com.jyt.terminal.controller.admin;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jyt.terminal.commom.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.commom.PageFactory;
import com.jyt.terminal.commom.PageInfoBT;
import com.jyt.terminal.commom.enums.BitEnum.Currency;
import com.jyt.terminal.commom.enums.BitEnum.VirtualCurrencyEnum;
import com.jyt.terminal.dto.TradeStatisticsDTO;
import com.jyt.terminal.model.TerminalSetting;
import com.jyt.terminal.model.TradeStatistics;
import com.jyt.terminal.service.ITerminalSettingService;
import com.jyt.terminal.service.ITradeStatisticsService;
import com.jyt.terminal.warpper.TradeStatisticsWarpper;

import io.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/tradeStatistics")
public class TradeStatisticsController extends BaseController{
	
	private static String PREFIX = "/system/statistics/";
	
	@Autowired
	public ITradeStatisticsService tradeStatisticsService;
	
	@Autowired
    private ITerminalSettingService terminalService;
	
	@RequestMapping(value = "/buy",method = RequestMethod.GET)
	@Permission
    public String buy(Model model) {
		List<TerminalSetting> terminalList = terminalService.selectList(new EntityWrapper<TerminalSetting>().groupBy("terminal_no"));
        model.addAttribute("terminalList",terminalList);
        model.addAttribute("type",1);
        
        //交易类型枚举
  		List<Map<String,Object>> tradeType = new ArrayList<Map<String,Object>>();
  		//加密币种枚举
    	List<Map<String,Object>> virtualCurrencyEnum = new ArrayList<Map<String,Object>>();
  		for(VirtualCurrencyEnum param:VirtualCurrencyEnum.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("value", param.getValue());
  			value.put("endesc", param.getEndesc());
  			value.put("cndesc", param.getCndesc());
  			virtualCurrencyEnum.add(value);
  		}
  		model.addAttribute("virtualCurrencyEnum",virtualCurrencyEnum);
        return PREFIX + "statisticsList.html";
    }
	
	@RequestMapping(value = "/sell",method = RequestMethod.GET)
	@Permission
	public String sell(Model model) {
		List<TerminalSetting> terminalList = terminalService.selectList(new EntityWrapper<TerminalSetting>().groupBy("terminal_no"));
        model.addAttribute("terminalList",terminalList);
        //交易类型枚举
  		List<Map<String,Object>> tradeType = new ArrayList<Map<String,Object>>();
  		 model.addAttribute("type",2);
  		//加密币种枚举
     	List<Map<String,Object>> virtualCurrencyEnum = new ArrayList<Map<String,Object>>();
   		for(VirtualCurrencyEnum param:VirtualCurrencyEnum.values()){
   			Map<String, Object> value = new HashMap<String,Object>();
   			value.put("value", param.getValue());
   			value.put("endesc", param.getEndesc());
   			value.put("cndesc", param.getCndesc());
   			virtualCurrencyEnum.add(value);
   		}
   		model.addAttribute("virtualCurrencyEnum",virtualCurrencyEnum);
        return PREFIX + "statisticsList.html";
    }


	@RequestMapping(value = "/buy",method = RequestMethod.POST)
	@Permission
	@ResponseBody
    public Object buyList(@ApiParam(name="查询统计列表",required=true) TradeStatisticsDTO statisticsDTO) {
        Page<TradeStatistics> page = new PageFactory<TradeStatistics>().defaultPage();
        statisticsDTO.setTradeType(1);
        List<Map<String,Object>> result = this.tradeStatisticsService.getTradeStatisticsList(page, statisticsDTO);
        Map<String, Object> map = tradeStatisticsService.getTradeStatisticsSum(statisticsDTO);
        if(map == null){
        	map = new HashMap<String, Object>();
        	map.put("amount", 0);
        	map.put("fee", 0);
        	map.put("profit", 0);
        	map.put("count", 0);
        	map.put("cash", 0);
        }
        page.setRecords((List<TradeStatistics>) new TradeStatisticsWarpper(result).warp());
        PageInfoBT<TradeStatistics> pageInfo = super.packForBT(page);
        pageInfo.setResult(map);
        return pageInfo;
    }
	
	@RequestMapping(value = "/sell",method = RequestMethod.POST)
	@Permission
	@ResponseBody
    public Object sellList(@ApiParam(name="查询统计列表",required=true) TradeStatisticsDTO statisticsDTO) {
        Page<TradeStatistics> page = new PageFactory<TradeStatistics>().defaultPage();
        statisticsDTO.setTradeType(2);
        List<Map<String,Object>> result = this.tradeStatisticsService.getTradeStatisticsList(page, statisticsDTO);
        Map<String, Object> map = tradeStatisticsService.getTradeStatisticsSum(statisticsDTO);
        if(map == null){
        	map = new HashMap<String, Object>();
        	map.put("amount", 0);
        	map.put("fee", 0);
        	map.put("profit", 0);
        	map.put("count", 0);
        	map.put("cash", 0);
        }
        page.setRecords((List<TradeStatistics>) new TradeStatisticsWarpper(result).warp());
        PageInfoBT<TradeStatistics> pageInfo = super.packForBT(page);
        pageInfo.setResult(map);
        return pageInfo;
    }
	
	/**
     * 统计图查询
     */
	@RequestMapping("/queryStatisticsInfo")
	@Permission
	@ResponseBody
    public Object queryStatisticsInfo(TradeStatisticsDTO tradeStatisticsDTO) {
        List<TradeStatistics> info = this.tradeStatisticsService.queryStatisticsInfo(tradeStatisticsDTO);
        List<String> dates = getBetweenDates(tradeStatisticsDTO.getBeginTime(),tradeStatisticsDTO.getEndTime());
        
		for (String date : dates) {
			boolean need = true;
			for (TradeStatistics tradeStatistics : info) {
				if(date.equals(tradeStatistics.getDate())){
					need = false;
					break;
				}
			}
			if(need){
				TradeStatistics statis = new TradeStatistics();
				statis.setDate(date);
				statis.setAmount(new BigDecimal(0));
				statis.setProfit(new BigDecimal(0).setScale(2));
				statis.setCash(new BigDecimal(0).setScale(2));
				statis.setCount(0);
				info.add(statis);
			}
		}
        ListSort(info);
        dateFormat(info);
        return info;
    }
	private List<String> getBetweenDates(String start, String end) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		List<String> result = new ArrayList<String>();
		try {
		    Calendar tempStart = Calendar.getInstance();
		    tempStart.setTime(format.parse(start));
		    tempStart.add(Calendar.DAY_OF_YEAR, 1);
		    
		    Calendar tempEnd = Calendar.getInstance();
		    tempEnd.setTime(format.parse(end));
		    result.add(start);
		    while (tempStart.before(tempEnd)) {
		        result.add(format.format(tempStart.getTime()));
		        tempStart.add(Calendar.DAY_OF_YEAR, 1);
		    }
		    if(!start.equals(end)){
		    	result.add(end);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
    //排序
    private static void ListSort(List<TradeStatistics> list) {  
		Collections.sort(list, new Comparator<TradeStatistics>() {  
			@Override  
			public int compare(TradeStatistics o1, TradeStatistics o2) {  
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
				try {  
					Date dt1 = format.parse(o1.getDate());  
					Date dt2 = format.parse(o2.getDate());  
					if (dt1.getTime() > dt2.getTime()) {  
						return 1;  
					} else if (dt1.getTime() < dt2.getTime()) {  
						return -1;  
					} else {  
						return 0;  
					}  
				} catch (Exception e) {  
					e.printStackTrace();  
				}  
				return 0;  
			}  
		});  
	}
    
    public void dateFormat(List<TradeStatistics> info){
    	SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
    	for(TradeStatistics param : info){
    		try {
    			param.setDate(sdf.format(sdf.parse(param.getDate())));
			} catch (ParseException e) {
				e.printStackTrace();
			}
    	}
    }

}
