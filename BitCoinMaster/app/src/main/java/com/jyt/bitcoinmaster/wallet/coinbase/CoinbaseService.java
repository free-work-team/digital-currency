package com.jyt.bitcoinmaster.wallet.coinbase;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jyt.bitcoinmaster.wallet.entity.CoinbaseParam;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransByTxIdRequest;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransByTxIdResult;
import com.jyt.bitcoinmaster.wallet.entity.SendExchangeResult;
import com.jyt.hardware.cashoutmoudle.enums.CodeMessageEnum;
import com.jyt.hardware.cashoutmoudle.enums.CryptoCurrencyEnum;
import com.jyt.hardware.cashoutmoudle.enums.TranStatusEnum;
import com.jyt.bitcoinmaster.exchange.Entity.SellParam;
import com.jyt.hardware.cashoutmoudle.enums.ExchangeEnum;
import com.jyt.bitcoinmaster.exchange.ExchangeFactory;
import com.jyt.bitcoinmaster.statics.Setting;
import com.jyt.bitcoinmaster.wallet.IWallet;
import com.jyt.bitcoinmaster.wallet.entity.CreateAddressRequest;
import com.jyt.bitcoinmaster.wallet.entity.CreateAddressResult;
import com.jyt.bitcoinmaster.wallet.entity.InitRequest;
import com.jyt.bitcoinmaster.wallet.entity.InitResult;
import com.jyt.bitcoinmaster.wallet.entity.QueryBalanceRequest;
import com.jyt.bitcoinmaster.wallet.entity.QueryBalanceResult;
import com.jyt.bitcoinmaster.wallet.entity.QueryPriceRequest;
import com.jyt.bitcoinmaster.wallet.entity.QueryPriceResult;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransCreateRequest;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransCreateResult;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransConfirmRequest;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransConfirmResult;
import com.jyt.bitcoinmaster.wallet.entity.SendCoinRequest;
import com.jyt.bitcoinmaster.wallet.entity.SendCoinResult;
import com.jyt.hardware.cashoutmoudle.bean.WithdrawLog;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import java.math.BigDecimal;
import java.util.*;


/**
 * CoinBase接口
 */
public class CoinbaseService implements IWallet {

    private static Logger log = Logger.getLogger("BitCoinMaster");


    //coinbase
    public static Map<String,CoinbaseParam> paramMap = new HashMap<String,CoinbaseParam>();
    //public static Map<String,String> accountIdMap = new HashMap<String,String>();
    //public static String apiKey;
    //public static String apiSecret;

    private static CoinbaseService coinbaseService;

    public static CoinbaseService getInstance() {
            if (null == coinbaseService) {
                synchronized (CoinbaseService.class) {
                    if(null == coinbaseService) {
                        coinbaseService = new CoinbaseService();
                    }
                }
            }
        return coinbaseService;
    }
    @Override
    public InitResult init(InitRequest initRequest) {
        InitResult initResult = new InitResult();
        boolean flag = HttpClientUtils.getCoinbaseTimeDiff();
        if(flag){
            CoinbaseParam param = new CoinbaseParam();
            JSONObject channelParam = JSONObject.parseObject(Setting.cryptoSettings.getCryptoSetting(initRequest.getCryptoCurrency()).getChannelParam());
            String apiKey =channelParam.getJSONObject("wallet").getString("api_key");
            String apiSecret = channelParam.getJSONObject("wallet").getString("api_secret");
            param.setApiKey(apiKey);
            param.setApiSecret(apiSecret);
            paramMap.put(initRequest.getCryptoCurrency(),param);
            if(StringUtils.isNotBlank(apiKey) && StringUtils.isNotBlank(apiSecret)){
                QueryBalanceRequest request = new QueryBalanceRequest();
                request.setCryptoCurrency(initRequest.getCryptoCurrency());
                QueryBalanceResult result = queryBalance(request);
                param.setAccountId(result.getAccountId());
                //paramMap.put(initRequest.getCryptoCurrency(),param);

                initResult.setCode(result.getCode());
                initResult.setMessage(result.getMessage());
                if(!CodeMessageEnum.SUCCESS.getCode().equals(result.getCode())){
                    log.info("[coinbase]初始化 查询accountId失败:"+result.getMessage());
                }
            }else{
                initResult.setCode(CodeMessageEnum.FAIL.getCode());
                initResult.setMessage("Incomplete setting parameters（coinbase）");
                log.info("[coinbase]初始化 参数设置缺失apikey或apiSecret");
            }
        }else{
            initResult.setCode(CodeMessageEnum.FAIL.getCode());
            initResult.setMessage("Coinbase Synchronization time failed");
            log.info("[coinbase]初始化 同步时间失败");
        }
        return initResult;
    }

