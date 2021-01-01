package com.jyt.bitcoinmaster.wallet.bitgo;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.jyt.bitcoinmaster.statics.Setting;
import com.jyt.bitcoinmaster.utils.RootCmd;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransByTxIdRequest;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransByTxIdResult;
import com.jyt.bitcoinmaster.wallet.entity.SendExchangeResult;
import com.jyt.hardware.cashoutmoudle.enums.CodeMessageEnum;
import com.jyt.hardware.cashoutmoudle.enums.TranStatusEnum;


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
import com.jyt.hardware.cashoutmoudle.Sqlite.DBHelper;
import com.jyt.hardware.cashoutmoudle.bean.ParamSetting;
import com.jyt.hardware.cashoutmoudle.bean.WithdrawLog;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;


/**
 * BitGo接口
 */
public class BitGoService implements IWallet {

    private static Logger log = Logger.getLogger("BitCoinMaster");

    //private final String BASE_URL = "https://test.bitgo.com/api";  //测试
    private final String BASE_URL = "https://bitgo.com/api";       //正式

    private final String SERVER_URL = "http://localhost:3080/api";

    //private final String coin = "tbtc";//测试比特币
    private final String coin = "btc";  //生产比特币

    private static BitGoService bitGoService;

    //bitgo
    public static String walletPassphrase;

    public static String accessToken;

    public static String walletId ;

