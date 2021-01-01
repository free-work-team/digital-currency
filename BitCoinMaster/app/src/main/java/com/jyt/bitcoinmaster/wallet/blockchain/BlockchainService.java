package com.jyt.bitcoinmaster.wallet.blockchain;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jyt.bitcoinmaster.exchange.Entity.SellParam;
import com.jyt.bitcoinmaster.exchange.ExchangeFactory;
import com.jyt.bitcoinmaster.statics.Setting;
import com.jyt.bitcoinmaster.utils.RootCmd;
import com.jyt.bitcoinmaster.wallet.IWallet;
import com.jyt.bitcoinmaster.wallet.coinbase.HttpClientUtils;
import com.jyt.bitcoinmaster.wallet.entity.BaseResult;
import com.jyt.bitcoinmaster.wallet.entity.CreateAddressRequest;
import com.jyt.bitcoinmaster.wallet.entity.CreateAddressResult;
import com.jyt.bitcoinmaster.wallet.entity.InitRequest;
import com.jyt.bitcoinmaster.wallet.entity.InitResult;
import com.jyt.bitcoinmaster.wallet.entity.QueryAccountResult;
import com.jyt.bitcoinmaster.wallet.entity.QueryBalanceRequest;
import com.jyt.bitcoinmaster.wallet.entity.QueryBalanceResult;
import com.jyt.bitcoinmaster.wallet.entity.QueryPriceRequest;
import com.jyt.bitcoinmaster.wallet.entity.QueryPriceResult;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransCreateRequest;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransCreateResult;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransConfirmRequest;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransConfirmResult;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransByTxIdRequest;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransByTxIdResult;
import com.jyt.bitcoinmaster.wallet.entity.SendCoinRequest;
import com.jyt.bitcoinmaster.wallet.entity.SendCoinResult;
import com.jyt.bitcoinmaster.wallet.entity.SendExchangeResult;
import com.jyt.hardware.cashoutmoudle.Sqlite.DBHelper;
import com.jyt.hardware.cashoutmoudle.bean.ParamSetting;
import com.jyt.hardware.cashoutmoudle.bean.WithdrawLog;
import com.jyt.hardware.cashoutmoudle.enums.CodeMessageEnum;
import com.jyt.hardware.cashoutmoudle.enums.ExchangeEnum;
import com.jyt.hardware.cashoutmoudle.enums.TranStatusEnum;
import com.jyt.hardware.cashoutmoudle.enums.TransferStatusEnum;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Blockchain接口
 */
public class BlockchainService implements IWallet {

    private static Logger log = Logger.getLogger("BitCoinMaster");


    private final String SERVER_URL = "http://localhost:3000/";

    private final String BASE_URL = "https://blockchain.info/";



    private static BlockchainService blockchainService;

    public static String walletId;

    public static String defaultPub;//默认账户私钥

    public static String password;//密码

    public static boolean isRunService;

