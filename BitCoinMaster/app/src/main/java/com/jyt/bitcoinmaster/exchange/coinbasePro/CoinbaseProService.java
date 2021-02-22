package com.jyt.bitcoinmaster.exchange.coinbasePro;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jyt.bitcoinmaster.exchange.Entity.BuyParam;
import com.jyt.bitcoinmaster.exchange.Entity.InitParam;
import com.jyt.bitcoinmaster.exchange.Entity.SellParam;
import com.jyt.bitcoinmaster.exchange.IExchange;


import com.jyt.bitcoinmaster.exchange.Entity.MyResponse;

import com.jyt.bitcoinmaster.statics.Setting;
import com.jyt.bitcoinmaster.timer.UploadTimer;
import com.jyt.bitcoinmaster.wallet.IWallet;
import com.jyt.bitcoinmaster.wallet.WalletFactory;
import com.jyt.bitcoinmaster.wallet.entity.SendCoinRequest;
import com.jyt.bitcoinmaster.wallet.entity.SendCoinResult;
import com.jyt.hardware.cashoutmoudle.Sqlite.DBHelper;
import com.jyt.hardware.cashoutmoudle.bean.BuyLog;
import com.jyt.hardware.cashoutmoudle.bean.TransferLog;

import com.jyt.hardware.cashoutmoudle.enums.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


/**
 * coinbasepro接口
 */
public class CoinbaseProService implements IExchange {

    private static Logger log = Logger.getLogger("BitCoinMaster");

    // 存不同币种的参数
    private Map<String, CoinbaseProParam> allProParam = new HashMap<>();

    private static CoinbaseProService coinbasePro;

    public static CoinbaseProService getInstance() {
        if (null == coinbasePro) {
            synchronized (CoinbaseProService.class) {
                if (null == coinbasePro) {
                    coinbasePro = new CoinbaseProService();
                }
            }
        }
        return coinbasePro;
    }

    @Override
    public boolean init(InitParam param) {
        boolean flag = HttpClientUtils.getProTimeDiff();
        if (flag) {
            JSONObject exchange = JSONObject.parseObject(Setting.cryptoSettings.getCryptoSetting(param.getCryptoCurrency()).getChannelParam()).getJSONObject("exchange");
            //JSONObject exchange = Setting.channelParam.getJSONObject("exchange");
            CoinbaseProParam coinbaseProParam = new CoinbaseProParam();
            coinbaseProParam.setProKey(exchange.getString("pro_key"));
            coinbaseProParam.setProSecret(exchange.getString("pro_secret"));
            allProParam.put(param.getCryptoCurrency(), coinbaseProParam);
            coinbaseProParam.setProPassphrase(exchange.getString("pro_passphrase"));
            coinbaseProParam.setCoinbaseAccountId(coinbaseAccounts(param.getCryptoCurrency()));
            String currencyPair = exchange.getString("currency_pair");
            coinbaseProParam.setProMinSize(minSizePro(currencyPair));
            if (StringUtils.isBlank(coinbaseProParam.getCoinbaseAccountId()) || StringUtils.isBlank(coinbaseProParam.getProMinSize())) {
                log.error("查询pro AccountId  ======= coinbaseAccountId 或 minSize为空");
                flag = false;
            }
            allProParam.put(param.getCryptoCurrency(), coinbaseProParam);
        }
        return flag;
    }

    @Override
    public MyResponse buyOrder(BuyParam param) {
        if (checkParam(param.getCryptoCurrency())) {
            return new MyResponse("CoinbasePro Incomplete parameters");
        }
        return this.buyOrder(param.getTransId(), param.getFunds(), param.getSize(), param.getPrice(), param.getTerminalNo(), param.getCryptoCurrency(),param.getCurrency());
    }

    @Override
    public MyResponse sellOrder(SellParam param) {
        if (checkParam(param.getCryptoCurrency())) {
            return new MyResponse("CoinbasePro Incomplete parameters");
        }
        return this.sellOrder(param.getTransId(), param.getTerminalNo(), param.getFunds(), param.getSize(), param.getPrice(), param.getCryptoCurrency(),param.getCurrency());
    }