    public static BitGoService getInstance() {
        try {
            if (null == bitGoService) {
                // 模拟在创建对象之前做一些准备工作
                Thread.sleep(1000);
                synchronized (BitGoService.class) {
                    if(null == bitGoService) {
                        bitGoService = new BitGoService();
                    }
                }
            }
        } catch (InterruptedException e) {
            // TODO: handle exception
        }
        return bitGoService;
    }
    @Override
    public InitResult init(InitRequest param) {
        InitResult result = new InitResult();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //初始化bitgo本地安全服务/data/local/bitgo-express
                String cmd = "tsu \n pkill node \n set NODE_ENV=production \n node /data/local/bitgo-express/bin/bitgo-express --debug --port 3080 --env prod --bind localhost \n"; //生产
                //  String cmd = "tsu \n pkill node \n node /data/local/bitgo-express/bin/bitgo-express --debug --port 3080 --env test --bind localhost \n"; //测试
                log.info("bitgo服务出来=======================");
                RootCmd.execRootCmd(cmd);
            }
        }).start();
        JSONObject btcChannelParam = JSONObject.parseObject(Setting.cryptoSettings.getBtc().getChannelParam());
        walletId = btcChannelParam.getJSONObject("wallet").getString("wallet_id");
        walletPassphrase = btcChannelParam.getJSONObject("wallet").getString("wallet_passphrase");
        accessToken = btcChannelParam.getJSONObject("wallet").getString("access_token");
        if(StringUtils.isNotBlank(walletId) && StringUtils.isNotBlank(walletPassphrase) && StringUtils.isNotBlank(accessToken)){
            result.setCode(CodeMessageEnum.SUCCESS.getCode());
            result.setMessage(CodeMessageEnum.SUCCESS.getMessage());
        }else{
            result.setCode(CodeMessageEnum.FAIL.getCode());
            result.setMessage("Incomplete setting parameters（bitgo）");
            log.info("[bitgo]初始化 参数设置缺失walletId或walletPassphrase或accessToken");
        }
       return  result;
    }

    @Override
    public SendCoinResult sendCoin(SendCoinRequest sendCoinRequest) {
        SendCoinResult sendCoinResult = new SendCoinResult();
        String url = SERVER_URL+"/v2/" + coin + "/wallet/" + walletId + "/sendcoins";
        log.info("[bitgo]发送比特币 金额："+ sendCoinRequest.getAmount()+" Sat");
        try {
            String request = sendCoinParam(sendCoinRequest);

            HttpUtils httpUtils = HttpUtils.getInstance();
            String result = httpUtils.postJson(url, request, accessToken);

            log.info("[bitgo]发送比特币 返回参数"+ result);
            JSONObject jsonResult = JSONObject.parseObject(result);
            if(jsonResult.containsKey("error")){
                //失败
                sendCoinResult.setCode(CodeMessageEnum.FAIL.getCode());
                sendCoinResult.setMessage(jsonResult.getString("error"));
            }else{
                sendCoinResult.setCode(CodeMessageEnum.SUCCESS.getCode());
                sendCoinResult.setMessage(CodeMessageEnum.SUCCESS.getMessage());
                JSONObject transfer = jsonResult.getJSONObject("transfer");
                sendCoinResult.setTransId(transfer.getString("txid"));
                sendCoinResult.setFee(transfer.getString("feeString"));
            }
        }catch (Exception e){
            log.info("[bitgo]发送比特币 系统异常:" + e);
            sendCoinResult.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            sendCoinResult.setMessage(CodeMessageEnum.SYSTEM_EXCEPTION.getMessage());
        }
        return  sendCoinResult;
    }

    @Override
    public QueryTransCreateResult queryTransCreate(QueryTransCreateRequest request) {
        QueryTransCreateResult transResult = new QueryTransCreateResult();
        String url = BASE_URL + "/v1/address/" + request.getWithdrawLog().getTargetAddress() + "/tx";
        String result = null;
        try {
            HttpUtils httpUtils = HttpUtils.getInstance();
            result = httpUtils.get(url, accessToken);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if(jsonObject.containsKey("error")){
                //失败
                transResult.setCode(CodeMessageEnum.FAIL.getCode());
                transResult.setMessage(jsonObject.getString("error"));
                log.info("[bitgo] 根据地址查询交易失败：" +jsonObject.getString("error") );
            }else{
                JSONArray transactions = jsonObject.getJSONArray("transactions");
                if (transactions.size() != 0) {
                    for (int i = 0; i < transactions.size(); i++) {
                        JSONObject tran = transactions.getJSONObject(i);
                        JSONArray outputs = tran.getJSONArray("outputs");
                        for (int j = 0; j < outputs.size(); j++) {
                            JSONObject output = outputs.getJSONObject(j);
                            if (output.getString("account").equals(request.getWithdrawLog().getTargetAddress())) {
                                if (Integer.valueOf(request.getWithdrawLog().getAmount()).compareTo(output.getInteger("value")) == 0) {
                                    transResult.setCode(CodeMessageEnum.SUCCESS.getCode());
                                    transResult.setMessage(CodeMessageEnum.SUCCESS.getMessage());
                                    transResult.setTransId(tran.getString("id"));
                                    return transResult;
                                }
                            }
                        }
                    }
                }
                log.info("[bitgo] 根据地址查询交易：" + request.getWithdrawLog().getTargetAddress() + " 的订单 未创建! ");
                transResult.setCode(CodeMessageEnum.FAIL.getCode());
                transResult.setMessage("交易订单未被创建");
            }
        } catch (Exception e) {
            log.info("[bitgo]根据地址查询交易 系统异常:" + e);
            transResult.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            transResult.setMessage(e.getMessage());
        }
        return transResult;
    }

    @Override
    public QueryTransConfirmResult queryTransConfirm(QueryTransConfirmRequest request) {
        QueryTransConfirmResult transResult = new QueryTransConfirmResult();
        String url = BASE_URL+"/v1/tx/" + request.getTransId();
        try {
            HttpUtils httpUtils = HttpUtils.getInstance();
            String result = httpUtils.get(url, accessToken);
            JSONObject resultJson = JSONObject.parseObject(result);

            if(resultJson.containsKey("error")){
                //失败
                transResult.setCode(CodeMessageEnum.FAIL.getCode());
                transResult.setMessage(resultJson.getString("error"));
                log.info("[bitgo] 根据订单号查询交易失败: " + resultJson.getString("error"));
            }else{
                transResult.setCode(CodeMessageEnum.SUCCESS.getCode());
                transResult.setMessage(CodeMessageEnum.SUCCESS.getMessage());
                // 6确认为不可撤销，更新状态为confirm
                if (resultJson.containsKey("confirmations")) {
                    if (resultJson.getInteger("confirmations") >= 6) {
                        transResult.setStatus(TranStatusEnum.CONFIRM.getValue());
                        log.info("[bitgo] transId: "+request.getTransId()+" 已付款,已6确认");
                    }else{
                        transResult.setStatus(TranStatusEnum.PENDING.getValue());
                    }
                }
            }
        }catch (Exception e){
            log.info("[bitgo]根据id查询交易 系统异常:" + e);
            transResult.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            transResult.setMessage(e.getMessage());
        }
        return transResult;
    }

    @Override
    public QueryTransByTxIdResult queryTransByTxId(QueryTransByTxIdRequest request) {
        return null;
    }

    @Override
    public CreateAddressResult createAddress(CreateAddressRequest request) {
       CreateAddressResult result = new CreateAddressResult();
       String url = BASE_URL + "/v2/" + coin + "/wallet/" + walletId + "/address";

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("chain",0);
            jsonObject.put("label",System.currentTimeMillis()+"address");
            HttpUtils httpUtils = HttpUtils.getInstance();
            String resp = httpUtils.postJson(url, jsonObject.toJSONString(),  accessToken);
            JSONObject resultJson = JSONObject.parseObject(resp);
            if(resultJson.containsKey("error")){
                //失败
                result.setCode(CodeMessageEnum.FAIL.getCode());
                result.setMessage(resultJson.getString("error"));
            }else{
                result.setCode(CodeMessageEnum.SUCCESS.getCode());
                result.setMessage(CodeMessageEnum.SUCCESS.getMessage());
                result.setAddress(resultJson.getString("address"));
            }
        } catch (Exception e) {
            log.info("[bitgo]创建地址 系统异常:" + e);
            result.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            result.setMessage(e.getMessage());
        }
       return result;
    }

    @Override
    public QueryBalanceResult queryBalance(QueryBalanceRequest request) {
        QueryBalanceResult result = new QueryBalanceResult();
        String url = BASE_URL + "/v2/" + coin + "/wallet/" + walletId ;
        try {
            HttpUtils httpUtils = HttpUtils.getInstance();
            String resp = httpUtils.get(url, accessToken);
            JSONObject resultJson = JSONObject.parseObject(resp);
            if(resultJson.containsKey("error")){
                //失败
                result.setCode(CodeMessageEnum.FAIL.getCode());
                result.setMessage(resultJson.getString("error"));
            }else{
                result.setCode(CodeMessageEnum.SUCCESS.getCode());
                result.setMessage(CodeMessageEnum.SUCCESS.getMessage());
                BigDecimal balance = new BigDecimal(resultJson.getString("balance")).divide(new BigDecimal(100000000));
                result.setBalance(balance);
                log.info("[bitgo]BTC balance = " + balance);
            }
        } catch (Exception e) {
            log.info("[bitgo]查询余额 系统异常:" + e);
            result.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public QueryPriceResult queryMarketPrice(QueryPriceRequest request) {
        QueryPriceResult result = new QueryPriceResult();
        String url = BASE_URL + "/v1/market/latest?random="+System.currentTimeMillis();
        try {
            HttpUtils httpUtils = HttpUtils.getInstance();
            String resp = httpUtils.get(url, accessToken);
            JSONObject resultJson = JSONObject.parseObject(resp);
            if(resultJson.containsKey("error")){
                //失败
                result.setCode(CodeMessageEnum.FAIL.getCode());
                result.setMessage(resultJson.getString("error"));
            }else{
                result.setCode(CodeMessageEnum.SUCCESS.getCode());
                result.setMessage(CodeMessageEnum.SUCCESS.getMessage());
                String price = resultJson.getJSONObject("latest").getJSONObject("currencies").getJSONObject(request.getCurrency()).getString("last");
                result.setPrice(new BigDecimal(price).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
            }
        } catch (Exception e) {
            log.info("[bitgo]查询比特币市场价格 系统异常:" + e);
            result.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            result.setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public SendExchangeResult sendExchangeByCreate(WithdrawLog withdrawLog) { return null;}

    @Override
    public SendExchangeResult sendExchangeByConfirm(WithdrawLog withdrawLog) {
        return null;
    }

    @Override
    public BigDecimal getReserveFee(String cryptoCurrency) {
        return new BigDecimal(0);
    }


    private String connectBitGo(String reqData, String url, boolean isGet) {
        HttpUtils httpUtils = HttpUtils.getInstance();
        String resp = "";
        try {
            if (isGet) {
                resp = httpUtils.get(url, accessToken);
            } else {
                resp = httpUtils.postJson(url, reqData,  accessToken);
            }
        } catch (Exception e) {
            log.info("[bitgo] Connection failed :" + e);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("error",e.getMessage());
            resp = jsonObject.toJSONString();
        }
        log.info("bitogo连接返回： " + resp);
        return resp;
    }

    private String sendCoinParam(SendCoinRequest sendCoinRequest){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("address",sendCoinRequest.getReceiveAddress());
        jsonObject.put("amount",sendCoinRequest.getAmount());
        jsonObject.put("message","transfer");
        jsonObject.put("data","transfer");
        jsonObject.put("minConfirms",0);
        jsonObject.put("prv","");
        jsonObject.put("enforceMinConfirmsForChange",false);
        jsonObject.put("walletPassphrase",walletPassphrase);
        return jsonObject.toJSONString();
    }
}
