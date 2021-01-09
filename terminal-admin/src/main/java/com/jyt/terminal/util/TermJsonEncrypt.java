package com.jyt.terminal.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jyt.terminal.commom.enums.BitEnum.Exchange;
import com.jyt.terminal.commom.enums.BitEnum.HotWallet;
import com.jyt.terminal.commom.enums.BitException;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.model.TerminalSetting;

/**
 * 终端参数设置 - 渠道参数加密
 * @className TermJsonEncrypt
 * @author wangwei
 * @date 2019年11月25日
 *
 */
public class TermJsonEncrypt {
	private static final String defaultParam = "******";

	/**
	 * 
	 * @param channelParam 前端 - 渠道参数
	 * @param hotWallet 前端 - 热钱包
	 * @param exchangeType 前端 - 交易所
	 * @param entity 数据库 - 参数
	 * @return
	 */
	public static String channelParamEncrypt(String channelParam,Integer hotWallet,Integer exchangeType,TerminalSetting entity){
		try {
			JSONObject obj = JSONObject.parseObject(channelParam);
			JSONObject obj_wallet = JSON.parseObject(obj.get("wallet").toString());
			JSONObject obj_exchange = JSON.parseObject(obj.get("exchange").toString());
			Map<String,Object> wallet = new HashMap<String,Object>();
			Map<String,Object> exchange = new HashMap<String,Object>();
			
			//entity为null ==> 添加
			if(ToolUtil.isEmpty(entity)){
				//热钱包
				if(hotWallet == HotWallet.BITGO.getCode()){
					Map<String,Object> map = new HashMap<String,Object>();
					String walletPassphrase = obj_wallet.get("wallet_passphrase").toString();
					String accessToken = obj_wallet.get("access_token").toString();
					map.put("wallet_passphrase", walletPassphrase);
					map.put("access_token", accessToken);
					wallet = paramEncrypt(map);
					
					String walletId = obj_wallet.get("wallet_id").toString();
					wallet.put("wallet_id", walletId);
				}
				if(hotWallet == HotWallet.COINBASE.getCode()){
					Map<String,Object> map = new HashMap<String,Object>();
					String apiKey = obj_wallet.get("api_key").toString();
					String apiSecret = obj_wallet.get("api_secret").toString();
					map.put("api_key", apiKey);
					map.put("api_secret", apiSecret);
					wallet = paramEncrypt(map);
				}
				if(hotWallet == HotWallet.BLOCKCHAIN.getCode()){
					Map<String,Object> map = new HashMap<String,Object>();
					String walletId = obj_wallet.get("wallet_id").toString();
					String password = obj_wallet.get("password").toString();
					map.put("wallet_id", walletId);
					map.put("password", password);
					wallet = paramEncrypt(map);
				}
				//交易所
				if(exchangeType == Exchange.PRO.getCode()){
					Map<String,Object> map = new HashMap<String,Object>();
					String proKey = obj_exchange.get("pro_key").toString();
					String proSecret = obj_exchange.get("pro_secret").toString();
					String proPassphrase = obj_exchange.get("pro_passphrase").toString();
					map.put("pro_key", proKey);
					map.put("pro_secret", proSecret);
					map.put("pro_passphrase", proPassphrase);
					exchange = paramEncrypt(map);
				}
				if(exchangeType == Exchange.KRAKEN.getCode()){
					Map<String,Object> map = new HashMap<String,Object>();
					String krakenApiKey = obj_exchange.get("kraken_api_key").toString();
					String krakenSecret = obj_exchange.get("kraken_secret").toString();
					map.put("kraken_api_key", krakenApiKey);
					map.put("kraken_secret", krakenSecret);
					exchange = paramEncrypt(map);
					
					String krakenWithdrawalsAddressName = obj_exchange.get("kraken_withdrawals_address_name").toString();
					exchange.put("kraken_withdrawals_address_name", krakenWithdrawalsAddressName);
				}
			}else{//修改
				String param = entity.getChannelParam();
				JSONObject entityParam = JSONObject.parseObject(param);
				JSONObject entityParam_wallet = JSON.parseObject(entityParam.get("wallet").toString());
				JSONObject entityParam_exchange = JSON.parseObject(entityParam.get("exchange").toString());
				//热钱包
				if(hotWallet.equals(entity.getHotWallet())){    //页面未更换钱包
					if(hotWallet == HotWallet.BITGO.getCode()){
						String walletId = obj_wallet.get("wallet_id").toString();
						if(StringUtils.equals(walletId, defaultParam)){
							walletId = entityParam_wallet.get("wallet_id").toString();
						}
						wallet.put("wallet_id", walletId);
						
						String walletPassphrase = obj_wallet.get("wallet_passphrase").toString();
						if(StringUtils.equals(walletPassphrase, defaultParam)){
							walletPassphrase = entityParam_wallet.get("wallet_passphrase").toString();
						}else{
							walletPassphrase = EncryptionUtils.encrypt(walletPassphrase);
						}
						wallet.put("wallet_passphrase", walletPassphrase);
						
						String accessToken = obj_wallet.get("access_token").toString();
						if(StringUtils.equals(accessToken, defaultParam)){
							accessToken = entityParam_wallet.get("access_token").toString();
						}else{
							accessToken = EncryptionUtils.encrypt(accessToken);
						}
						wallet.put("access_token", accessToken);
					}
					if(hotWallet == HotWallet.COINBASE.getCode()){
	
						String apiKey = obj_wallet.get("api_key").toString();
						if(StringUtils.equals(apiKey, defaultParam)){
							apiKey = entityParam_wallet.get("api_key").toString();
						}else{
							apiKey = EncryptionUtils.encrypt(apiKey);
						}
						wallet.put("api_key", apiKey);
						
						String apiSecret = obj_wallet.get("api_secret").toString();
						if(StringUtils.equals(apiSecret, defaultParam)){
							apiSecret = entityParam_wallet.get("api_secret").toString();
						}else{
							apiSecret = EncryptionUtils.encrypt(apiSecret);
						}
						wallet.put("api_secret", apiSecret);
					}
					
					if(hotWallet == HotWallet.BLOCKCHAIN.getCode()){
						String walletId = obj_wallet.get("wallet_id").toString();
						if(StringUtils.equals(walletId, defaultParam)){
							walletId = entityParam_wallet.get("wallet_id").toString();
						}
						wallet.put("wallet_id", walletId);
						
						String password = obj_wallet.get("password").toString();
						if(StringUtils.equals(password, defaultParam)){
							password = entityParam_wallet.get("password").toString();
						}else{
							password = EncryptionUtils.encrypt(password);
						}
						wallet.put("password", password);
					}
				}else{
					if(hotWallet == HotWallet.BITGO.getCode()){
						Map<String,Object> map = new HashMap<String,Object>();
						String walletPassphrase = obj_wallet.get("wallet_passphrase").toString();
						String accessToken = obj_wallet.get("access_token").toString();
						map.put("wallet_passphrase", walletPassphrase);
						map.put("access_token", accessToken);
						wallet = paramEncrypt(map);
						
						String walletId = obj_wallet.get("wallet_id").toString();
						wallet.put("wallet_id", walletId);
					}
					if(hotWallet == HotWallet.COINBASE.getCode()){
						Map<String,Object> map = new HashMap<String,Object>();
						String apiKey = obj_wallet.get("api_key").toString();
						String apiSecret = obj_wallet.get("api_secret").toString();
						map.put("api_key", apiKey);
						map.put("api_secret", apiSecret);
						wallet = paramEncrypt(map);
					}
					if(hotWallet == HotWallet.BLOCKCHAIN.getCode()){
						Map<String,Object> map = new HashMap<String,Object>();
						String walletId = obj_wallet.get("wallet_id").toString();
						String password = obj_wallet.get("password").toString();
						map.put("wallet_id", walletId);
						map.put("password", password);
						wallet = paramEncrypt(map);
					}
				}
				//交易所
				if(exchangeType.equals(entity.getExchange())){    //未更换交易所
					//PRO
					if(exchangeType == Exchange.PRO.getCode()){
						String proKey = obj_exchange.get("pro_key").toString();
						if(ToolUtil.isNotEmpty(param) && StringUtils.equals(proKey, defaultParam)){
							proKey = entityParam_exchange.get("pro_key").toString();
						}else{
							proKey = EncryptionUtils.encrypt(proKey);
						}
						exchange.put("pro_key", proKey);
						
						String proSecret = obj_exchange.get("pro_secret").toString();
						if(StringUtils.equals(proSecret, defaultParam)){
							proSecret = entityParam_exchange.get("pro_secret").toString();
						}else{
							proSecret = EncryptionUtils.encrypt(proSecret);
						}
						exchange.put("pro_secret", proSecret);
						
						String proPassphrase = obj_exchange.get("pro_passphrase").toString();
						if(StringUtils.equals(proPassphrase, defaultParam)){
							proPassphrase = entityParam_exchange.get("pro_passphrase").toString();
						}else{
							proPassphrase = EncryptionUtils.encrypt(proPassphrase);
						}
						exchange.put("pro_passphrase", proPassphrase);
					}
					//KRAKEN
					if(exchangeType == Exchange.KRAKEN.getCode()){
						String krakenApiKey = obj_exchange.get("kraken_api_key").toString();
						if(ToolUtil.isNotEmpty(param) && StringUtils.equals(krakenApiKey, defaultParam)){
							krakenApiKey = entityParam_exchange.get("kraken_api_key").toString();
						}else{
							krakenApiKey = EncryptionUtils.encrypt(krakenApiKey);
						}
						exchange.put("kraken_api_key", krakenApiKey);
						
						String krakenSecret = obj_exchange.get("kraken_secret").toString();
						if(StringUtils.equals(krakenSecret, defaultParam)){
							krakenSecret = entityParam_exchange.get("kraken_secret").toString();
						}else{
							krakenSecret = EncryptionUtils.encrypt(krakenSecret);
						}
						exchange.put("kraken_secret", krakenSecret);
						
						String krakenWithdrawalsAddressName = obj_exchange.get("kraken_withdrawals_address_name").toString();
						if(StringUtils.equals(krakenWithdrawalsAddressName, defaultParam)){
							krakenWithdrawalsAddressName = entityParam_exchange.get("kraken_withdrawals_address_name").toString();
						}
						exchange.put("kraken_withdrawals_address_name", krakenWithdrawalsAddressName);
					}
					
				}else{
					if(exchangeType == Exchange.PRO.getCode()){
						Map<String,Object> map = new HashMap<String,Object>();
						String proKey = obj_exchange.get("pro_key").toString();
						String proSecret = obj_exchange.get("pro_secret").toString();
						String proPassphrase = obj_exchange.get("pro_passphrase").toString();
						map.put("pro_key", proKey);
						map.put("pro_secret", proSecret);
						map.put("pro_passphrase", proPassphrase);
						exchange = paramEncrypt(map);
					}
					if(exchangeType == Exchange.KRAKEN.getCode()){
						Map<String,Object> map = new HashMap<String,Object>();
						String krakenApiKey = obj_exchange.get("kraken_api_key").toString();
						String krakenSecret = obj_exchange.get("kraken_secret").toString();
						map.put("kraken_api_key", krakenApiKey);
						map.put("kraken_secret", krakenSecret);
						exchange = paramEncrypt(map);
						
						String krakenWithdrawalsAddressName = obj_exchange.get("kraken_withdrawals_address_name").toString();
						exchange.put("kraken_withdrawals_address_name", krakenWithdrawalsAddressName);
					}
				}
			}
			
			exchange.put("order_type", obj_exchange.get("order_type"));
			exchange.put("currency_pair", obj_exchange.get("currency_pair"));
			obj.put("wallet", wallet);
			obj.put("exchange", exchange);
			channelParam = obj.toJSONString();
			return channelParam;
		} catch (Exception e) {
			throw new BitException(BizExceptionEnum.ENCRYPTION_FAILED);
		}
	}
	
	/**
	 * 参数加密
	 * @return
	 */
	private static Map<String,Object> paramEncrypt(Map<String,Object> map){
		Map<String,Object> result = new HashMap<String,Object>();
		for (Map.Entry<String,Object> entry : map.entrySet()){
			String key = entry.getKey();
			String value = entry.getValue().toString();
			result.put(key, EncryptionUtils.encrypt(value));
		}
		return result;
	}
	
}
