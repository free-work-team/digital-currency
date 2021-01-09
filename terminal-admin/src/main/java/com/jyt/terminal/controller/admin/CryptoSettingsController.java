package com.jyt.terminal.controller.admin;

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
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.commom.PageFactory;
import com.jyt.terminal.commom.Permission;
import com.jyt.terminal.commom.enums.BitEnum.CryptoSettingsStatusEnum;
import com.jyt.terminal.commom.enums.BitEnum.CurrencyPair;
import com.jyt.terminal.commom.enums.BitEnum.Exchange;
import com.jyt.terminal.commom.enums.BitEnum.ExchangeStrategy;
import com.jyt.terminal.commom.enums.BitEnum.HotWallet;
import com.jyt.terminal.commom.enums.BitEnum.IsSend;
import com.jyt.terminal.commom.enums.BitEnum.OrderType;
import com.jyt.terminal.commom.enums.BitEnum.RateSourceEnum;
import com.jyt.terminal.commom.enums.BitEnum.SellType;
import com.jyt.terminal.commom.enums.BitEnum.VirtualCurrencyEnum;
import com.jyt.terminal.commom.enums.BitException;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.dto.CryptoSettingsDTO;
import com.jyt.terminal.model.CryptoSettings;
import com.jyt.terminal.model.TerminalSetting;
import com.jyt.terminal.service.ICryptoSettingsService;
import com.jyt.terminal.service.ITerminalSettingService;
import com.jyt.terminal.warpper.CryptoSettingsWarpper;

import io.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/cryptoSettings")
public class CryptoSettingsController extends BaseController{
	private static String PREFIX = "/system/cryptoSettings/";
	private static final String defaultParam = "******";
	
	@Autowired
	public ICryptoSettingsService cryptoSettingsService;
	@Autowired
	public ITerminalSettingService terminalSettingService;
	
