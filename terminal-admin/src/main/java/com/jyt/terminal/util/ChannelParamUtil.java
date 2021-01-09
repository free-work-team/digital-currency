package com.jyt.terminal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jyt.terminal.commom.enums.BitException;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.commom.enums.BitEnum.Exchange;
import com.jyt.terminal.commom.enums.BitEnum.HotWallet;
import com.jyt.terminal.model.CryptoSettings;

public class ChannelParamUtil {
	private static final String defaultParam = "******";

	public static String getInputOrOld(String channelParam,CryptoSettings oldEntity,Integer hotWallet,Integer exchangeType){
		String result = null;
		try {
			JSONObject obj = JSONObject.parseObject(channelParam);
			JSONObject obj_wallet = JSON.parseObject(obj.get("wallet").toString());
			JSONObject obj_exchange = JSON.parseObject(obj.get("exchange").toString());
			
			String param = EncryptionUtils.decrypt(oldEntity.getChannelParam());
			JSONObject entityParam = JSONObject.parseObject(param);
			JSONObject entityParam_wallet = JSON.parseObject(entityParam.get("wallet").toString());
			JSONObject entityParam_exchange = JSON.parseObject(entityParam.get("exchange").toString());
			
			Map<String,Object> wallet = new HashMap<String,Object>();
			Map<String,Object> exchange = new HashMap<String,Object>();
			//热钱包
			List<String> walletList = new ArrayList<>();
			walletList = getWalletKeyList(hotWallet);
			wallet = walletParamMap(obj_wallet,entityParam_wallet,walletList);
			//交易所
			List<String> exchangeList = new ArrayList<>();
			exchangeList = getExchangeKeyList(exchangeType);
			exchange = exchangeParamMap(obj_exchange,entityParam_exchange,exchangeList);
			
			if(exchangeType != Exchange.NO.getCode()){
				exchange.put("order_type", obj_exchange.get("order_type"));
				exchange.put("currency_pair", obj_exchange.get("currency_pair"));
			}
			obj.put("wallet", wallet);
			obj.put("exchange", exchange);
			result = obj.toJSONString();
		} catch (Exception e) {
			throw new BitException(BizExceptionEnum.ENCRYPTION_FAILED);
		}
		return result;
	}
	//钱包map
	private static Map<String,Object> walletParamMap(JSONObject newWallet,JSONObject oldWallet,List<String> list){
		Map<String,Object> walletMap = new HashMap<String,Object>();
		for(String str:list){
			String walletId = newWallet.get(str).toString();
			if(StringUtils.equals(walletId, defaultParam)){
				walletId = oldWallet.get(str).toString();
			}
			walletMap.put(str, walletId);
		}
		return walletMap;
	}
	//交易所map
	private static Map<String,Object> exchangeParamMap(JSONObject newExchange,JSONObject oldExchange,List<String> list){
		Map<String,Object> exchangeMap = new HashMap<String,Object>();
		for(String str:list){
			String walletId = newExchange.get(str).toString();
			if(StringUtils.equals(walletId, defaultParam)){
				walletId = oldExchange.get(str).toString();
			}
			exchangeMap.put(str, walletId);
		}
		return exchangeMap;
	}
	
	private static List<String> getWalletKeyList(Integer hotWallet){
		List<String> walletList = new ArrayList<>();
		if(hotWallet == HotWallet.BITGO.getCode()){
			walletList.add("wallet_id");
			walletList.add("wallet_passphrase");
			walletList.add("access_token");
		}
		if(hotWallet == HotWallet.COINBASE.getCode()){
			walletList.add("api_key");
			walletList.add("api_secret");
		}
		if(hotWallet == HotWallet.BLOCKCHAIN.getCode()){
			walletList.add("wallet_id");
			walletList.add("password");
		}
		return walletList;
	}
	
	private static List<String> getExchangeKeyList(Integer exchangeType){
		List<String> exchangeList = new ArrayList<>();
		//PRO
		if(exchangeType == Exchange.PRO.getCode()){
			exchangeList.add("pro_key");
			exchangeList.add("pro_secret");
			exchangeList.add("pro_passphrase");
		}
		//KRAKEN
		if(exchangeType == Exchange.KRAKEN.getCode()){
			exchangeList.add("kraken_api_key");
			exchangeList.add("kraken_secret");
			exchangeList.add("kraken_withdrawals_address_name");
		}
		return exchangeList;
	}
	
}