    @Override
    public void checkOrderFinish(String orderId, boolean isBuy, String cryptoCurrency) {
        if (checkParam(cryptoCurrency)) {
            return;
        }
        this.checkProOrderFinish(orderId, isBuy, cryptoCurrency);
    }

    @Override
    public void cancelOrderById(String orderId, boolean isSell, String transId, String cryptoCurrency) {
        if (checkParam(cryptoCurrency)) {
            return;
        }
        this.cancelProOrderById(orderId, isSell, transId, cryptoCurrency);
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        return "";
    }

    @Override
    public boolean checkDepositStatus(TransferLog transferLog) {
        return false;
    }

    @Override
    public TransferLog getWithdrawStatus(String refid, String cryptoCurrency) {
        return new TransferLog();
    }

    /**
     * Place a New Order
     *
     * @param funds
     * @return
     */
    private MyResponse buyOrder(String transId, String funds, String size, String price, String terminalNo, String cryptoCurrency, String currency) {
        // 组装入库数据
        Map jsonResult = new HashMap();
        JSONObject exchange = JSONObject.parseObject(Setting.cryptoSettings.getCryptoSetting(cryptoCurrency).getChannelParam()).getJSONObject("exchange");
        String orderType = exchange.getString("order_type");
        String currencyPair = exchange.getString("currency_pair");
        jsonResult.put("type", ExchangeProEnum.OrderTypeEnum.getDesc(orderType));
        jsonResult.put("side", "buy");
        jsonResult.put("product_id", ProductIdEnum.getDesc(currencyPair));
        if (ExchangeProEnum.OrderTypeEnum.LIMIT.getValue().equals(orderType)) {
            if (StringUtils.isNotEmpty(price)) {
                try {
                    price = new BigDecimal(price).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                } catch (Exception e) {
                    price = "";
                }
            }
            jsonResult.put("price", price);
        }
        jsonResult.put("size", size);

        // 下单参数
        final String url = "/orders";
        final String method = "POST";
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("type", ExchangeProEnum.OrderTypeEnum.getDesc(orderType));
        jsonParam.put("side", "buy");
        jsonParam.put("product_id", getProductId(currencyPair));
        if (ExchangeProEnum.OrderTypeEnum.LIMIT.getValue().equals(orderType)) {
            jsonParam.put("price", price);
        }
        jsonParam.put("size", size);
        jsonParam.put("time_in_force", "GTT");
        jsonParam.put("cancel_after", "day");
        final String body = jsonParam.toString();
        log.info("======== coinbasePro 买币下单: " + price + " 数量: " + size + cryptoCurrency);
        MyResponse myResponse = HttpClientUtils.commonRequest(url, method, body, allProParam.get(cryptoCurrency));
        if (myResponse.isSuccess()) {
            jsonResult = new HashMap();
            String result = myResponse.getMessage();
            jsonResult = JSONObject.parseObject(result, Map.class);
            log.info("======== coinbasePro 买币下单成功");
        } else {
            jsonResult.put("message", myResponse.getMessage());
            log.info("======== coinbasePro 买币下单失败" + myResponse.getMessage());
        }
        jsonResult.put("funds", funds);

        // 插入数据库order表
        boolean insertResult = DBHelper.getHelper().insertOrderInfo(transId, jsonResult, terminalNo, cryptoCurrency,currency);
        if (insertResult) {
            return new MyResponse(true);
        } else {
            String errorMsg = ErrorTypeEnum.TO_ORDER.getValue() + jsonResult.get("message").toString();
            return new MyResponse(errorMsg);
        }
    }

