package com.jyt.terminal.controller.admin;

import java.math.BigDecimal;
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
import com.jyt.terminal.commom.enums.BitEnum.RedeemStatus;
import com.jyt.terminal.commom.enums.BitEnum.SellType;
import com.jyt.terminal.commom.enums.BitEnum.TradeStatus;
import com.jyt.terminal.commom.enums.BitEnum.VirtualCurrencyEnum;
import com.jyt.terminal.commom.enums.BitException;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.dao.WithdrawMapper;
import com.jyt.terminal.dto.SellDTO;
import com.jyt.terminal.model.Withdraw;
import com.jyt.terminal.service.IWithdrawService;
import com.jyt.terminal.util.ArithUtil;
import com.jyt.terminal.util.ToolUtil;
import com.jyt.terminal.warpper.SellWarpper;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/sell")
public class QuerySellListController extends BaseController{

private static String PREFIX = "/system/trade/";
	
	@Autowired
	public IWithdrawService withdrawService;
	@Resource
	public WithdrawMapper withdrawMapper;
	/**
     * 跳转到比特币取现页面
     */
    @RequestMapping(value = "",method = RequestMethod.GET)
	@Permission
	public String index(Model model) {
    	//交易状态枚举
    	List<Map<String,Object>> tradeStatus = new ArrayList<Map<String,Object>>();
  		for(TradeStatus param:TradeStatus.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("v1", param.getV1());
  			value.put("v2", param.getV2());
  			tradeStatus.add(value);
  		}
  		model.addAttribute("tradeStatus",tradeStatus);
  		//出钞状态枚举
    	List<Map<String,Object>> redeemStatus = new ArrayList<Map<String,Object>>();
  		for(RedeemStatus param:RedeemStatus.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("v1", param.getV1());
  			value.put("v2", param.getV2());
  			redeemStatus.add(value);
  		}
  		model.addAttribute("redeemStatus",redeemStatus);
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
        return PREFIX + "sellList.html";
    }
    
    /**
     * 查询购买流水列表
     */
    @ApiOperation(value="查询取现流水列表",notes="分页查询取现流水列表")
    @RequestMapping(value = "",method = RequestMethod.POST)
	@Permission
	@ResponseBody
    public Object list(@ApiParam(name="查询购买流水列表",required=true) HttpServletRequest request, SellDTO sellDTO) {
		Map<String, String> respParam = Utils.getAllRequestParam(request);
		String currentType = respParam.get("currentType");
    	Page<Withdraw> page = new PageFactory<Withdraw>().defaultPage();
        List<Map<String,Object>> result = this.withdrawService.getWithdrawList(page, sellDTO);
        page.setRecords((List<Withdraw>) new SellWarpper(result).warp(currentType));
        return super.packForBT(page);
    }
	
    /**
	 * 跳转到提现记录详情页面
	 */
	@RequestMapping("/detail/{id}/{currentType}")
	@Permission
	public String sellFlowDetail(@PathVariable Integer id,@PathVariable String currentType, Model model) {
		if (ToolUtil.isEmpty(id)) {
			throw new BitException(BizExceptionEnum.REQUEST_NULL);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String,Object> sell = this.withdrawService.getWithdrawDetail(id);
		String lastTime = "";
		if(ToolUtil.isNotEmpty(sell.get("update_time"))){
			lastTime = sdf.format(sell.get("update_time"));
		}
		sell.put("update_time", lastTime);
		sell.put("cash", new BigDecimal((String)sell.get("cash")).setScale(2).toString());
		if(ToolUtil.isEmpty(sell.get("strategy"))){
			sell.put("strategy", Exchange.valueOf(Exchange.NO.getCode()));
		}else{
			sell.put("strategy", Exchange.valueOf(Integer.valueOf((String)sell.get("strategy"))));
		}
		sell.put("trans_status", TradeStatus.getName(currentType, (Integer)sell.get("trans_status")));
		sell.put("redeem_status", RedeemStatus.getName(currentType, (Integer)sell.get("redeem_status")));
		sell.put("sellType", SellType.valueOf((Integer)sell.get("sell_type")));
		sell.put("exchangeStrategy", ExchangeStrategy.getName(currentType,(Integer)sell.get("exchange_strategy")));
		
		//手续费计算（法币）
		double fee = Double.parseDouble((String) sell.get("fee"));
		double price = Double.parseDouble((String) sell.get("price"));
		try {
			fee = ArithUtil.mul(ArithUtil.div(fee, Math.pow(10,8)), price);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		sell.put("fee", ArithUtil.fixedNumber(fee)+sell.get("currency").toString());
		sell.put("price", sell.get("price").toString()+sell.get("currency").toString());
		sell.put("cash", sell.get("cash").toString()+sell.get("currency").toString());
		Double coin = Double.valueOf((String)sell.get("amount"))/Math.pow(10,8);
		Double c_fee = Double.valueOf((String)sell.get("c_fee"))/Math.pow(10,8);
		if(VirtualCurrencyEnum.BTC.getEndesc().equals(sell.get("crypto_currency"))) {
			sell.put("coin", ArithUtil.doubleToStr(coin)+"BTC");
			sell.put("c_fee", ArithUtil.doubleToStr(c_fee)+"BTC");
		}else if(VirtualCurrencyEnum.ETH.getEndesc().equals(sell.get("crypto_currency"))) {
			sell.put("coin", ArithUtil.doubleToStr(coin)+"ETH");
			sell.put("c_fee", ArithUtil.doubleToStr(c_fee)+"ETH");
		}
		model.addAttribute("sell",sell);
		//热钱包
		List<Map<String,Object>> hotWallet = new ArrayList<Map<String,Object>>();
		for(HotWallet param:HotWallet.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			value.put("message", param.getMessage());
			hotWallet.add(value);
		}
		model.addAttribute("hotWallet",hotWallet);

		return PREFIX + "sellFlow_detail.html";
	}
	
}