    public static BlockchainService getInstance() {

            if (null == blockchainService) {
                synchronized (BlockchainService.class) {
                    if(null == blockchainService) {
                        blockchainService = new BlockchainService();
                    }
                }
            }
        return blockchainService;
    }
    @Override
    public InitResult init(InitRequest param) {
        InitResult result = new InitResult();
            if(!isRunService){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        log.info("blockchain服务正在启动。。。");
                        String cmd = "tsu \n pkill node \n  node /data/local/service-my-wallet-v3-master/bin/cli.js start \n "; //生产
                        isRunService = true;
                        RootCmd.execRootCmd(cmd);
                        log.info("blockchain服务已关闭！");
                        isRunService = false;
                    }
                }).start();
            }
            JSONObject btcChannelParam = JSONObject.parseObject(Setting.cryptoSettings.getBtc().getChannelParam());
            walletId = btcChannelParam.getJSONObject("wallet").getString("wallet_id");
            password = btcChannelParam.getJSONObject("wallet").getString("password");
            if(StringUtils.isNotBlank(walletId) && StringUtils.isNotBlank(password)){
                result.setCode(CodeMessageEnum.SUCCESS.getCode());
                result.setMessage(CodeMessageEnum.SUCCESS.getMessage());
            }else{
                result.setCode(CodeMessageEnum.FAIL.getCode());
                result.setMessage("Incomplete setting parameters（blockchain）");
                log.info("[blockchain]初始化 参数设置缺失walletId或password");
            }

        return  result;
    }

    private BaseResult queryPub(){
        BaseResult result = new BaseResult();
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("password",password);
            String response = HttpClient.getInstance().post(SERVER_URL, "merchant/"+walletId+"/accounts", params);
            JSONArray jsonArray = JSONArray.parseArray(response);
            defaultPub = jsonArray.getJSONObject(0).getString("extendedPublicKey");
            result.setCode(CodeMessageEnum.SUCCESS.getCode());
            result.setMessage(CodeMessageEnum.SUCCESS.getMessage());
        }catch (APIException ex){
            result.setCode(CodeMessageEnum.FAIL.getCode());
            result.setMessage(JSONObject.parseObject(ex.getMessage()).getString("error"));
            log.info("[blockchain]查询默认账户公钥失败:"+JSONObject.parseObject(ex.getMessage()).getString("error"));
        }catch (Exception e){
            result.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            result.setMessage(e.getMessage());
            log.info("[blockchain]查询默认账户公钥 系统异常",e);
        }
        return result;
    }



    @Override
    public SendCoinResult sendCoin(SendCoinRequest sendCoinRequest) {
        SendCoinResult sendCoinResult = new SendCoinResult();
        log.info("[blockchain]发送比特币 金额："+ sendCoinRequest.getAmount()+" sat");

        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("password",password);
            params.put("to",sendCoinRequest.getReceiveAddress());
            params.put("amount",sendCoinRequest.getAmount());
            if(StringUtils.isNotBlank(sendCoinRequest.getIndex())){
                params.put("from",sendCoinRequest.getIndex());
            }else{
                params.put("from","0");
            }
            String response = HttpClient.getInstance().post(SERVER_URL,"merchant/"+walletId+"/payment",params);
            log.info("[blockchain]发送比特币 返回参数"+ response);
            JSONObject resultJson = JSONObject.parseObject(response);
            sendCoinResult.setCode(CodeMessageEnum.SUCCESS.getCode());
            sendCoinResult.setMessage(CodeMessageEnum.SUCCESS.getMessage());
            sendCoinResult.setTransId(resultJson.getString("tx_hash"));
            sendCoinResult.setTxId(resultJson.getString("tx_hash"));
            sendCoinResult.setFee(resultJson.getInteger("fee")+"");
        }catch (APIException ex){
            sendCoinResult.setCode(CodeMessageEnum.FAIL.getCode());
            sendCoinResult.setMessage(JSONObject.parseObject(ex.getMessage()).getString("error"));
            log.info("[blockchain]发送比特币 失败:"+JSONObject.parseObject(ex.getMessage()).getString("error"));
        }catch (Exception e){
            log.info("[blockchain]发送比特币 系统异常" ,e);
            sendCoinResult.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            sendCoinResult.setMessage(e.getMessage());
        }
        return  sendCoinResult;



    }

    @Override
    public QueryTransCreateResult queryTransCreate(QueryTransCreateRequest request) {
        QueryTransCreateResult result = new QueryTransCreateResult();
        if(StringUtils.isBlank(walletId) || StringUtils.isBlank(password) ){
            //未初始化
            result.setCode(CodeMessageEnum.FAIL.getCode());
            result.setMessage("Parameter not initialized");
            log.info("[blockchain] 查询交易创建失败: 参数未初始化");
            return  result;
        }
        WithdrawLog withdrawLog = request.getWithdrawLog();
        JSONObject ext = JSONObject.parseObject(withdrawLog.getExtId());

        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("password",password);
            String response = HttpClient.getInstance().post(SERVER_URL, "merchant/"+walletId+"/accounts/"+ext.getString("xpub")+"/balance", params);
            JSONObject resultJson = JSONObject.parseObject(response);
            BigDecimal balance = new BigDecimal(resultJson.getString("balance"));
            if(balance.compareTo(new BigDecimal(withdrawLog.getAmount()))>=0){
                result.setCode(CodeMessageEnum.SUCCESS.getCode());
                result.setMessage(CodeMessageEnum.SUCCESS.getMessage());
                log.info("[blockchain]查询订单号为"+withdrawLog.getTransId()+"交易是否到账 已到账");
            }else{
                result.setCode(CodeMessageEnum.FAIL.getCode());
                result.setMessage("交易订单未被创建");
                log.info("[blockchain]查询订单号为"+withdrawLog.getTransId()+"交易是否到账 未到账");
            }
        }catch (APIException ex){
            result.setCode(CodeMessageEnum.FAIL.getCode());
            result.setMessage(JSONObject.parseObject(ex.getMessage()).getString("error"));
            log.info("[blockchain]查询订单号为"+withdrawLog.getTransId()+"交易是否到账 查询失败:"+JSONObject.parseObject(ex.getMessage()).getString("error"));
        }catch (Exception e){
            result.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            result.setMessage(e.getMessage());
            log.info("[blockchain]查询订单号为"+withdrawLog.getTransId()+"交易是否到账 系统异常",e);
        }

        return result;
    }

    @Override
    public QueryTransConfirmResult queryTransConfirm(QueryTransConfirmRequest request) {
        QueryTransConfirmResult result = new QueryTransConfirmResult();
        result.setCode(CodeMessageEnum.SUCCESS.getCode());
        result.setStatus(TranStatusEnum.CONFIRM.getValue());
        return result;
    }

    @Override
    public QueryTransByTxIdResult queryTransByTxId(QueryTransByTxIdRequest request) {
        QueryTransByTxIdResult result = new QueryTransByTxIdResult();

        QueryBalanceRequest queryBalanceRequest = new QueryBalanceRequest();
        queryBalanceRequest.setType("1");
        QueryBalanceResult queryBalanceResult = queryBalance(queryBalanceRequest);
        result.setCode(queryBalanceResult.getCode());
        result.setMessage(queryBalanceResult.getMessage());
        if(CodeMessageEnum.SUCCESS.getCode().equals(queryBalanceResult.getCode())){
            BigDecimal requestAmount = new BigDecimal(request.getAmount()).divide(new BigDecimal(100000000));
            if(queryBalanceResult.getBalance().compareTo(requestAmount)>=0){
                result.setStatus(TranStatusEnum.CONFIRM.getValue());
            }else{
                result.setStatus(TranStatusEnum.PENDING.getValue());
            }
        }
        return result;
    }

    @Override
    public CreateAddressResult createAddress(CreateAddressRequest request) {
       CreateAddressResult result = new CreateAddressResult();
       boolean flag= true;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("password",password);
            String response = HttpClient.getInstance().post(SERVER_URL, "merchant/"+walletId+"/accounts/create", params);
            JSONObject resultJson = JSONObject.parseObject(response);
            flag = false;
            log.info("[blockchain]创建高清账户成功");
            String xpub = resultJson.getString("xpub");
            response = HttpClient.getInstance().post(SERVER_URL, "merchant/"+walletId+"/accounts/"+xpub+"/receiveAddress", params);
            resultJson = JSONObject.parseObject(response);
            String address = resultJson.getString("address");
            log.info("[blockchain]创建地址成功:"+address);
            result.setCode(CodeMessageEnum.SUCCESS.getCode());
            result.setMessage(CodeMessageEnum.SUCCESS.getMessage());
            result.setAddress(address);
            result.setXpub(xpub);
        }catch (APIException ex){
            result.setCode(CodeMessageEnum.FAIL.getCode());
            result.setMessage(JSONObject.parseObject(ex.getMessage()).getString("error"));
            if(flag){
                log.info("[blockchain]创建高清账户失败:"+JSONObject.parseObject(ex.getMessage()).getString("error"));
            }else{
                log.info("[blockchain]创建地址失败:"+JSONObject.parseObject(ex.getMessage()).getString("error"));
            }
        }catch (Exception e){
            result.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            result.setMessage(e.getMessage());
            log.info("[blockchain]创建高清账户失败 系统异常",e);
        }
       return result;
    }



    @Override
    public QueryBalanceResult queryBalance(QueryBalanceRequest request) {
        QueryBalanceResult result = new QueryBalanceResult();
        if("1".equals(request.getType())){
            try {
                if(StringUtils.isBlank(defaultPub)){
                    BaseResult baseReult = queryPub();
                    if(!CodeMessageEnum.SUCCESS.getCode().equals(baseReult.getCode())){
                        result.setCode(baseReult.getCode());
                        result.setMessage(baseReult.getMessage());
                        return  result;
                    }
                }
                Map<String, String> params = new HashMap<String, String>();
                params.put("password",password);
                String response = HttpClient.getInstance().get(SERVER_URL, "merchant/"+walletId+"/accounts/"+defaultPub+"/balance", params);
                log.info("[blockchain]查询余额 返回参数:"+response);
                JSONObject resultJson = JSONObject.parseObject(response);
                String balance = resultJson.getString("balance");
                if("null".equals(balance)){
                    result.setCode(CodeMessageEnum.FAIL.getCode());
                    result.setMessage("查询余额为null");
                    log.info("[blockchain]查询余额为null");
                }else{
                    result.setCode(CodeMessageEnum.SUCCESS.getCode());
                    result.setMessage(CodeMessageEnum.SUCCESS.getMessage());
                    BigDecimal ableAmount = null;

                    ableAmount = new BigDecimal(balance).divide(new BigDecimal(100000000));
                    log.info("[blockchain]查询默认账户实际余额成功："+ableAmount+" bitcoin");

                    if(ableAmount.compareTo(new BigDecimal(0))<0){
                        ableAmount = new BigDecimal(0);
                    }
                    result.setBalance(ableAmount);
                }

            }catch (APIException ex){
                result.setCode(CodeMessageEnum.FAIL.getCode());
                result.setMessage(JSONObject.parseObject(ex.getMessage()).getString("error"));
                log.info("[blockchain]查询账户余额失败:"+JSONObject.parseObject(ex.getMessage()).getString("error"));
            }catch (Exception e){
                result.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
                result.setMessage(e.getMessage());
                log.info("[blockchain]查询账户余额 系统异常",e);
            }
        }else if("2".equals(request.getType())){
            result.setCode(CodeMessageEnum.SUCCESS.getCode());
            result.setMessage(CodeMessageEnum.SUCCESS.getMessage());
            result.setBalance(new BigDecimal(10000));
        }
        return result;
    }

    @Override
    public QueryPriceResult queryMarketPrice(QueryPriceRequest request) {
        QueryPriceResult result = new QueryPriceResult();
        try {
            Map<String, String> params = new HashMap<String, String>();
            String response = HttpClient.getInstance().get(BASE_URL, "ticker", params);
            JSONObject resultJson = JSONObject.parseObject(response);
            result.setCode(CodeMessageEnum.SUCCESS.getCode());
            result.setMessage(CodeMessageEnum.SUCCESS.getMessage());
            Double price = resultJson.getJSONObject(request.getCurrency()).getDouble("last");
            result.setPrice(price.toString());
            log.info("[blockchain]查询市场价格成功:"+price+request.getCurrency());
        }catch (APIException ex){
            result.setCode(CodeMessageEnum.FAIL.getCode());
            result.setMessage(JSONObject.parseObject(ex.getMessage()).getString("error"));
            log.info("[blockchain]查询市场价格失败:"+JSONObject.parseObject(ex.getMessage()).getString("error"));
        }catch (Exception e){
            result.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            result.setMessage(e.getMessage());
            log.info("[blockchain]查询市场价格 系统异常",e);
        }
        return result;
    }

    @Override
    public SendExchangeResult sendExchangeByCreate(WithdrawLog withdrawLog) {
        SendExchangeResult result = new SendExchangeResult();
        result.setCode(CodeMessageEnum.PROCESSING.getCode());
        return result;
    }

    @Override
    public SendExchangeResult sendExchangeByConfirm(WithdrawLog withdrawLog) {
        SendExchangeResult sendExchangeResult = new SendExchangeResult();
        //blockchain渠道，不管交易策略1还是2，都按2处理，确认后再从钱包转出到交易所
        if(1 == withdrawLog.getExchangeStrategy() || 2 == withdrawLog.getExchangeStrategy()){
            JSONObject jsonObject = JSONObject.parseObject(withdrawLog.getExtId());
            String xpub = jsonObject.getString("xpub");
            QueryAccountResult queryAccountResult = queryAccount(xpub);
            if(CodeMessageEnum.SUCCESS.getCode().equals(queryAccountResult.getCode())){
                SendCoinRequest request = new SendCoinRequest();
                request.setIndex(queryAccountResult.getIndex());
                BigDecimal amount = new BigDecimal(withdrawLog.getAmount()).subtract(getReserveFee(withdrawLog.getCryptoCurrency()));
                request.setAmount(amount.toString());
                request.setReceiveAddress( ExchangeFactory.getExchange(Setting.cryptoSettings.getBtc().getExchange()).getDepositAddress(withdrawLog.getCryptoCurrency()));
                SendCoinResult result = sendCoin(request);
                if(CodeMessageEnum.SUCCESS.getCode().equals(result.getCode())){
                    sendExchangeResult.setTxId(result.getTxId());
                    sendExchangeResult.setFee(result.getFee());
                    sendExchangeResult.setActualAmount(amount.toString());
                    log.info("[blockchain] 钱包发送交易所成功，交易Id:"+result.getTransId()+"，平台手续费:"+result.getFee());
                }else{
                    log.info("[blockchain] 钱包发送交易所失败，"+result.getMessage());
                }
                sendExchangeResult.setCode(result.getCode());
                sendExchangeResult.setMessage(result.getMessage());
            }else{
                log.info("[blockchain] 钱包发送交易所失败，"+queryAccountResult.getMessage());
                sendExchangeResult.setCode(queryAccountResult.getCode());
                sendExchangeResult.setMessage(queryAccountResult.getMessage());
            }
        }else{
            sendExchangeResult.setCode(CodeMessageEnum.PROCESSING.getCode());
        }

        return  sendExchangeResult;
    }

    private QueryAccountResult queryAccount(String xpub){
        QueryAccountResult result = new QueryAccountResult();
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("password",password);
            String response = HttpClient.getInstance().post(SERVER_URL, "merchant/"+walletId+"/accounts/"+xpub, params);
            JSONObject jsonObject = JSONObject.parseObject(response);

            result.setIndex(jsonObject.getString("index"));
            result.setCode(CodeMessageEnum.SUCCESS.getCode());
            result.setMessage(CodeMessageEnum.SUCCESS.getMessage());
        }catch (APIException ex){
            result.setCode(CodeMessageEnum.FAIL.getCode());
            result.setMessage(JSONObject.parseObject(ex.getMessage()).getString("error"));
            log.info("[blockchain]根据公钥查询账户索引 失败:"+JSONObject.parseObject(ex.getMessage()).getString("error"));
        }catch (Exception e){
            result.setCode(CodeMessageEnum.SYSTEM_EXCEPTION.getCode());
            result.setMessage(e.getMessage());
            log.info("[blockchain]根据公钥查询账户索引 系统异常",e);
        }
        return result;
    }

    @Override
    public BigDecimal getReserveFee(String cryptoCurrency) {
        return new BigDecimal(10000);
    }





}