    /**
     * Place a sell Order
     *
     * @param transId
     * @param terminalNo
     * @param funds
     * @param size
     * @param price
     */
    private MyResponse sellOrder(String transId, String terminalNo, String funds, String size, String price, String cryptoCurrency, String currency) {
        JSONObject exchange = JSONObject.parseObject(Setting.cryptoSettings.getCryptoSetting(cryptoCurrency).getChannelParam()).getJSONObject("exchange");
        String orderType = exchange.getString("order_type");
        String currencyPair = exchange.getString("currency_pair");

        // 组装数据入库
        Map jsonResult = new HashMap();
        jsonResult.put("type", ExchangeProEnum.OrderTypeEnum.getDesc(orderType));
        jsonResult.put("side", "sell");
        jsonResult.put("product_id", ProductIdEnum.getDesc(currencyPair));
        if (ExchangeProEnum.OrderTypeEnum.LIMIT.getValue().equals(orderType)) {
            if (StringUtils.isNotEmpty(price)) {
                try {
                    price = new BigDecimal(price).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                } catch (Exception e) {
                    price = "";
                }
            }
            jsonResult.put("price", price);
        }
        jsonResult.put("size", size);//转账的数量
        jsonResult.put("funds", funds);

        //判断ChannnelParam.minSize
        if (StringUtils.isBlank(allProParam.get(cryptoCurrency).getProMinSize())) {
            jsonResult.put("message", "CoinbaseProService.proMinSize is NULL");
            DBHelper.getHelper().insertOrderInfo(transId, jsonResult, terminalNo,cryptoCurrency,currency);
            //钱包提到pro失败，更新提现表状态
            DBHelper.getHelper().updateWithdrawRemark(transId, ErrorTypeEnum.TO_EXCHANGE.getValue() + jsonResult.get("message").toString(), TranStatusEnum.ERROR.getValue());
            return new MyResponse(jsonResult.get("message").toString());
        }
        //判断下单 Size < minSize
        BigDecimal bigSize = new BigDecimal(size);
        if (bigSize.compareTo(new BigDecimal(allProParam.get(cryptoCurrency).getProMinSize())) == -1) {
            jsonResult.put("message", "size is too small. Minimum size is " + allProParam.get(cryptoCurrency).getProMinSize() + cryptoCurrency);
            DBHelper.getHelper().insertOrderInfo(transId, jsonResult, terminalNo,cryptoCurrency,currency);
            //钱包提到pro失败，更新提现表状态
            DBHelper.getHelper().updateWithdrawRemark(transId, ErrorTypeEnum.TO_EXCHANGE.getValue() + jsonResult.get("message").toString(), TranStatusEnum.ERROR.getValue());
            return new MyResponse(ErrorTypeEnum.TO_EXCHANGE.getValue() + jsonResult.get("message").toString());
        }

        // 存钱
        MyResponse depositResult = depositsAccount(size, cryptoCurrency);
        if (!depositResult.isSuccess()) {
            jsonResult.put("message", depositResult.getMessage());
            log.info("coinbase转币coinbase pro 失败：" + depositResult.getMessage());
            // 插入数据库order表
            DBHelper.getHelper().insertOrderInfo(transId, jsonResult, terminalNo,cryptoCurrency,currency);
            //钱包提到pro失败，更新提现表状态
            DBHelper.getHelper().updateWithdrawRemark(transId, ErrorTypeEnum.TO_EXCHANGE.getValue() + jsonResult.get("message").toString(), TranStatusEnum.ERROR.getValue());
            return new MyResponse(ErrorTypeEnum.TO_EXCHANGE.getValue() + jsonResult.get("message").toString());
        }

        // 下单参数
        final String url = "/orders";
        final String method = "POST";
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("type", ExchangeProEnum.OrderTypeEnum.getDesc(orderType));
        jsonParam.put("side", "sell");
        jsonParam.put("product_id", getProductId(currencyPair));
        if (ExchangeProEnum.OrderTypeEnum.LIMIT.getValue().equals(orderType)) {
            jsonParam.put("price", price);
        }
        jsonParam.put("size", size);
        jsonParam.put("time_in_force", "GTT");
        jsonParam.put("cancel_after", "day");
        final String body = jsonParam.toString();
        log.info("======== coinbasePro 卖币下单: " + price + " 数量: " + size + cryptoCurrency);
        MyResponse myResponse = HttpClientUtils.commonRequest(url, method, body, allProParam.get(cryptoCurrency));
        if (myResponse.isSuccess()) {
            String result = myResponse.getMessage();
            jsonResult = new HashMap();
            jsonResult = JSONObject.parseObject(result, Map.class);
            log.info("======== coinbasePro 卖币下单成功");
        } else {
            jsonResult.put("message", myResponse.getMessage());
            log.info("======== coinbasePro 卖币下单失败" + myResponse.getMessage());
            //pro卖币下单失败
            DBHelper.getHelper().updateWithdrawRemark(transId, ErrorTypeEnum.TO_ORDER.getValue() + jsonResult.get("message").toString(), TranStatusEnum.ERROR.getValue());
        }
        jsonResult.put("funds", funds);

        // 插入数据库order表
        boolean insertResult = DBHelper.getHelper().insertOrderInfo(transId, jsonResult, terminalNo,cryptoCurrency,currency);
        if (insertResult) {
            return new MyResponse(true);
        } else {
            String errorMsg = ErrorTypeEnum.TO_ORDER.getValue() + jsonResult.get("message").toString();
            return new MyResponse(errorMsg);
        }
    }

