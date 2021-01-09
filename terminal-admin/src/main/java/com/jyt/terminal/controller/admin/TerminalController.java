package com.jyt.terminal.controller.admin;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.jyt.terminal.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.commom.LogObjectHolder;
import com.jyt.terminal.commom.PageFactory;
import com.jyt.terminal.commom.Permission;
import com.jyt.terminal.commom.enums.BitEnum.Currency;
import com.jyt.terminal.commom.enums.BitEnum.CurrencyPair;
import com.jyt.terminal.commom.enums.BitEnum.Exchange;
import com.jyt.terminal.commom.enums.BitEnum.ExchangeStrategy;
import com.jyt.terminal.commom.enums.BitEnum.HotWallet;
import com.jyt.terminal.commom.enums.BitEnum.IsSend;
import com.jyt.terminal.commom.enums.BitEnum.KycIsEnable;
import com.jyt.terminal.commom.enums.BitEnum.OrderType;
import com.jyt.terminal.commom.enums.BitEnum.RateSourceEnum;
import com.jyt.terminal.commom.enums.BitEnum.SellType;
import com.jyt.terminal.commom.enums.BitEnum.TerminalSettingStatus;
import com.jyt.terminal.commom.enums.BitEnum.VirtualCurrencyEnum;
import com.jyt.terminal.commom.enums.BitEnum.WayEnum;
import com.jyt.terminal.commom.enums.BitException;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.dto.QueryTermDTO;
import com.jyt.terminal.model.CryptoSettings;
import com.jyt.terminal.model.TerminalSetting;
import com.jyt.terminal.service.ICryptoSettingsService;
import com.jyt.terminal.service.ITerminalSettingService;
import com.jyt.terminal.warpper.TerminalWarpper;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Controller
@RequestMapping("/term")
public class TerminalController extends BaseController{
	
	private static String PREFIX = "/system/terminal/";
	private static final String defaultParam = "******";
	
	@Autowired
	public ITerminalSettingService terminalService;
	@Autowired
	public ICryptoSettingsService cryptoSettingsService;
	