	/**
     * 跳转到查看用户列表的页面
     */
    @RequestMapping(value = "",method = RequestMethod.GET)
	@Permission
    public String index(Model model) {
    	//加密设置状态
    	List<Map<String,Object>> cryptoSettingsStatusEnum = new ArrayList<Map<String,Object>>();
  		for(CryptoSettingsStatusEnum param:CryptoSettingsStatusEnum.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getValue());
  			value.put("cndesc", param.getCndesc());
  			value.put("endesc", param.getEndesc());
  			cryptoSettingsStatusEnum.add(value);
  		}
  		model.addAttribute("cryptoSettingsStatusEnum",cryptoSettingsStatusEnum);
  		//虚拟币种
    	List<Map<String,Object>> virtualCurrencyEnum = new ArrayList<Map<String,Object>>();
  		for(VirtualCurrencyEnum param:VirtualCurrencyEnum.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getValue());
  			value.put("cndesc", param.getCndesc());
  			value.put("endesc", param.getEndesc());
  			virtualCurrencyEnum.add(value);
  		}
  		model.addAttribute("virtualCurrencyEnum",virtualCurrencyEnum);
        return PREFIX + "cryptoSettings.html";
    }
    /**
     * 分页查询列表
     */
    @RequestMapping(value = "",method = RequestMethod.POST)
	@Permission
    @ResponseBody
    public Object list(@ApiParam(name="客户查询对象",required=true) HttpServletRequest request, CryptoSettingsDTO cryptoSettingsDTO) {
		Map<String, String> respParam = Utils.getAllRequestParam(request);
		String currentType = respParam.get("currentType");
    	Page<Map<String, Object>> page = new PageFactory<Map<String, Object>>().defaultPage();
        List<Map<String, Object>> result = this.cryptoSettingsService.getCryptoSettingsList(page, cryptoSettingsDTO);
        page.setRecords((List<Map<String, Object>>) new CryptoSettingsWarpper(result).warp(currentType));
        return super.packForBT(page);
    }
    
    /**
	 * 跳转新增页面
	 */
	@RequestMapping(value = "/add",method = RequestMethod.GET)
	@Permission
	public String openAddView(Model model) {
		//虚拟币
    	List<Map<String,Object>> virtualCurrencyEnum = new ArrayList<Map<String,Object>>();
  		for(VirtualCurrencyEnum param:VirtualCurrencyEnum.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getValue());
  			value.put("cndesc", param.getCndesc());
  			value.put("endesc", param.getEndesc());
  			virtualCurrencyEnum.add(value);
  		}
  		model.addAttribute("virtualCurrencyEnum",virtualCurrencyEnum);
  		//交易策略
    	List<Map<String,Object>> exchangeStrategyEnum = new ArrayList<Map<String,Object>>();
  		for(ExchangeStrategy param:ExchangeStrategy.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("cndesc", param.getV1());
  			value.put("endesc", param.getV2());
  			exchangeStrategyEnum.add(value);
  		}
  		model.addAttribute("exchangeStrategyEnum",exchangeStrategyEnum);
  	    //交易所
    	List<Map<String,Object>> exchangeEnum = new ArrayList<Map<String,Object>>();
  		for(Exchange param:Exchange.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("message", param.getMessage());
  			exchangeEnum.add(value);
  		}
  		model.addAttribute("exchangeEnum",exchangeEnum);
  		//钱包
    	List<Map<String,Object>> hotWalletEnum = new ArrayList<Map<String,Object>>();
  		for(HotWallet param:HotWallet.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("message", param.getMessage());
  			hotWalletEnum.add(value);
  		}
  		model.addAttribute("hotWalletEnum",hotWalletEnum);
  		//钱包
    	List<Map<String,Object>> rateSourceEnum = new ArrayList<Map<String,Object>>();
  		for(RateSourceEnum param:RateSourceEnum.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getValue());
  			value.put("message", param.getDesc());
  			rateSourceEnum.add(value);
  		}
  		model.addAttribute("rateSourceEnum",rateSourceEnum);
  		//订单类型
    	List<Map<String,Object>> orderTypeEnum = new ArrayList<Map<String,Object>>();
  		for(OrderType param:OrderType.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("message", param.getMessage());
  			orderTypeEnum.add(value);
  		}
  		model.addAttribute("orderTypeEnum",orderTypeEnum);
  		//交易对
    	List<Map<String,Object>> currencyPairEnum = new ArrayList<Map<String,Object>>();
  		for(CurrencyPair param:CurrencyPair.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("message", param.getMessage());
  			currencyPairEnum.add(value);
  		}
  		model.addAttribute("currencyPairEnum",currencyPairEnum);
  		//交易对
    	List<Map<String,Object>> confirmationsEnum = new ArrayList<Map<String,Object>>();
  		for(SellType param:SellType.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("message", param.getMessage());
  			confirmationsEnum.add(value);
  		}
  		model.addAttribute("confirmationsEnum",confirmationsEnum);
		return PREFIX + "cryptoSettings_add.html";
	}
	
	@RequestMapping(value = "/add",method = RequestMethod.POST)
	@ResponseBody
	@Permission
	public Tip add(@Valid CryptoSettings cryptoSettings){
		CryptoSettings entity = cryptoSettingsService.selectOne(new EntityWrapper<CryptoSettings>().eq("name", cryptoSettings.getName()));
		if(ToolUtil.isNotEmpty(entity)){
			ErrorTip tip = new ErrorTip(500, "Name already exists");
			return tip;
		}
		cryptoSettings.setChannelParam(EncryptionUtils.encrypt(cryptoSettings.getChannelParam()));
		cryptoSettings.setStatus(CryptoSettingsStatusEnum.ENABLE.getValue());
		cryptoSettings.setCreateUser(ShiroKit.getUser().getAccount());
		cryptoSettings.setCreateTime(new Date());
		cryptoSettingsService.insert(cryptoSettings);
		return SUCCESS_TIP;
	}
	
	@RequestMapping(value = "/edit/{id}",method = RequestMethod.GET)
	@Permission
	public String openEditView(@PathVariable Integer id,Model model){
		CryptoSettings entity = cryptoSettingsService.selectById(id);
		model.addAttribute("entity",entity);
		returnEncryptedData(entity,model);
		String channelParam = EncryptionUtils.decrypt(entity.getChannelParam());
		JSONObject obj = JSON.parseObject(channelParam);
		JSONObject obj_exchange = JSON.parseObject(obj.get("exchange").toString());
		Integer oType = null;
		Integer cPair = null;
		if(entity.getExchange() != 0){
			if(ToolUtil.isNotEmpty(obj_exchange.get("order_type"))){
				oType = Integer.valueOf((String) obj_exchange.get("order_type"));
			}
			if(ToolUtil.isNotEmpty(obj_exchange.get("currency_pair"))){
				cPair = Integer.valueOf((String) obj_exchange.get("currency_pair"));
			}
		}
		model.addAttribute("orderType",oType);
		model.addAttribute("currencyPair",cPair);
		//虚拟币
    	List<Map<String,Object>> virtualCurrencyEnum = new ArrayList<Map<String,Object>>();
  		for(VirtualCurrencyEnum param:VirtualCurrencyEnum.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getValue());
  			value.put("cndesc", param.getCndesc());
  			value.put("endesc", param.getEndesc());
  			virtualCurrencyEnum.add(value);
  		}
  		model.addAttribute("virtualCurrencyEnum",virtualCurrencyEnum);
  		//交易策略
    	List<Map<String,Object>> exchangeStrategyEnum = new ArrayList<Map<String,Object>>();
  		for(ExchangeStrategy param:ExchangeStrategy.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("cndesc", param.getV1());
  			value.put("endesc", param.getV2());
  			exchangeStrategyEnum.add(value);
  		}
  		model.addAttribute("exchangeStrategyEnum",exchangeStrategyEnum);
  	    //交易所
    	List<Map<String,Object>> exchangeEnum = new ArrayList<Map<String,Object>>();
  		for(Exchange param:Exchange.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("message", param.getMessage());
  			exchangeEnum.add(value);
  		}
  		model.addAttribute("exchangeEnum",exchangeEnum);
  		//钱包
    	List<Map<String,Object>> hotWalletEnum = new ArrayList<Map<String,Object>>();
  		for(HotWallet param:HotWallet.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("message", param.getMessage());
  			hotWalletEnum.add(value);
  		}
  		model.addAttribute("hotWalletEnum",hotWalletEnum);
  		//钱包
    	List<Map<String,Object>> rateSourceEnum = new ArrayList<Map<String,Object>>();
  		for(RateSourceEnum param:RateSourceEnum.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getValue());
  			value.put("message", param.getDesc());
  			rateSourceEnum.add(value);
  		}
  		model.addAttribute("rateSourceEnum",rateSourceEnum);
  		//订单类型
    	List<Map<String,Object>> orderTypeEnum = new ArrayList<Map<String,Object>>();
  		for(OrderType param:OrderType.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("message", param.getMessage());
  			orderTypeEnum.add(value);
  		}
  		model.addAttribute("orderTypeEnum",orderTypeEnum);
  		//交易对
    	List<Map<String,Object>> currencyPairEnum = new ArrayList<Map<String,Object>>();
  		for(CurrencyPair param:CurrencyPair.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("message", param.getMessage());
  			currencyPairEnum.add(value);
  		}
  		model.addAttribute("currencyPairEnum",currencyPairEnum);
  		//交易对
    	List<Map<String,Object>> confirmationsEnum = new ArrayList<Map<String,Object>>();
  		for(SellType param:SellType.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("message", param.getMessage());
  			confirmationsEnum.add(value);
  		}
  		model.addAttribute("confirmationsEnum",confirmationsEnum);
		return PREFIX + "cryptoSettings_edit.html";
	}
	
	@RequestMapping(value = "/edit",method = RequestMethod.POST)
	@ResponseBody
	@Permission
	public Tip edit(@Valid CryptoSettings cryptoSettings){
		CryptoSettings oldEntity = cryptoSettingsService.selectById(cryptoSettings.getId());
		try {
			String channelParam = ChannelParamUtil.getInputOrOld(cryptoSettings.getChannelParam(),oldEntity,cryptoSettings.getHotWallet(),cryptoSettings.getExchange());
			cryptoSettings.setChannelParam(EncryptionUtils.encrypt(channelParam));
		}catch (Exception e) {
			throw new BitException(BizExceptionEnum.ENCRYPTION_FAILED);
		}
		
		cryptoSettings.setUpdateUser(ShiroKit.getUser().getAccount());
		cryptoSettings.setUpdateTime(new Date());
		cryptoSettingsService.updateById(cryptoSettings);
		//修改参数下发状态
		List<TerminalSetting> termList = terminalSettingService.selectList(new EntityWrapper<TerminalSetting>());
		List<Integer> termIds = new ArrayList<Integer>();
		for(TerminalSetting term:termList) {
			if(ToolUtil.isNotEmpty(term.getCryptoSettings())) {
				if(term.getCryptoSettings().contains(String.valueOf(cryptoSettings.getId()))) {
					termIds.add(term.getId());
				}
			}
		}
		if(ToolUtil.isNotEmpty(termIds)) {
			terminalSettingService.updateSendStatus(termIds,IsSend.NO.getCode());
		}
		return SUCCESS_TIP;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@Permission
	@ResponseBody
	public Tip delete(@RequestParam Integer id) {
		if (ToolUtil.isEmpty(id)) {
			throw new BitException(BizExceptionEnum.REQUEST_NULL);
		}
		this.cryptoSettingsService.deleteById(id);
		return SUCCESS_TIP;
	}
	
	@RequestMapping("/detail/{id}")
	@Permission
	public String openDetailView(@PathVariable Integer id,Model model){
		CryptoSettings entity = cryptoSettingsService.selectById(id);
		model.addAttribute("entity",entity);
		returnEncryptedData(entity,model);
		String channelParam = EncryptionUtils.decrypt(entity.getChannelParam());
		JSONObject obj = JSON.parseObject(channelParam);
		JSONObject obj_exchange = JSON.parseObject(obj.get("exchange").toString());
		String oType = null;
		String cPair = null;
		if(entity.getExchange() != 0){
			if(ToolUtil.isNotEmpty(obj_exchange.get("order_type"))){
				oType = OrderType.valueOf(Integer.valueOf((String) obj_exchange.get("order_type")));
			}
			if(ToolUtil.isNotEmpty(obj_exchange.get("currency_pair"))){
				cPair = CurrencyPair.valueOf(Integer.valueOf((String) obj_exchange.get("currency_pair")));
			}
		}
		model.addAttribute("orderType",oType);
		model.addAttribute("currencyPair",cPair);
		//虚拟币
    	List<Map<String,Object>> virtualCurrencyEnum = new ArrayList<Map<String,Object>>();
  		for(VirtualCurrencyEnum param:VirtualCurrencyEnum.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getValue());
  			value.put("cndesc", param.getCndesc());
  			value.put("endesc", param.getEndesc());
  			virtualCurrencyEnum.add(value);
  		}
  		model.addAttribute("virtualCurrencyEnum",virtualCurrencyEnum);
  		//交易策略
    	List<Map<String,Object>> exchangeStrategyEnum = new ArrayList<Map<String,Object>>();
  		for(ExchangeStrategy param:ExchangeStrategy.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("cndesc", param.getV1());
  			value.put("endesc", param.getV2());
  			exchangeStrategyEnum.add(value);
  		}
  		model.addAttribute("exchangeStrategyEnum",exchangeStrategyEnum);
  	    //交易所
    	List<Map<String,Object>> exchangeEnum = new ArrayList<Map<String,Object>>();
  		for(Exchange param:Exchange.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("message", param.getMessage());
  			exchangeEnum.add(value);
  		}
  		model.addAttribute("exchangeEnum",exchangeEnum);
  		//钱包
    	List<Map<String,Object>> hotWalletEnum = new ArrayList<Map<String,Object>>();
  		for(HotWallet param:HotWallet.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("message", param.getMessage());
  			hotWalletEnum.add(value);
  		}
  		model.addAttribute("hotWalletEnum",hotWalletEnum);
  		//钱包
    	List<Map<String,Object>> rateSourceEnum = new ArrayList<Map<String,Object>>();
  		for(RateSourceEnum param:RateSourceEnum.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getValue());
  			value.put("message", param.getDesc());
  			rateSourceEnum.add(value);
  		}
  		model.addAttribute("rateSourceEnum",rateSourceEnum);
  		//赎回方式
    	List<Map<String,Object>> confirmationsEnum = new ArrayList<Map<String,Object>>();
  		for(SellType param:SellType.values()){
  			Map<String, Object> value = new HashMap<String,Object>();
  			value.put("code", param.getCode());
  			value.put("message", param.getMessage());
  			confirmationsEnum.add(value);
  		}
  		model.addAttribute("confirmationsEnum",confirmationsEnum);
		return PREFIX + "cryptoSettings_detail.html";
	}
	
	private void returnEncryptedData(CryptoSettings entity,Model model){
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
		/*String channelParam = entity.getChannelParam();
		JSONObject obj = JSON.parseObject(channelParam);
		JSONObject obj_exchange = JSON.parseObject(obj.get("exchange").toString());
		Integer oType = null;
		Integer cPair = null;
		if(entity.getExchange() != 0){
			if(ToolUtil.isNotEmpty(obj_exchange.get("order_type"))){
				oType = Integer.valueOf((String) obj_exchange.get("order_type"));
			}
			if(ToolUtil.isNotEmpty(obj_exchange.get("currency_pair"))){
				cPair = Integer.valueOf((String) obj_exchange.get("currency_pair"));
			}
		}
		model.addAttribute("orderType",oType);
		model.addAttribute("currencyPair",cPair);*/
	}

}