    /**
     * 取消订单
     *
     * @param orderId
     */
    private void cancelProOrderById(String orderId, boolean isSell, String transId, String cryptoCurrency) {
        final String url = "/orders/" + orderId;
        try {
            log.info("======== cancel pro Order id_" + orderId);
            MyResponse myResponse = HttpClientUtils.commonRequest(url, "DELETE", "", allProParam.get(cryptoCurrency));
            String cancelStatus = "";
            String message = "";
            if (myResponse.isSuccess()) {
                // 更新coinbasePro订单表
                cancelStatus = OrderStatusEnum.PRO_CANCEL.getValue();
                message = "TIMEOUT CANCEL COINBASEPRO ORDER!";
            } else {
                cancelStatus = OrderStatusEnum.PRO_FAIL.getValue();
                message = "CANCEL COINBASEPRO FAIL !" + myResponse.getMessage();
            }
            DBHelper.getHelper().cancelCoinbaseOrder(orderId, message, cancelStatus);
            if (isSell) {
                DBHelper.getHelper().updateWithdrawRemark(transId, ErrorTypeEnum.CANCEL_ORDER.getValue() + message, TranStatusEnum.ERROR.getValue());
            } else {
                BuyLog buyLog = DBHelper.getHelper().queryBuyLogByTransId(transId);
                if (ExchangeStrategyEnum.WALLET_EXCHANGE.getValue() == buyLog.getExchangeStrategy()) {
                    DBHelper.getHelper().updateBuyLogRemarkAndStatus(transId, ErrorTypeEnum.CANCEL_ORDER.getValue() + message, TranStatusEnum.ERROR.getValue() + "");
                }
                if (ExchangeStrategyEnum.NO_WALLET.getValue() == buyLog.getExchangeStrategy()) {
                    DBHelper.getHelper().updateBuyLogRemarkAndStatus(transId, ErrorTypeEnum.CANCEL_ORDER.getValue() + message, TranStatusEnum.FAIL.getValue() + "");
                }
            }
//            // 取消卖币，把剩币转回去
//            if (isSell) {
//                log.info("======== 卖币 cancel pro Order id_" + orderId + "进行提款操作========");
//                withdrawalsAccount(orderId, DBHelper.getHelper().queryCoinbaseOrderByOrderId(orderId).getSize(), true);
//            }
        } catch (Exception e) {
            log.info("cancel order fail, id=" + orderId + ",message=" + e);
        }
    }

    /**
     * 校验pro是否完成订单
     * 买币订单成功，提现到coinbase 账户
     *
     * @param orderId
     * @param isBuy
     */
    private void checkProOrderFinish(String orderId, boolean isBuy, String cryptoCurrency) {
        final String url = "/orders/" + orderId;
        try {
            log.info("正在检查 coinbase pro 订单 id_" + orderId);
            MyResponse myResponse = HttpClientUtils.commonRequest(url, "GET", "", allProParam.get(cryptoCurrency));
            if (!myResponse.isSuccess()) {
                return;
            }
            String result = myResponse.getMessage();
            Map jsonObject = JSONObject.parseObject(result, Map.class);
            String status = "";
            Boolean settled = false;
            if (jsonObject.containsKey("status") && jsonObject.containsKey("settled")) {
                status = jsonObject.get("status").toString();
                settled = (Boolean) jsonObject.get("settled");
            }
            if ("done".equals(status) && settled) {
                status = OrderStatusEnum.PRO_CONFIRM.getValue();
                final String orderSize = (String) jsonObject.get("filled_size");// 单位btc
                final String orderFees = (String) jsonObject.get("fill_fees");//单位货币类型
                final String orderValue = (String) jsonObject.get("executed_value");//单位货币类型
                final String price = (String) jsonObject.get("price");//单位货币类型
                // 更新coinbasePro订单表
                DBHelper.getHelper().updateCoinbaseOrder(orderId, price, status, settled.toString(), orderSize, orderFees, orderValue);
                if (isBuy) {
                    log.info("coinbase pro 订单 id_" + orderId + "下单完成,购入了" + orderSize + cryptoCurrency);
                    // 买币订单成功，提现到coinbase 账户
                    withdrawalsAccount(orderId, orderSize, false, cryptoCurrency);
                } else {
                    log.info("coinbase pro 订单 id_" + orderId + "下单完成,卖出了" + orderSize + cryptoCurrency);
                    // 卖币订单成功，将pro剩余余额提现到coinbase账户
                    //withdrawalsAllToAccount();
                }
            } else {
                log.info("coinbase pro 订单 id_" + orderId + ": 订单未完成，处理中");
            }

        } catch (Exception e) {
            log.info("coinbase pro 订单异常 :" + e);
        }
    }