    @Override
    public SendCoinResult sendCoin(SendCoinRequest sendCoinRequest) {
        SendCoinResult sendCoinResult = new SendCoinResult();
        log.info("[coinbase]发送比特币 金额："+ sendCoinRequest.getAmount()+" sat");

        String url = "/v2/accounts/" + paramMap.get(sendCoinRequest.getCryptoCurrency()).getAccountId() + "/transactions";
        String method = "POST";

        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("type", "send");
            jsonParam.put("to", sendCoinRequest.getReceiveAddress());
            //coinbase单位转换
            BigDecimal amount = new BigDecimal(sendCoinRequest.getAmount()).divide(new BigDecimal(100000000));
            jsonParam.put("amount", amount.toString());
            jsonParam.put("currency", CryptoCurrencyEnum.getDesc(sendCoinRequest.getCryptoCurrency()));

//          jsonParam.put("fee", "0.00002002");
            jsonParam.put("idem", UUID.randomUUID());
            String body = jsonParam.toString();
            String result = HttpClientUtils.commonRequest(url, method, body,sendCoinRequest.getCryptoCurrency());
            log.info("[coinbase]发送比特币 返回参数"+ result);
            JSONObject jsonResult = JSONObject.parseObject(result);
            if(jsonResult.containsKey("errors")){
                //失败
                sendCoinResult.setCode(CodeMessageEnum.FAIL.getCode());
                sendCoinResult.setMessage(jsonResult.getJSONArray("errors").getJSONObject(0).getString("message"));
                log.info("[coinbase]发送比特币 失败:" + jsonResult.getJSONArray("errors").getJSONObject(0).getString("message"));
            }else{
                sendCoinResult.setCode(CodeMessageEnum.SUCCESS.getCode());
                sendCoinResult.setMessage(CodeMessageEnum.SUCCESS.getMessage());
                JSONObject transfer = jsonResult.getJSONObject("data");
                sendCoinResult.setTransId(transfer.getString("id"));
                BigDecimal fee = new BigDecimal(transfer.getJSONObject("network").getJSONObject("transaction_fee").getString("amount")).multiply(new BigDecimal(100000000));
                sendCoinResult.setFee(fee.setScale(0, BigDecimal.ROUND_UP).longValue()+"");
            }
        }catch (Exception e){
            log.info("[coinbase]发送比特币 系统异常",e);
            sendCoinResult.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            sendCoinResult.setMessage(e.getMessage());
        }
        return  sendCoinResult;
    }

    @Override
    public QueryTransCreateResult queryTransCreate(QueryTransCreateRequest request) {
        QueryTransCreateResult transResult = new QueryTransCreateResult();
        CoinbaseParam param = paramMap.get(request.getCryptoCurrency());
        if(StringUtils.isBlank(param.getAccountId()) || StringUtils.isBlank(param.getApiKey()) || StringUtils.isBlank(param.getApiSecret())){
            //未初始化
            transResult.setCode(CodeMessageEnum.FAIL.getCode());
            transResult.setMessage("Parameter not initialized");
            log.info("[coinbase] 根据地址查询交易失败: 参数未初始化");
            return  transResult;
        }
        String url = "/v2/accounts/" + param.getAccountId() + "/addresses/" + request.getWithdrawLog().getAddressId() + "/transactions";
        String method = "GET";
        try {
            String result = HttpClientUtils.commonRequest(url, method, "",request.getCryptoCurrency());
            JSONObject jsonResult = JSONObject.parseObject(result);
            if(jsonResult.containsKey("errors")){
                //失败
                transResult.setCode(CodeMessageEnum.FAIL.getCode());
                transResult.setMessage(jsonResult.getJSONArray("errors").getJSONObject(0).getString("message"));
                log.info("[coinbase] 根据地址id查询交易失败: " + jsonResult.getJSONArray("errors").getJSONObject(0).getString("message"));
            }else{
                JSONArray data = jsonResult.getJSONArray("data");
                if (data.size() != 0) {
                    for (int i = 0; i < data.size(); i++) {
                        String amount = data.getJSONObject(i).getJSONObject("amount").getString("amount");
                        BigDecimal transAmount = new BigDecimal(amount).multiply(new BigDecimal(100000000));
                        BigDecimal requestAmount = new BigDecimal(request.getWithdrawLog().getAmount());
                        if(transAmount.compareTo(requestAmount) == 0){
                            transResult.setCode(CodeMessageEnum.SUCCESS.getCode());
                            transResult.setMessage(CodeMessageEnum.SUCCESS.getMessage());
                            transResult.setTransId(data.getJSONObject(i).getString("id"));
                            return transResult;
                        }
                    }
                }
                log.info("[coinbase] 根据地址查询交易："+request.getWithdrawLog().getTargetAddress()+" 的订单 未创建! ");
                transResult.setCode(CodeMessageEnum.FAIL.getCode());
                transResult.setMessage("交易订单未被创建");
            }
        }catch (Exception e){
            log.info("[coinbase]根据地址查询交易 系统异常",e);
            transResult.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            transResult.setMessage(e.getMessage());
        }

        return transResult;
    }

    @Override
    public QueryTransConfirmResult queryTransConfirm(QueryTransConfirmRequest request) {
        QueryTransConfirmResult transResult = new QueryTransConfirmResult();
        CoinbaseParam param = paramMap.get(request.getCryptoCurrency());
        if(StringUtils.isBlank(param.getAccountId()) || StringUtils.isBlank(param.getApiKey()) || StringUtils.isBlank(param.getApiSecret())){
            //未初始化
            transResult.setCode(CodeMessageEnum.FAIL.getCode());
            transResult.setMessage("Parameter not initialized");
            log.info("[coinbase] 根据订单号查询交易失败: 参数未初始化");
            return  transResult;
        }
        String url = "/v2/accounts/" + param.getAccountId() + "/transactions/" + request.getTransId();
        String method = "GET";

        try {
            String result = HttpClientUtils.commonRequest(url, method, "",request.getCryptoCurrency());
            log.info("[coinbase] 根据订单号查询交易成功,上游返回: " + result);
            JSONObject jsonResult = JSONObject.parseObject(result);
            if (jsonResult.containsKey("errors")) {
                transResult.setCode(CodeMessageEnum.FAIL.getCode());
                transResult.setMessage(jsonResult.getJSONArray("errors").getJSONObject(0).getString("message"));
                log.info("[coinbase] 根据订单号查询交易失败: " + jsonResult.getJSONArray("errors").getJSONObject(0).getString("message"));
            }else{
                transResult.setCode(CodeMessageEnum.SUCCESS.getCode());
                transResult.setMessage(CodeMessageEnum.SUCCESS.getMessage());
                if (jsonResult.containsKey("data")) {
                    if ("completed".equals(jsonResult.getJSONObject("data").getString("status"))) {
                        transResult.setStatus(TranStatusEnum.CONFIRM.getValue());
                        log.info("[coinbase] transId: "+request.getTransId()+" 已付款,已确认");
                    }else{
                        transResult.setStatus(TranStatusEnum.PENDING.getValue());
                    }
                    transResult.setTxId(jsonResult.getJSONObject("data").getJSONObject("network").getString("hash"));
                }

            }
        }catch (Exception e){
            log.info("[bitgo]根据id查询交易 系统异常",e );
            transResult.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            transResult.setMessage(e.getMessage());
        }
        return transResult;
    }

    @Override
    public QueryTransByTxIdResult queryTransByTxId(QueryTransByTxIdRequest request) {
        QueryTransByTxIdResult transResult = new QueryTransByTxIdResult();
        CoinbaseParam param = paramMap.get(request.getCryptoCurrency());
        if (StringUtils.isBlank(param.getAccountId()) || StringUtils.isBlank(param.getApiKey()) || StringUtils.isBlank(param.getApiSecret())) {
            //未初始化
            transResult.setCode(CodeMessageEnum.FAIL.getCode());
            transResult.setMessage("Parameter not initialized");
            log.info("[coinbase] 根据hash和地址查询交易失败: 参数未初始化");
            return transResult;
        }
        if (CryptoCurrencyEnum.ETH.getValue().equals(request.getCryptoCurrency())) {
            //以太坊需要转成addressId
            String addressId = getAddressId(request.getAddress(), request.getCryptoCurrency());
            request.setTxId(request.getTxId().substring(2));
            request.setAddress(addressId);
        }
        String url = "/v2/accounts/" + param.getAccountId() + "/addresses/" + request.getAddress() + "/transactions";
        return getTranPageData(url, request.getTxId(), request.getAddress(), request.getCryptoCurrency(), transResult);
    }


    /**
     * 递归翻页查询交易
     * @param url
     * @param txId
     * @param address
     * @param cryptoCurrency
     * @param transResult
     * @return
     */
    private QueryTransByTxIdResult getTranPageData(String url, String txId,String address, String cryptoCurrency,QueryTransByTxIdResult transResult) {
        String method = "GET";
        try {
            String result = HttpClientUtils.commonRequest(url, method, "",cryptoCurrency);
            JSONObject jsonResult = JSONObject.parseObject(result);
            if(jsonResult.containsKey("errors")){
                //失败
                transResult.setCode(CodeMessageEnum.FAIL.getCode());
                transResult.setMessage(jsonResult.getJSONArray("errors").getJSONObject(0).getString("message"));
                log.info("[coinbase] 根据hash和地址查询交易失败: " + jsonResult.getJSONArray("errors").getJSONObject(0).getString("message"));
            }else{
                JSONArray data = jsonResult.getJSONArray("data");
                if (data.size() != 0) {
                    for (int i = 0; i < data.size(); i++) {
                        String hash = data.getJSONObject(i).getJSONObject("network").getString("hash");
                        if(txId.equals(hash)){
                            String status = data.getJSONObject(i).getString("status");
                            transResult.setCode(CodeMessageEnum.SUCCESS.getCode());
                            transResult.setMessage(CodeMessageEnum.SUCCESS.getMessage());
                            if("completed".equals(status)){
                                transResult.setStatus(TranStatusEnum.CONFIRM.getValue());
                            }else{
                                transResult.setStatus(TranStatusEnum.PENDING.getValue());
                            }
                            return  transResult;
                        }
                    }
                    String nextUrl = jsonResult.getJSONObject("pagination").getString("next_uri");
                    if (StringUtils.isNotEmpty(nextUrl)) {
                        return getTranPageData(nextUrl, txId, address,cryptoCurrency,transResult);
                    }
                }
                log.info("[coinbase] 根据hash和地址查询交易："+address+" 的订单 未创建! ");
                transResult.setCode(CodeMessageEnum.FAIL.getCode());
                transResult.setMessage("交易订单未被创建");
            }
        }catch (Exception e){
            log.info("[coinbase]根据hash和地址查询交易 系统异常",e);
            transResult.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            transResult.setMessage(e.getMessage());
        }
        return transResult;
    }

    @Override
    public CreateAddressResult createAddress(CreateAddressRequest request) {
        CreateAddressResult result = new CreateAddressResult();
        String url = "/v2/accounts/" +  paramMap.get(request.getCryptoCurrency()).getAccountId()  + "/addresses";
        String method = "POST";
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("name", "my receive address" + System.currentTimeMillis());
            String resp = HttpClientUtils.commonRequest(url, method, jsonParam.toString(),request.getCryptoCurrency());
            JSONObject resultJson = JSONObject.parseObject(resp);
            if(resultJson.containsKey("errors")){
                //失败
                result.setCode(CodeMessageEnum.FAIL.getCode());
                result.setMessage(resultJson.getJSONArray("errors").getJSONObject(0).getString("message"));
                log.info("[coinbase] 创建地址失败: " + resultJson.getJSONArray("errors").getJSONObject(0).getString("message"));
            }else{
                result.setCode(CodeMessageEnum.SUCCESS.getCode());
                result.setMessage(CodeMessageEnum.SUCCESS.getMessage());
                result.setAddress(resultJson.getJSONObject("data").getString("address"));
                result.setAddressId(resultJson.getJSONObject("data").getString("id"));
            }
        } catch (Exception e) {
            log.info("[coinbase]创建地址 系统异常" ,e);
            result.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            result.setMessage(e.getMessage());
        }
       return result;
    }

    private String getAddressId(String address, String cryptoCurrency) {
        String url = "/v2/accounts/" + paramMap.get(cryptoCurrency).getAccountId() + "/addresses";
        return getAddressPageData(address, url, cryptoCurrency);
    }

    /**
     * 递归查询所有页码的地址
     * @param address
     * @param url
     * @param cryptoCurrency
     * @return
     */
    private String getAddressPageData(String address, String url, String cryptoCurrency) {
        String method = "GET";
        try {
            String resp = HttpClientUtils.commonRequest(url, method, "", cryptoCurrency);
            JSONObject resultJson = JSONObject.parseObject(resp);
            if (resultJson.containsKey("errors")) {
                //失败
                log.error("[coinbase] 根据地址查询地址ID失败: " + resultJson.getJSONArray("errors").getJSONObject(0).getString("message"));
            } else {
                JSONArray data = resultJson.getJSONArray("data");
                for (int i = 0; i < data.size(); i++) {
                    if (StringUtils.equalsIgnoreCase(address, data.getJSONObject(i).getString("address"))) {
                        return data.getJSONObject(i).getString("id");
                    }
                }
                String nextUrl = resultJson.getJSONObject("pagination").getString("next_uri");
                if (StringUtils.isNotEmpty(nextUrl)) {
                    return getAddressPageData(address, nextUrl, cryptoCurrency);
                }
            }
        } catch (Exception e) {
            log.info("[coinbase]根据地址查询地址ID失败 系统异常", e);
        }
        return null;
    }


    @Override
    public QueryBalanceResult queryBalance(QueryBalanceRequest request) {
        QueryBalanceResult result = new QueryBalanceResult();
        String url = "/v2/accounts";
        String method = "GET";
        try {
            String resp = HttpClientUtils.commonRequest(url, method, "",request.getCryptoCurrency());
            JSONObject respJson = JSONObject.parseObject(resp);
            if (respJson.containsKey("errors")) {
                //失败
                result.setCode(CodeMessageEnum.FAIL.getCode());
                result.setMessage(respJson.getJSONArray("errors").getJSONObject(0).getString("message"));
                log.info("[coinbase] 查询余额失败: " + respJson.getJSONArray("errors").getJSONObject(0).getString("message"));
            }else {
                JSONArray data = respJson.getJSONArray("data");
                for (int i = 0; i < data.size(); i++) {
                    String walletName = data.getJSONObject(i).getString("name");// xx BTC Wallet
                    List<String> walletNameArrays = Arrays.asList(walletName.split(" "));
                    if (walletNameArrays.size() > 0) {
                        if (walletNameArrays.contains(CryptoCurrencyEnum.getDesc(request.getCryptoCurrency()))) {
                            BigDecimal balance = new BigDecimal(data.getJSONObject(i).getJSONObject("balance").getString("amount"));
                            log.info("[coinbase]" + request.getCryptoCurrency() + " walletName=" + walletName + " accountId= " + data.getJSONObject(i).getString("id")+ " balance = " + balance);
                            result.setAccountId(data.getJSONObject(i).getString("id"));
                            result.setCode(CodeMessageEnum.SUCCESS.getCode());
                            result.setMessage(CodeMessageEnum.SUCCESS.getMessage());
                            result.setBalance(balance);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("[coinbase]查询余额 系统异常",e);
            result.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public QueryPriceResult queryMarketPrice(QueryPriceRequest request) {
        QueryPriceResult result = new QueryPriceResult();
        String url = "v2/exchange-rates?currency=BTC&&random"+System.currentTimeMillis();
        String method = "GET";
        try {
            String resp = HttpClientUtils.commonRequest(url, method, "","btc");
            JSONObject resultJson = JSONObject.parseObject(resp);
            if(resultJson.containsKey("errors")){
                //失败
                result.setCode(CodeMessageEnum.FAIL.getCode());
                result.setMessage(resultJson.getJSONArray("errors").getJSONObject(0).getString("message"));
                log.info("[coinbase] 查询比特币市场价格失败: " + resultJson.getJSONArray("errors").getJSONObject(0).getString("message"));
            }else{
                result.setCode(CodeMessageEnum.SUCCESS.getCode());
                result.setMessage(CodeMessageEnum.SUCCESS.getMessage());
                String price = resultJson.getJSONObject("data").getJSONObject("rates").getString(request.getCurrency());
                result.setPrice(new BigDecimal(price).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                // 交易所价格
                String exchangePrice = resultJson.getJSONObject("data").getJSONObject("rates").getString(request.getExchangeCurrency());
                result.setExchangeRate(new BigDecimal(exchangePrice).setScale(2,BigDecimal.ROUND_HALF_UP).toString());

            }
        } catch (Exception e) {
            log.info("[coinbase]查询比特币市场价格 系统异常",e);
            result.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public SendExchangeResult sendExchangeByCreate(WithdrawLog withdrawLog) {
        SendExchangeResult sendExchangeResult = new SendExchangeResult();
        if(1 == withdrawLog.getExchangeStrategy()){
            //交易所策略为1，从钱包直接转出到交易所
            if(ExchangeEnum.COINBASEPRO.getValue() == Integer.valueOf(withdrawLog.getStrategy())){
                sendExchangeResult.setCode(CodeMessageEnum.PROCESSING.getCode());
                //pro
                ExchangeFactory.getExchange(Integer.valueOf(withdrawLog.getStrategy())).sellOrder(new SellParam(withdrawLog.getTransId(), withdrawLog.getTerminalNo(), withdrawLog.getCash(), new BigDecimal(withdrawLog.getAmount()).divide(new BigDecimal(100000000)).toString(), withdrawLog.getExchangeRate(),withdrawLog.getCryptoCurrency(),withdrawLog.getCurrency()));
            }else{
                SendCoinRequest request = new SendCoinRequest();
                BigDecimal amount = new BigDecimal(withdrawLog.getAmount()).subtract(getReserveFee(withdrawLog.getCryptoCurrency()));
                request.setAmount(amount.toString());
                request.setCryptoCurrency(withdrawLog.getCryptoCurrency());
                Integer hotWallet = Setting.cryptoSettings.getCryptoSetting(withdrawLog.getCryptoCurrency()).getHotWallet();
                request.setReceiveAddress( ExchangeFactory.getExchange(hotWallet).getDepositAddress(withdrawLog.getCryptoCurrency()));
                SendCoinResult result = sendCoin(request);
                sendExchangeResult.setCode(result.getCode());
                sendExchangeResult.setMessage(result.getMessage());
                if(CodeMessageEnum.SUCCESS.getCode().equals(result.getCode())){
                    sendExchangeResult.setActualAmount(amount.toString());
                    sendExchangeResult.setFee(result.getFee());
                    sendExchangeResult.setTransId(result.getTransId());
                    log.info("[coinbase] 钱包发送交易所成功，交易Id:"+result.getTransId()+"，平台手续费:"+result.getFee());
                }else{
                    log.info("[coinbase] 钱包发送交易所失败，"+result.getMessage());
                }
            }
        }else{
            sendExchangeResult.setCode(CodeMessageEnum.PROCESSING.getCode());
        }
        return  sendExchangeResult;
    }

    @Override
    public SendExchangeResult sendExchangeByConfirm(WithdrawLog withdrawLog) {
        SendExchangeResult sendExchangeResult = new SendExchangeResult();
        if(2 == withdrawLog.getExchangeStrategy()){
            Integer hotWallet = Setting.cryptoSettings.getCryptoSetting(withdrawLog.getCryptoCurrency()).getExchange();
            if(ExchangeEnum.COINBASEPRO.getValue() == hotWallet){
                sendExchangeResult.setCode(CodeMessageEnum.SUCCESS.getCode());
                //pro
                ExchangeFactory.getExchange(Integer.valueOf(withdrawLog.getStrategy())).sellOrder(new SellParam(withdrawLog.getTransId(), withdrawLog.getTerminalNo(), withdrawLog.getCash(), new BigDecimal(withdrawLog.getAmount()).divide(new BigDecimal(100000000)).toString(), withdrawLog.getExchangeRate(),withdrawLog.getCryptoCurrency(),withdrawLog.getCurrency()));
            }else{
                SendCoinRequest request = new SendCoinRequest();
                BigDecimal amount = new BigDecimal(withdrawLog.getAmount()).subtract(getReserveFee(withdrawLog.getCryptoCurrency()));
                request.setAmount(amount.toString());
                request.setReceiveAddress(ExchangeFactory.getExchange(hotWallet).getDepositAddress(withdrawLog.getCryptoCurrency()));
                request.setCryptoCurrency(withdrawLog.getCryptoCurrency());
                SendCoinResult result = sendCoin(request);
                sendExchangeResult.setCode(result.getCode());
                sendExchangeResult.setMessage(result.getMessage());
                if(CodeMessageEnum.SUCCESS.getCode().equals(result.getCode())){
                    sendExchangeResult.setActualAmount(amount.toString());
                    sendExchangeResult.setFee(result.getFee());
                    sendExchangeResult.setTransId(result.getTransId());
                    log.info("[coinbase] 钱包发送交易所成功，交易Id:"+result.getTransId()+"，平台手续费:"+result.getFee());
                }else{
                    log.info("[coinbase]  钱包发送交易所失败，"+result.getMessage());
                }
            }
        }else{
            sendExchangeResult.setCode(CodeMessageEnum.PROCESSING.getCode());
        }
        return  sendExchangeResult;
    }

    @Override
    public BigDecimal getReserveFee(String cryptoCurrency) {
        if(CryptoCurrencyEnum.BTC.getValue().equals(cryptoCurrency)){
            return new BigDecimal(10000);
        }else if (CryptoCurrencyEnum.ETH.getValue().equals(cryptoCurrency)){
            return new BigDecimal(50000);
        }
        return  null;
    }
}
