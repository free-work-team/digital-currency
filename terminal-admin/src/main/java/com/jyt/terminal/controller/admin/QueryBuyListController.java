package com.jyt.terminal.controller.admin;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.jyt.terminal.commom.enums.BitEnum.Exchange;
import com.jyt.terminal.commom.enums.BitEnum.ExchangeStrategy;
import com.jyt.terminal.commom.enums.BitEnum.HotWallet;
import com.jyt.terminal.commom.enums.BitEnum.TradeStatus;
import com.jyt.terminal.commom.enums.BitEnum.VirtualCurrencyEnum;
import com.jyt.terminal.commom.enums.BitException;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.dao.BuyMapper;
import com.jyt.terminal.dto.BuyDTO;
import com.jyt.terminal.model.Buy;
import com.jyt.terminal.service.IBuyService;
import com.jyt.terminal.util.ArithUtil;
import com.jyt.terminal.util.ToolUtil;
import com.jyt.terminal.warpper.BuyWarpper;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/buy")
public class QueryBuyListController extends BaseController{

private static String PREFIX = "/system/trade/";
	
	@Autowired
	public IBuyService buyService;
	@Resource
	public BuyMapper buyMapper;
	/**
     * 跳转到比特币购买页面
     */
    @RequestMapping(value = "",method = RequestMethod.GET)
	@Permission
	public String index(Model model) {
    	//买入状态枚举
    	List<Map<String,Object>> status = new ArrayList<Map<String,Object>>();
  		for(TradeStatus param:TradeStatus.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("v1", param.getV1());
  			value.put("v2", param.getV2());
  			status.add(value);
  		}
  		model.addAttribute("status",status);
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
        return PREFIX + "buyList.html";
    }
    
    /**
     * 查询购买流水列表
     */
    @ApiOperation(value="查询购买流水列表",notes="分页查询购买流水列表")
    @RequestMapping(value = "",method = RequestMethod.POST)
	@Permission
	@ResponseBody
    public Object list(@ApiParam(name="查询购买流水列表",required=true) HttpServletRequest request, BuyDTO buyDTO) {
		Map<String, String> respParam = Utils.getAllRequestParam(request);
		String currentType = respParam.get("currentType");
        Page<Buy> page = new PageFactory<Buy>().defaultPage();
        List<Map<String,Object>> result = this.buyService.getBuyList(page, buyDTO);
        page.setRecords((List<Buy>) new BuyWarpper(result).warp(currentType));
        return super.packForBT(page);
    }
    
    /**
	 * 跳转到购买记录详情页面
	 */
	@RequestMapping("/detail/{id}/{currentType}")
	@Permission
	public String buyFlowDetail(@PathVariable Integer id,@PathVariable String currentType, Model model) {
		if (ToolUtil.isEmpty(id)) {
			throw new BitException(BizExceptionEnum.REQUEST_NULL);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String,Object> buy = this.buyService.getBuyDetail(id);
		String lastTime = "";
		if(ToolUtil.isNotEmpty(buy.get("update_time"))){
			lastTime = sdf.format(buy.get("update_time"));
		}
		buy.put("update_time", lastTime);
		buy.put("cash", new BigDecimal((String)buy.get("cash")).setScale(2).toString());
		if(ToolUtil.isEmpty(buy.get("strategy"))){
			buy.put("strategy", Exchange.valueOf(Exchange.NO.getCode()));
		}else{
			buy.put("strategy", Exchange.valueOf(Integer.valueOf((String)buy.get("strategy"))));
		}
		buy.put("exchangeStrategy", ExchangeStrategy.getName(currentType,(Integer)buy.get("exchange_strategy")));
		if (ToolUtil.isNum(buy.get("status"))){
			buy.put("status", TradeStatus.getName(currentType,Integer.parseInt((String) buy.get("status"))));
		}else{
			buy.put("status",buy.get("status"));
		}
		
		//手续费计算（法币）
		double fee = Double.parseDouble((String) buy.get("fee"));
		double price = Double.parseDouble((String) buy.get("price"));
		try {
			fee = ArithUtil.mul(ArithUtil.div(fee, Math.pow(10,8)), price);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		buy.put("fee", ArithUtil.fixedNumber(fee)+buy.get("currency").toString());
		buy.put("price", ArithUtil.fixedNumber(price)+buy.get("currency").toString());
		buy.put("cash", buy.get("cash").toString()+buy.get("currency").toString());
		Double coin = Double.valueOf((String)buy.get("amount"))/Math.pow(10,8);
		Double channel_fee = Double.valueOf((String)buy.get("channel_fee"))/Math.pow(10,8);
		if(VirtualCurrencyEnum.BTC.getEndesc().equals(buy.get("crypto_currency"))) {
			buy.put("coin", ArithUtil.doubleToStr(coin)+"BTC");
			buy.put("channel_fee", ArithUtil.doubleToStr(channel_fee)+"BTC");
		}else if(VirtualCurrencyEnum.ETH.getEndesc().equals(buy.get("crypto_currency"))) {
			buy.put("coin", ArithUtil.doubleToStr(coin)+"ETH");
			buy.put("channel_fee", ArithUtil.doubleToStr(channel_fee)+"ETH");
		}
		model.addAttribute("buy",buy);
		//热钱包
		List<Map<String,Object>> hotWallet = new ArrayList<Map<String,Object>>();
		for(HotWallet param:HotWallet.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			value.put("message", param.getMessage());
			hotWallet.add(value);
		}
		model.addAttribute("hotWallet",hotWallet);
		return PREFIX + "buyFlow_detail.html";
	}
	
}