    /**
     * 提取资金到coinbase账户
     *
     * @param amount
     * @return
     */
    private void withdrawalsAccount(String orderId, String amount, boolean isCancel, String cryptoCurrency) {
        String transId = DBHelper.getHelper().queryCoinbaseOrderByOrderId(orderId).getTransId();
        BuyLog buyLog = DBHelper.getHelper().queryBuyLogByTransId(transId);

        log.info("======== coinbase pro提取"+cryptoCurrency+"到coinbase账户:" + allProParam.get(cryptoCurrency).getCoinbaseAccountId() + "  数量:" + amount + cryptoCurrency);
        if (StringUtils.isBlank(allProParam.get(cryptoCurrency).getCoinbaseAccountId())) {
            log.info("======== coinbase pro 获取coinbase账户:ChannnelParam.coinbaseAccountId为空");
            return;
        }
        String currency = ExchangeProEnum.proPair.getDesc(cryptoCurrency);
        final String url = "/withdrawals/coinbase-account";
        final String method = "POST";
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("amount", amount);
        jsonParam.put("currency", currency);
        jsonParam.put("coinbase_account_id", allProParam.get(cryptoCurrency).getCoinbaseAccountId());
        final String body = jsonParam.toString();
        String message = isCancel ? "Timeout Cancel order," : "";
        try {
            MyResponse myResponse = HttpClientUtils.commonRequest(url, method, body, allProParam.get(cryptoCurrency));
            if (myResponse.isSuccess()) {
                message += "withdrawals to coinbase success";
                // 买币成功 判断策略为2转给客户
                if (!isCancel) {
                    if (2 == buyLog.getExchangeStrategy() && String.valueOf(TranStatusEnum.PENDING.getValue()).equals(buyLog.getStatus())) {
                        IWallet wallet = WalletFactory.getWallet(Setting.cryptoSettings.getCryptoSetting(cryptoCurrency).getHotWallet());
                        SendCoinRequest request = new SendCoinRequest();
                        request.setAmount(buyLog.getAmount());
                        request.setReceiveAddress(buyLog.getAddress());
                        request.setCryptoCurrency(cryptoCurrency);
                        SendCoinResult result = wallet.sendCoin(request);
                        if (CodeMessageEnum.SUCCESS.getCode().equals(result.getCode())) {
                            buyLog.setStatus(TranStatusEnum.CONFIRM.getValue() + "");
                            buyLog.setChannelFee(result.getFee());
                            buyLog.setChannelTransId(result.getTransId());

                            // 分销
                            UploadTimer.agencyProfit(buyLog.getTransId(),"buy");

                        } else {
                            buyLog.setStatus(TranStatusEnum.FAIL.getValue() + "");
                            buyLog.setRemark(result.getMessage());
                        }
                        DBHelper.getHelper().updateBuyLogByTransId(buyLog);
                    }

                }
            } else {
                message += ErrorTypeEnum.TO_WALLET.getValue() + "withdrawals to coinbase fail";
                //提款失败
                if (ExchangeStrategyEnum.WALLET_EXCHANGE.getValue() == buyLog.getExchangeStrategy()) {
                    DBHelper.getHelper().updateBuyLogRemarkAndStatus(transId, message, TranStatusEnum.ERROR.getValue() + "");
                }
                if (ExchangeStrategyEnum.NO_WALLET.getValue() == buyLog.getExchangeStrategy()) {
                    DBHelper.getHelper().updateBuyLogRemarkAndStatus(transId, message, TranStatusEnum.FAIL.getValue() + "");
                }
                log.info(message);
            }
        } catch (Exception ex) {
            message += "withdrawals to coinbase fail: " + ex.getMessage();
            log.error("coinbase pro提取"+cryptoCurrency+"到coinbase账户 failed :", ex);
        }
//        DBHelper.getHelper().updateOrderMessage(orderId, message);
    }


