package com.jyt.bitcoinmaster.rate.coinbase;

import com.alibaba.fastjson.JSONObject;
import com.jyt.bitcoinmaster.rate.IRate;
import com.jyt.bitcoinmaster.rate.entity.QueryPriceRequest;
import com.jyt.bitcoinmaster.rate.entity.QueryPriceResult;
import com.jyt.hardware.cashoutmoudle.enums.CodeMessageEnum;
import com.jyt.hardware.cashoutmoudle.enums.CryptoCurrencyEnum;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

/**
 * coinbase 获取汇率
 *
 * @author huoChangGuo
 * @since 2019/12/18
 */
public class CoinbaseService implements IRate {

    private static Logger log = Logger.getLogger("BitCoinMaster");

    private static CoinbaseService coinbase;

    public static CoinbaseService getInstance() {
        if (null == coinbase) {
            synchronized (CoinbaseService.class) {
                if (null == coinbase) {
                    coinbase = new CoinbaseService();
                }
            }
        }
        return coinbase;
    }

    @Override
    public QueryPriceResult queryMarketPrice(QueryPriceRequest request) {
        QueryPriceResult result = new QueryPriceResult();
        String cryptoCurrency = CryptoCurrencyEnum.getDesc(request.getCryptoCurrency());
        String url = "v2/exchange-rates?currency="+ cryptoCurrency+"&&random="+System.currentTimeMillis();
        String method = "GET";
        try {
            String resp = HttpClientUtils.singleRequest(url, method, "");
            JSONObject resultJson = JSONObject.parseObject(resp);
            if(resultJson.containsKey("errors")){
                //失败
                result.setCode(CodeMessageEnum.FAIL.getCode());
                result.setMessage(resultJson.getJSONArray("errors").getJSONObject(0).getString("message"));
                log.info("[Rate coinbase] 查询"+cryptoCurrency+"市场价格失败: " + resultJson.getJSONArray("errors").getJSONObject(0).getString("message"));
            }else{
                result.setCode(CodeMessageEnum.SUCCESS.getCode());
                result.setMessage(CodeMessageEnum.SUCCESS.getMessage());
                String price = resultJson.getJSONObject("data").getJSONObject("rates").getString(request.getCurrency());
                result.setPrice(new BigDecimal(price).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                // 交易所价格
                if(StringUtils.isNotEmpty(request.getExchangeCurrency())){// 策略0
                    String exchangePrice = resultJson.getJSONObject("data").getJSONObject("rates").getString(request.getExchangeCurrency());
                    result.setExchangeRate(new BigDecimal(exchangePrice).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                }
            }
        } catch (Exception e) {
            log.info("[Rate coinbase]查询比特币市场价格 系统异常:" ,e);
            result.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