	/**
     * 跳转到查看终端机账号列表
     */
    @RequestMapping("")
	@Permission
    public String index(Model model) {
    	//渠道类型枚举
    	List<Map<String,Object>> channelType = new ArrayList<Map<String,Object>>();
  		for(HotWallet param:HotWallet.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("message", param.getMessage());
  			channelType.add(value);
  		}
  		model.addAttribute("channelType",channelType);
  		//币种枚举
    	List<Map<String,Object>> currency = new ArrayList<Map<String,Object>>();
  		for(Currency param:Currency.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("message", param.getMessage());
  			currency.add(value);
  		}
  		model.addAttribute("currency",currency);
  	    //通道枚举
    	List<Map<String,Object>> wayEnum = new ArrayList<Map<String,Object>>();
  		for(WayEnum param:WayEnum.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getValue());
  			value.put("cndesc", param.getCndesc());
  			value.put("endesc", param.getEndesc());
  			wayEnum.add(value);
  		}
  		model.addAttribute("wayEnum",wayEnum);
        return PREFIX + "terminal.html";
    }
    
    /**
     * 查询用户列表
     */
    @ApiOperation(value="查询终端机账号列表",notes="分页查询终端机账号列表")
	@RequestMapping(value = "",method = RequestMethod.POST)
	@ResponseBody
	@Permission
	public Object list(@ApiParam(name="客户查询对象",required=true) HttpServletRequest request, QueryTermDTO queryTermDTO) {
		Map<String, String> respParam = Utils.getAllRequestParam(request);
		String currentType = respParam.get("currentType");
    	Page<Map<String, Object>> page = new PageFactory<Map<String, Object>>().defaultPage();
        List<Map<String, Object>> result = this.terminalService.getTermList(page, queryTermDTO);
        page.setRecords((List<Map<String, Object>>) new TerminalWarpper(result).warp(currentType));
        return super.packForBT(page);
    }
    /**
	 * 跳转到新增用户页面
	 */
	@RequestMapping(value = "/add",method = RequestMethod.GET)
	@Permission
	public String addView(HttpServletRequest request,Model model) {
		Map<String, String> respParam = Utils.getAllRequestParam(request);
		String currentType = respParam.get("currentType");
		List<CryptoSettings> cryptoSettingsList = cryptoSettingsService.selectList(new EntityWrapper<CryptoSettings>());
		model.addAttribute("cryptoSettingsList",cryptoSettingsList);
		//虚拟币种枚举
		List<Map<String,Object>> virtualCurrencyEnum = new ArrayList<Map<String,Object>>();
		for(VirtualCurrencyEnum param:VirtualCurrencyEnum.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getValue());
			value.put("message", param.getEndesc());
			virtualCurrencyEnum.add(value);
		}
		model.addAttribute("virtualCurrencyEnum",virtualCurrencyEnum);
		//热钱包枚举
		List<Map<String,Object>> hotWallet = new ArrayList<Map<String,Object>>();
		for(HotWallet param:HotWallet.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			value.put("message", param.getMessage());
			hotWallet.add(value);
		}
		model.addAttribute("hotWallet",hotWallet);
		//交易所枚举
		List<Map<String,Object>> exchange = new ArrayList<Map<String,Object>>();
		for(Exchange param:Exchange.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			value.put("message", param.getMessage());
			exchange.add(value);
		}
		model.addAttribute("exchange",exchange);
		//交易策略枚举
		List<Map<String,Object>> exchangeStrategy = new ArrayList<Map<String,Object>>();
		for(ExchangeStrategy param:ExchangeStrategy.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			if(currentType.equals("lanChinese")){
				value.put("message", param.getV1());
			}else{
				value.put("message", param.getV2());
			}
			exchangeStrategy.add(value);
		}
		model.addAttribute("exchangeStrategy",exchangeStrategy);
		//币种枚举
		List<Map<String,Object>> currency = new ArrayList<Map<String,Object>>();
		for(Currency param:Currency.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			value.put("message", param.getMessage());
			currency.add(value);
		}
		model.addAttribute("currency",currency);
		//赎回方式枚举
		List<Map<String,Object>> sellType = new ArrayList<Map<String,Object>>();
		for(SellType param:SellType.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			value.put("message", param.getMessage());
			sellType.add(value);
		}
		model.addAttribute("sellType",sellType);
		//订单类型枚举
		List<Map<String,Object>> orderType = new ArrayList<Map<String,Object>>();
		for(OrderType param:OrderType.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			value.put("message", param.getMessage());
			orderType.add(value);
		}
		model.addAttribute("orderType",orderType);
		//交易对枚举
		List<Map<String,Object>> currencyPair = new ArrayList<Map<String,Object>>();
		for(CurrencyPair param:CurrencyPair.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			value.put("message", param.getMessage());
			currencyPair.add(value);
		}
		model.addAttribute("currencyPair",currencyPair);
		//币汇率来源枚举
		List<Map<String,Object>> rateSourceEnum = new ArrayList<Map<String,Object>>();
		for(RateSourceEnum param: RateSourceEnum.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getValue());
			value.put("message", param.getDesc());
			rateSourceEnum.add(value);
		}
		model.addAttribute("rateSourceEnum",rateSourceEnum);
		//通道枚举
    	List<Map<String,Object>> wayEnum = new ArrayList<Map<String,Object>>();
  		for(WayEnum param:WayEnum.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("value", param.getValue());
  			value.put("cndesc", param.getCndesc());
  			value.put("endesc", param.getEndesc());
  			wayEnum.add(value);
  		}
  		model.addAttribute("wayEnum",wayEnum);
		return PREFIX + "term_add.html";
	}
	
	/**
	 * 提交新增用户
	 */
	@RequestMapping(value = "/add",method = RequestMethod.POST)
	@Permission
	@ResponseBody
	public Tip add(@Valid TerminalSetting terminal) {
		
		//String channelParam = terminal.getChannelParam();
		//String jsonStr = "";
		/*
		 * try { //json中热钱包和交易所信息加密 jsonStr =
		 * TermJsonEncrypt.channelParamEncrypt(channelParam,terminal.getHotWallet(),
		 * terminal.getExchange(),null); } catch (Exception e) { throw new
		 * BitException(BizExceptionEnum.ENCRYPTION_FAILED); }
		 */
		try {
	    	//terminal.setChannelParam(jsonStr);
	    	terminal.setCreateTime(new Date());
	    	terminal.setIsSend(IsSend.NO.getCode());
	    	terminal.setSalt(ShiroKit.getRandomSalt(5));
	    	terminal.setPassword(ShiroKit.md5(terminal.getPassword(), terminal.getSalt()));
	    	terminal.setStatus(TerminalSettingStatus.ENABLE.getCode());
			boolean flag = terminalService.insert(terminal); 
			if(flag){ 
				DecimalFormat df=new DecimalFormat("00000000");
				String terminalNo="t"+df.format(terminal.getId());
				terminal.setTerminalNo(terminalNo); 
				terminalService.updateById(terminal); 
			}
			return SUCCESS_TIP;
		} catch (Exception e) {
			throw new BitException(BizExceptionEnum.SERVER_ERROR);
		}
		
	}
	
	/**
	 * 跳转到详情页面
	 */
	@RequestMapping("/detail/{userId}/{currentType}")
	@Permission
	public String termDetail(@PathVariable Integer userId,@PathVariable String currentType, Model model) {
		if (ToolUtil.isEmpty(userId)) {
			throw new BitException(BizExceptionEnum.REQUEST_NULL);
		}
		TerminalSetting term = this.terminalService.selectById(userId);
		model.addAttribute("term",term);
		List<CryptoSettings> cryptoSettingsList = cryptoSettingsService.selectList(new EntityWrapper<CryptoSettings>());
		model.addAttribute("cryptoSettingsList",cryptoSettingsList);
		int[] cryptoSetList = null;
		if(ToolUtil.isNotEmpty(term.getCryptoSettings())) {
			String[] strList = term.getCryptoSettings().split(",");
			cryptoSetList = new int[strList.length];
			for(int i =0;i<strList.length;i++) {
				cryptoSetList[i] = Integer.parseInt(strList[i]);
			}
		}
		model.addAttribute("cryptoSetList",cryptoSetList);
		//虚拟币种枚举
		List<Map<String,Object>> virtualCurrencyEnum = new ArrayList<Map<String,Object>>();
		for(VirtualCurrencyEnum param:VirtualCurrencyEnum.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getValue());
			value.put("message", param.getEndesc());
			virtualCurrencyEnum.add(value);
		}
		model.addAttribute("virtualCurrencyEnum",virtualCurrencyEnum);
		//交易所
		List<Map<String,Object>> exchange = new ArrayList<Map<String,Object>>();
		for(Exchange param:Exchange.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			value.put("message", param.getMessage());
			exchange.add(value);
		}
		model.addAttribute("exchange",exchange);
		//热钱包
		List<Map<String,Object>> hotWallet = new ArrayList<Map<String,Object>>();
		for(HotWallet param:HotWallet.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			value.put("message", param.getMessage());
			hotWallet.add(value);
		}
		model.addAttribute("hotWallet",hotWallet);
		//赎回方式枚举
		List<Map<String,Object>> sellType = new ArrayList<Map<String,Object>>();
		for(SellType param:SellType.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			value.put("message", param.getMessage());
			sellType.add(value);
		}
		model.addAttribute("sellType",sellType);
		//交易策略枚举
		List<Map<String,Object>> exchangeStrategy = new ArrayList<Map<String,Object>>();
		for(ExchangeStrategy param:ExchangeStrategy.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			if(currentType.equals("lanChinese")){
				value.put("message", param.getV1());
			}else{
				value.put("message", param.getV2());
			}
			exchangeStrategy.add(value);
		}
		model.addAttribute("exchangeStrategy",exchangeStrategy);
		//通道枚举
    	List<Map<String,Object>> wayEnum = new ArrayList<Map<String,Object>>();
  		for(WayEnum param:WayEnum.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("value", param.getValue());
  			value.put("cndesc", param.getCndesc());
  			value.put("endesc", param.getEndesc());
  			wayEnum.add(value);
  		}
  		model.addAttribute("wayEnum",wayEnum);
		//热钱包
		//bitgo
		model.addAttribute("walletId",defaultParam);
		model.addAttribute("walletPassphrase",defaultParam);
		//coinbase
		model.addAttribute("accessToken",defaultParam);
		model.addAttribute("apiKey",defaultParam);
		model.addAttribute("apiSecret",defaultParam);
		//blockchain
		model.addAttribute("blockchainWalletId",defaultParam);
		model.addAttribute("blockchainPassword",defaultParam);
		//交易所
		//pro
		model.addAttribute("proKey",defaultParam);
		model.addAttribute("proSecret",defaultParam);
		model.addAttribute("proPassphrase",defaultParam);
		//kraken
		model.addAttribute("krakenApiKey",defaultParam);
		model.addAttribute("krakenSecret",defaultParam);
		model.addAttribute("krakenWithdrawalsAddressName",defaultParam);
		
		/*
		 * String channelParam = term.getChannelParam(); JSONObject obj =
		 * JSON.parseObject(channelParam); JSONObject obj_exchange =
		 * JSON.parseObject(obj.get("exchange").toString());
		 * 
		 * String orderType = null; String currencyPair = null; if(term.getExchange() !=
		 * 0){ if(ToolUtil.isNotEmpty(obj_exchange.get("order_type"))){ orderType =
		 * OrderType.valueOf(Integer.valueOf((String) obj_exchange.get("order_type")));
		 * } if(ToolUtil.isNotEmpty(obj_exchange.get("currency_pair"))){ currencyPair =
		 * CurrencyPair.valueOf(Integer.valueOf((String)
		 * obj_exchange.get("currency_pair"))); } }
		 */
		
		model.addAttribute("orderType",1);
		model.addAttribute("currencyPair",2);
		LogObjectHolder.me().set(term);
		return PREFIX + "term_detail.html";
	}
	
	/**
	 * 跳转到修改页面
	 */
	@RequestMapping(value = "/edit/{userId}/{currentType}",method = RequestMethod.GET)
	@Permission
	public String toEditView(@PathVariable Integer userId,@PathVariable String currentType, Model model) {
		if (ToolUtil.isEmpty(userId)) {
			throw new BitException(BizExceptionEnum.REQUEST_NULL);
		}
		TerminalSetting term = this.terminalService.selectById(userId);
		model.addAttribute("term",term);
		List<CryptoSettings> cryptoSettingsList = cryptoSettingsService.selectList(new EntityWrapper<CryptoSettings>());
		model.addAttribute("cryptoSettingsList",cryptoSettingsList);
		int[] cryptoSetList = null;
		if(ToolUtil.isNotEmpty(term.getCryptoSettings())) {
			String[] strList = term.getCryptoSettings().split(",");
			cryptoSetList = new int[strList.length];
			for(int i =0;i<strList.length;i++) {
				cryptoSetList[i] = Integer.parseInt(strList[i]);
			}
		}
		model.addAttribute("cryptoSetList",cryptoSetList);
		//虚拟币种枚举
		List<Map<String,Object>> virtualCurrencyEnum = new ArrayList<Map<String,Object>>();
		for(VirtualCurrencyEnum param:VirtualCurrencyEnum.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getValue());
			value.put("message", param.getEndesc());
			virtualCurrencyEnum.add(value);
		}
		model.addAttribute("virtualCurrencyEnum",virtualCurrencyEnum);
		//热钱包枚举
		List<Map<String,Object>> hotWallet = new ArrayList<Map<String,Object>>();
		for(HotWallet param:HotWallet.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			value.put("message", param.getMessage());
			hotWallet.add(value);
		}
		model.addAttribute("hotWallet",hotWallet);
		//交易所枚举
		List<Map<String,Object>> exchange = new ArrayList<Map<String,Object>>();
		for(Exchange param:Exchange.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			value.put("message", param.getMessage());
			exchange.add(value);
		}
		model.addAttribute("exchange",exchange);
		//交易策略枚举
		List<Map<String,Object>> exchangeStrategy = new ArrayList<Map<String,Object>>();
		for(ExchangeStrategy param:ExchangeStrategy.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			if(currentType.equals("lanChinese")){
				value.put("message", param.getV1());
			}else{
				value.put("message", param.getV2());
			}
			exchangeStrategy.add(value);
		}
		model.addAttribute("exchangeStrategy",exchangeStrategy);
		//币种枚举
		List<Map<String,Object>> currency = new ArrayList<Map<String,Object>>();
		for(Currency param:Currency.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			value.put("message", param.getMessage());
			currency.add(value);
		}
		model.addAttribute("currency",currency);
		//赎回方式枚举
		List<Map<String,Object>> sellType = new ArrayList<Map<String,Object>>();
		for(SellType param:SellType.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			value.put("message", param.getMessage());
			sellType.add(value);
		}
		model.addAttribute("sellType",sellType);
		//订单类型枚举
		List<Map<String,Object>> orderType = new ArrayList<Map<String,Object>>();
		for(OrderType param:OrderType.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			value.put("message", param.getMessage());
			orderType.add(value);
		}
		model.addAttribute("orderTypeEnum",orderType);
		//交易对枚举
		List<Map<String,Object>> currencyPair = new ArrayList<Map<String,Object>>();
		for(CurrencyPair param:CurrencyPair.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getCode());
			value.put("message", param.getMessage());
			currencyPair.add(value);
		}
		model.addAttribute("currencyPairEnum",currencyPair);
		//币汇率来源枚举
		List<Map<String,Object>> rateSourceEnum = new ArrayList<Map<String,Object>>();
		for(RateSourceEnum param: RateSourceEnum.values()){
			Map<String, Object> value = new HashMap<String,Object>();
			value.put("code", param.getValue());
			value.put("message", param.getDesc());
			rateSourceEnum.add(value);
		}
		model.addAttribute("rateSourceEnum",rateSourceEnum);
		//通道枚举
    	List<Map<String,Object>> wayEnum = new ArrayList<Map<String,Object>>();
  		for(WayEnum param:WayEnum.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("value", param.getValue());
  			value.put("cndesc", param.getCndesc());
  			value.put("endesc", param.getEndesc());
  			wayEnum.add(value);
  		}
  		model.addAttribute("wayEnum",wayEnum);
		//热钱包
		//bitgo
		model.addAttribute("walletId",defaultParam);
		model.addAttribute("walletPassphrase",defaultParam);
		//coinbase
		model.addAttribute("accessToken",defaultParam);
		model.addAttribute("apiKey",defaultParam);
		model.addAttribute("apiSecret",defaultParam);
		//blockchain
		model.addAttribute("blockchainWalletId",defaultParam);
		model.addAttribute("blockchainPassword",defaultParam);
		//交易所
		model.addAttribute("proKey",defaultParam);
		model.addAttribute("proSecret",defaultParam);
		model.addAttribute("proPassphrase",defaultParam);
		model.addAttribute("krakenApiKey",defaultParam);
		model.addAttribute("krakenSecret",defaultParam);
		model.addAttribute("krakenWithdrawalsAddressName",defaultParam);
		/*
		 * String channelParam = term.getChannelParam(); JSONObject obj =
		 * JSON.parseObject(channelParam); JSONObject obj_exchange =
		 * JSON.parseObject(obj.get("exchange").toString()); Integer oType = null;
		 * Integer cPair = null; if(term.getExchange() != 0){
		 * if(ToolUtil.isNotEmpty(obj_exchange.get("order_type"))){ oType =
		 * Integer.valueOf((String) obj_exchange.get("order_type")); }
		 * if(ToolUtil.isNotEmpty(obj_exchange.get("currency_pair"))){ cPair =
		 * Integer.valueOf((String) obj_exchange.get("currency_pair")); } }
		 */
		model.addAttribute("orderType",2);
		model.addAttribute("currencyPair",1);
		LogObjectHolder.me().set(term);
		return PREFIX + "term_edit.html";
	}
	
	/**
	 * 修改用户信息
	 * @throws Exception 
	 */
	@RequestMapping(value = "/edit",method = RequestMethod.POST)
	@Permission
	@ResponseBody
	public Tip edit(@Valid QueryTermDTO term, BindingResult result) throws Exception {
		if (result.hasErrors()) {
			throw new BitException(BizExceptionEnum.REQUEST_NULL);
		}
		if(ToolUtil.isEmpty(term.getCryptoSettings())) {
			ErrorTip tip = new ErrorTip(500, "CryptoSettings cannot be empty");
			return tip;
		}
		//String channelParam = term.getChannelParam();
		TerminalSetting termSetting = terminalService.selectById(term.getId());
		/*
		 * try { //json中热钱包和交易所信息加密 channelParam =
		 * TermJsonEncrypt.channelParamEncrypt(channelParam,term.getHotWallet(),term.
		 * getExchange(),termSetting); } catch (Exception e) { throw new
		 * BitException(BizExceptionEnum.ENCRYPTION_FAILED); }
		 */
		try {
			termSetting.setCryptoSettings(term.getCryptoSettings());
			termSetting.setMerchantName(term.getMerchantName());
			termSetting.setEmail(term.getEmail());
			termSetting.setHotline(term.getHotline());
			termSetting.setBuyTransactionFee(term.getBuyTransactionFee());
			termSetting.setSellTransactionFee(term.getSellTransactionFee());
			//termSetting.setChannelParam(channelParam);
			termSetting.setBuySingleFee(term.getBuySingleFee());
			termSetting.setSellSingleFee(term.getSellSingleFee());
			termSetting.setRate(term.getRate());
			termSetting.setExchangeStrategy(term.getExchangeStrategy());
			termSetting.setMinNeedBitcoin(term.getMinNeedBitcoin());
			termSetting.setMinNeedCash(term.getMinNeedCash());
			termSetting.setIsSend(IsSend.NO.getCode());
			termSetting.setHotWallet(term.getHotWallet());
			termSetting.setCurrency(term.getCurrency());
			termSetting.setExchange(term.getExchange());
			termSetting.setSellType(term.getSellType());
			termSetting.setRateSource(term.getRateSource());
			termSetting.setKycEnable(term.getKycEnable());
			if(term.getKycEnable() == KycIsEnable.FALSE.getValue()) {
				termSetting.setLimitCash("");
				termSetting.setKycUrl("");
			}else {
				termSetting.setLimitCash(term.getLimitCash());
				termSetting.setKycUrl(term.getKycUrl());
			}
			termSetting.setWay(term.getWay());
			termSetting.setUpdateTime(new Date());
			this.terminalService.updateById(termSetting);
		} catch (Exception e) {
			throw new BitException(BizExceptionEnum.SERVER_ERROR);
		}
		return SUCCESS_TIP;
	}
	
	
	/**
	 * 跳转到修改密码页面
	 */
	@RequestMapping(value = "/changePwd/{id}",method = RequestMethod.GET)
	@Permission
	public String toChangePwd(@PathVariable Integer id, Model model) {
		if (ToolUtil.isEmpty(id)) {
			throw new BitException(BizExceptionEnum.REQUEST_NULL);
		}
		TerminalSetting term = this.terminalService.selectById(id);
		model.addAttribute("term",term);
		LogObjectHolder.me().set(term);
		return PREFIX + "term_changepwd.html";
	}

	
	/**
	 * 修改密码
	 */
	@RequestMapping(value = "/changePwd",method = RequestMethod.POST)
	@Permission
	@ResponseBody
	public Tip changePwd(@Valid String oldPwd, String password,int id)  {
		
		TerminalSetting termSetting = terminalService.selectById(id);
		if(!termSetting.getPassword().equals(ShiroKit.md5(oldPwd, termSetting.getSalt()))){
			ErrorTip tip = new ErrorTip(500, "wrong password");
			return tip;
		}
		termSetting.setPassword(ShiroKit.md5(password, termSetting.getSalt()));
		this.terminalService.updateById(termSetting);
		return SUCCESS_TIP;
	}



	/**
	 * 删除用户信息（逻辑删除）
	 */
	@RequestMapping("/delete")
	@Permission
	@ResponseBody
	public Tip delete(@RequestParam Integer userId) {
		if (ToolUtil.isEmpty(userId)) {
			throw new BitException(BizExceptionEnum.REQUEST_NULL);
		}
		this.terminalService.setStatus(userId, 3);
		return SUCCESS_TIP;
	}

	/**
	 * 重置登录密码
	 */
	@RequestMapping("/resetPwd")
	@Permission
	@ResponseBody
	public Tip reset(@RequestParam Integer userId) {
		TerminalSetting oldTerminal = this.terminalService.selectById(userId);
		String password = "123456";
		logger.info("重置密码：password（{}） ", password);
		oldTerminal.setPassword(ShiroKit.md5(password, oldTerminal.getSalt()));
		oldTerminal.setUpdateTime(new Date());
		this.terminalService.updateById(oldTerminal);
		return SUCCESS_TIP;
	}
}