    /**
     * 存入资金到pro账户
     *
     * @param amount
     * @return
     */
    private MyResponse depositsAccount(String amount, String cryptoCurrency) {
        if (StringUtils.isBlank(allProParam.get(cryptoCurrency).getCoinbaseAccountId())) {
            log.info("======== coinbase pro 获取coinbase账户:ChannnelParam.coinbaseAccountId为空");
            MyResponse myResponse = new MyResponse();
            myResponse.setSuccess(false);
            myResponse.setMessage("coinbase pro 获取coinbase账户:ChannnelParam.coinbaseAccountId为空");
            return myResponse;
        }
        log.info("======== coinbase存入"+cryptoCurrency+"到 coinbase pro账户:" + allProParam.get(cryptoCurrency).getCoinbaseAccountId() + "  数量:" + amount + cryptoCurrency);
        String currency = ExchangeProEnum.proPair.getDesc(cryptoCurrency);
        String url = "/deposits/coinbase-account";
        String method = "POST";
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("amount", amount);
        jsonParam.put("currency", currency);
        jsonParam.put("coinbase_account_id", allProParam.get(cryptoCurrency).getCoinbaseAccountId());
        String body = jsonParam.toString();
        return HttpClientUtils.commonRequest(url, method, body, allProParam.get(cryptoCurrency));
    }


    /**
     * 获取pro产品id
     * 默认BTC-USD
     *
     * @return
     */
    private String getProductId(String currencyPair) {
//        return "BTC-USD";
        return ExchangeProEnum.PairEnum.getDesc(currencyPair);
    }


    /**
     * 通过pro 获取coinbase accoundId,存款用
     *
     * @return
     */
    private String coinbaseAccounts(String cryptoCurrency) {
        String url = "/coinbase-accounts";
        String method = "GET";
        String result = "";
        MyResponse myResponse = HttpClientUtils.commonRequest(url, method, "", allProParam.get(cryptoCurrency));
        if (myResponse.isSuccess()) {
            result = myResponse.getMessage();
            JSONArray coinbaseAccounts = JSONObject.parseArray(result);
            int length = coinbaseAccounts.size();
            for (int i = 0; i < length; i++) {
                JSONObject accounts = coinbaseAccounts.getJSONObject(i);
                if (ExchangeProEnum.proPair.getDesc(cryptoCurrency).equals(accounts.getString("currency"))) {
                    return accounts.getString("id");
                }
            }
        }
        return result;
    }

    /**
     * coinbasepro最小交易量（BTC）
     *
     * @return
     */
    private String minSizePro(String currencyPair) {
        String url = "/products/" + getProductId(currencyPair);
        String method = "GET";
        String result = "";
        MyResponse myResponse = HttpClientUtils.singleRequest(url, method, "");
        if (myResponse.isSuccess()) {
            result = myResponse.getMessage();
            JSONObject sizePro = JSONObject.parseObject(result);
            return sizePro.getString("base_min_size");
        }
        return result;
    }

    /**
     * 校验必要参数
     *
     * @return
     */
    private boolean checkParam(String cryptoCurrency) {
        if (StringUtils.isEmpty(cryptoCurrency)){
            log.info("CoinbasePro Incomplete parameters :cryptoCurrency");
            return false;
        }
        boolean result = StringUtils.isBlank(allProParam.get(cryptoCurrency).getCoinbaseAccountId()) || StringUtils.isBlank(allProParam.get(cryptoCurrency).getProKey())
                || StringUtils.isBlank(allProParam.get(cryptoCurrency).getProSecret()) || StringUtils.isBlank(allProParam.get(cryptoCurrency).getProPassphrase());
        if (result) {
            log.info("CoinbasePro参数不全");
        }
        return result;
    }

}
