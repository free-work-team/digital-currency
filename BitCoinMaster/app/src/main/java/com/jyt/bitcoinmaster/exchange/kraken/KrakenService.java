package com.jyt.bitcoinmaster.exchange.kraken;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jyt.bitcoinmaster.exchange.Entity.BuyParam;
import com.jyt.bitcoinmaster.exchange.Entity.InitParam;
import com.jyt.bitcoinmaster.exchange.Entity.SellParam;
import com.jyt.bitcoinmaster.exchange.IExchange;

import com.jyt.bitcoinmaster.exchange.Entity.MyResponse;
import com.jyt.bitcoinmaster.statics.Setting;
import com.jyt.hardware.cashoutmoudle.Sqlite.DBHelper;
import com.jyt.hardware.cashoutmoudle.bean.*;
import com.jyt.hardware.cashoutmoudle.enums.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Kraken接口
 */
public class KrakenService implements IExchange {

    private static Logger log = Logger.getLogger("BitCoinMaster");
    // 存不同币种的参数
    private Map<String, KrakenParam> krakenAllParam = new HashMap<>();

    private static KrakenService kraken;

    public static KrakenService getInstance() {
        if (null == kraken) {
            synchronized (KrakenService.class) {
                if (null == kraken) {
                    kraken = new KrakenService();
                }
            }
        }
        return kraken;
    }

    @Override
    public boolean init(InitParam param) {
        try {
            JSONObject channelParam = JSONObject.parseObject(Setting.cryptoSettings.getCryptoSetting(param.getCryptoCurrency()).getChannelParam());
            // 获取参数
            JSONObject exchange = channelParam.getJSONObject("exchange");

            KrakenParam krakenParam = new KrakenParam();
            krakenParam.setKrakenApiKey(exchange.getString("kraken_api_key"));
            krakenParam.setKrakenSecret(exchange.getString("kraken_secret"));
            krakenParam.setKrakenWithdrawalsAddressName(exchange.getString("kraken_withdrawals_address_name"));
            krakenAllParam.put(param.getCryptoCurrency(), krakenParam);
            krakenParam.setKrakenAddress(getAddress(param.getCryptoCurrency()));
            krakenParam.setKrakenMinSize(new BigDecimal(ExchangeKrakenEnum.OrderMinSizeEnum.getDesc(param.getCryptoCurrency())));
            krakenParam.setKrakenWithdrawalsFee(new BigDecimal(ExchangeKrakenEnum.OrderWithdrawalsFee.getDesc(param.getCryptoCurrency())));
            if (checkParam(param.getCryptoCurrency()) || StringUtils.isBlank(krakenParam.getKrakenAddress()) || StringUtils.isBlank(krakenParam.getKrakenWithdrawalsAddressName())) {
                return false;
            }
            krakenAllParam.put(param.getCryptoCurrency(), krakenParam);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public MyResponse buyOrder(BuyParam param) {
        if (checkParam(param.getCryptoCurrency())) {
            return new MyResponse("Kraken Incomplete parameters");
        }
        return this.buyOrder(param.getTransId(), param.getFunds(), param.getSize(), param.getPrice(), param.getTerminalNo(), param.getCryptoCurrency(),param.getCurrency());
    }

    @Override
    public MyResponse sellOrder(SellParam param) {
        if (checkParam(param.getCryptoCurrency())) {
            return new MyResponse("Kraken Incomplete parameters");
        }
        // kraken 存款的手续费去掉，每次少卖5w
        BigDecimal size =new BigDecimal(param.getSize()) ;
        String newSize = size.subtract(new BigDecimal(ExchangeKrakenEnum.OrderDepositFee.getDesc(param.getCryptoCurrency()))).toString();
        return this.sellOrder(param.getTransId(), param.getTerminalNo(), param.getFunds(), newSize, param.getPrice(), param.getCryptoCurrency(),param.getCurrency());
    }

    @Override
    public void checkOrderFinish(String orderId, boolean isBuy, String cryptoCurrency) {
        if (checkParam(cryptoCurrency)) {
            return;
        }
        this.checkKrakenOrderFinish(orderId, isBuy, cryptoCurrency);
    }

    @Override
    public void cancelOrderById(String orderId, boolean isSell, String transId, String cryptoCurrency) {
        if (checkParam(cryptoCurrency)) {
            return;
        }
        this.cancelKrakenOrderById(orderId, isSell, transId, cryptoCurrency);
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        return krakenAllParam.get(cryptoCurrency).getKrakenAddress();
    }

    @Override
    public boolean checkDepositStatus(TransferLog transferLog) {
        boolean finish = false;
        if (checkParam(transferLog.getCryptoCurrency()) || StringUtils.isEmpty(transferLog.getTxId())) {
            return finish;
        }
        try {
            KrakenAPIClient client = new KrakenAPIClient();
            Map<String, Object> params = new HashMap<>();
            params.put("asset", ExchangeKrakenEnum.krakenPair.getDesc(transferLog.getCryptoCurrency()));
            params.put("method", ExchangeKrakenEnum.krakenMethod.getDesc(transferLog.getCryptoCurrency()));
            MyResponse result = client.depositStatus(params, krakenAllParam.get(transferLog.getCryptoCurrency()));
            List<JSONObject> list = JSONArray.parseArray(result.getMessage(), JSONObject.class);
            for (JSONObject item : list) {
                // 存款到账进行卖币操作
                if (item.getString("txid").equals(transferLog.getTxId()) && "Success".equals(item.getString("status"))) {
                    finish = true;
                }
            }
        } catch (Exception e) {
            log.error("检测" + transferLog.getTxId() + "的Kraken存款 fail", e);
        } finally {
            log.info(transferLog.getTxId() + "的Kraken存款" + (finish ? "已经到账!" : "未到账!"));
        }
        return finish;
    }

    /**
     * 获取提现到钱包的信息
     * 入库
     *
     * @param refid
     */
    @Override
    public TransferLog getWithdrawStatus(String refid, String cryptoCurrency) {
        TransferLog transferLog = new TransferLog();
        KrakenAPIClient client = new KrakenAPIClient();
        Map<String, Object> params = new HashMap<>();
        params.put("asset", ExchangeKrakenEnum.krakenPair.getDesc(cryptoCurrency));
        params.put("method", ExchangeKrakenEnum.krakenWithdrawMethod.getDesc(cryptoCurrency));
        try {
            MyResponse result = client.withdrawStatus(params, krakenAllParam.get(cryptoCurrency));
            List<JSONObject> list = JSONArray.parseArray(result.getMessage(), JSONObject.class);
            for (JSONObject item : list) {
                // 存款到账进行卖币操作
                if (item.getString("refid").equals(refid)) {
                    log.info("txid =" + item.getString("txid"));
                    log.info("fee =" + item.getString("fee"));
                    log.info("address =" + item.getString("info"));
                    transferLog.setTxId(item.getString("txid"));
                    transferLog.setFee(new BigDecimal(item.getString("fee")).multiply(new BigDecimal(100000000)).toString());
                    transferLog.setAddress(item.getString("info"));
                }
            }
        } catch (Exception e) {
            log.error("获取kraken 通过" + refid + "获取提款状态 fail", e);
        }
        return transferLog;
    }

    /**
     * 获取最新存款地址
     */
    private String getAddress(String cryptoCurrency) {
        if (checkParam(cryptoCurrency)) {
            return "";
        }
        return getDepositAddress(false, cryptoCurrency);
    }

    /*--------------------------------------------订单相关--------------------------------------------*/

    /**
     * 买币下单
     *
     * @param transId?
     * @param funds
     * @param size
     * @param price
     * @param terminalNo
     */
    private MyResponse buyOrder(String transId, String funds, String size, String price, String terminalNo, String cryptoCurrency, String currency) {
        JSONObject exchange = JSONObject.parseObject(Setting.cryptoSettings.getCryptoSetting(cryptoCurrency).getChannelParam()).getJSONObject("exchange");
        String orderType = exchange.getString("order_type");
        String currencyPair = exchange.getString("currency_pair");
        // 组装入库数据
        Map jsonResult = new HashMap();
        try {
            jsonResult.put("type", ExchangeKrakenEnum.OrderTypeEnum.getDesc(orderType));
            jsonResult.put("side", "buy");
            jsonResult.put("product_id", ProductIdEnum.getDesc(currencyPair));
            if (ExchangeKrakenEnum.OrderTypeEnum.LIMIT.getValue().equals(orderType)) {
                if (StringUtils.isNotEmpty(price)) {
                    price = new BigDecimal(price).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                }
                jsonResult.put("price", price);
            }
            //判断下单 Size < minSize
            BigDecimal bigSize = new BigDecimal(size).add(krakenAllParam.get(cryptoCurrency).getKrakenWithdrawalsFee());
            // 交易所加5w 大于等于50w,下单用加后数量
            if (bigSize.compareTo(krakenAllParam.get(cryptoCurrency).getKrakenMinSize()) != -1) {
                size = bigSize.toString();
            }
            jsonResult.put("size", size);
            // 下单参数
            KrakenAPIClient client = new KrakenAPIClient();
            Map<String, Object> params = new HashMap<>();
            params.put("pair", getProductId(currencyPair));
            params.put("type", "buy");
            params.put("ordertype", ExchangeKrakenEnum.OrderTypeEnum.getDesc(orderType));
            if (ExchangeKrakenEnum.OrderTypeEnum.LIMIT.getValue().equals(orderType)) {
                params.put("price", price);
            }
            params.put("volume", size);
            log.info("======== kraken 买币下单, 价格: " + price + " 数量: " + size + cryptoCurrency);
            log.info("======== kraken 买币下单, 参数:\n" + JSONObject.toJSONString(params));
            MyResponse myResponse = client.postAddOrder(params, krakenAllParam.get(cryptoCurrency));
            if (myResponse.isSuccess()) {
                String result = myResponse.getMessage();
                log.info("买币结果:" + result);
                String krakenId = JSONObject.parseObject(result).getJSONArray("txid").get(0).toString();
                jsonResult.put("id", krakenId);
            } else {
                jsonResult.put("message", myResponse.getMessage());
            }
        } catch (Exception e) {
            log.error("kraken买币失败", e);
            jsonResult.put("message", e.getMessage());
        }
        jsonResult.put("funds", funds);

        // 插入数据库order表
        boolean insertResult = DBHelper.getHelper().insertOrderInfo(transId, jsonResult, terminalNo,cryptoCurrency,currency);
        if (insertResult) {
            return new MyResponse(true);
        } else {
            return new MyResponse(ErrorTypeEnum.TO_ORDER.getValue() + jsonResult.get("message").toString());
        }
    }


    /**
     * 卖币下单
     *
     * @param transId
     * @param terminalNo
     * @param funds
     * @param size
     * @param price
     */
    private MyResponse sellOrder(String transId, String terminalNo, String funds, String size, String price, String cryptoCurrency, String currency) {
        // 组装数据入库
        Map jsonResult = new HashMap();
        try {
            JSONObject channelParam = JSONObject.parseObject(Setting.cryptoSettings.getCryptoSetting(cryptoCurrency).getChannelParam()).getJSONObject("exchange");
            String orderType = channelParam.getString("order_type");
            String currencyPair = channelParam.getString("currency_pair");

            jsonResult.put("type", ExchangeKrakenEnum.OrderTypeEnum.getDesc(orderType));
            jsonResult.put("side", "sell");
            jsonResult.put("product_id", ProductIdEnum.getDesc(currencyPair));
            if (ExchangeKrakenEnum.OrderTypeEnum.LIMIT.getValue().equals(orderType)) {
                if (StringUtils.isNotEmpty(price)) {
                    price = new BigDecimal(price).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                }
                jsonResult.put("price", price);
            }
            jsonResult.put("size", size);//转账的数量

            // 下单参数
            KrakenAPIClient client = new KrakenAPIClient();
            Map<String, Object> params = new HashMap<>();
            params.put("pair", getProductId(currencyPair));
            params.put("type", "sell");
            params.put("ordertype", ExchangeKrakenEnum.OrderTypeEnum.getDesc(orderType));
            if (ExchangeKrakenEnum.OrderTypeEnum.LIMIT.getValue().equals(orderType)) {
                params.put("price", price);
            }
            params.put("volume", size);
            log.info("======== kraken 卖币下单, 价格: " + price + " 数量: " + size + cryptoCurrency);

            log.info("======== kraken 卖币下单, 参数:\n" + JSONObject.toJSONString(params));
            MyResponse myResponse = client.postAddOrder(params, krakenAllParam.get(cryptoCurrency));
            if (myResponse.isSuccess()) {
                String result = myResponse.getMessage();
                log.info("kraken 卖币结果:" + result);
                String krakenId = JSONObject.parseObject(result).getJSONArray("txid").get(0).toString();
                jsonResult.put("id", krakenId);
            } else {
                jsonResult.put("message", myResponse.getMessage());
                //kraken卖币下单失败
                DBHelper.getHelper().updateWithdrawRemark(transId, ErrorTypeEnum.TO_ORDER.getValue() + jsonResult.get("message").toString(), TranStatusEnum.ERROR.getValue());
            }
        } catch (Exception e) {
            log.error("kraken卖币失败", e);
            jsonResult.put("message", e.getMessage());
        }
        jsonResult.put("funds", funds);

        // 插入数据库order表
        boolean insertResult = DBHelper.getHelper().insertOrderInfo(transId, jsonResult, terminalNo,cryptoCurrency,currency);
        if (insertResult) {
            return new MyResponse(true);
        } else {
            return new MyResponse(ErrorTypeEnum.TO_ORDER.getValue() + jsonResult.get("message").toString());
        }
    }

    /**
     * 校验是否完成订单
     * 买币订单成功，提现到coinbase 账户
     *
     * @param orderId
     * @param isBuy
     */
    private void checkKrakenOrderFinish(String orderId, boolean isBuy, String cryptoCurrency) {
        KrakenAPIClient client = new KrakenAPIClient();
        Map<String, Object> params = new HashMap<>();
        params.put("txid", orderId);
        try {
            MyResponse myResponse = client.queryOrders(params, krakenAllParam.get(cryptoCurrency));
            if (!myResponse.isSuccess()) {
                return;
            }
            String result = myResponse.getMessage();
            JSONObject jsonObject = JSONObject.parseObject(result).getJSONObject(orderId);
            String status = "";
            if (jsonObject.containsKey("status")) {
                status = jsonObject.get("status").toString();
            }
            if ("closed".equals(status)) {
                status = OrderStatusEnum.PRO_CONFIRM.getValue();
                final String orderSize = jsonObject.getString("vol_exec");// 单位btc
                final String orderFees = jsonObject.getString("fee");//手续费，返回单位货币类型
                final String orderValue = jsonObject.getString("cost");//订单金额，单位货币类型
                final String price = jsonObject.getString("price");//单位货币类型
                // 更新Kraken订单表
                DBHelper.getHelper().updateCoinbaseOrder(orderId, price, status, "true", orderSize, orderFees, orderValue);
                if (isBuy) {
                    log.info("Kraken 订单 id_" + orderId + "下单完成,已买到了" + orderSize + cryptoCurrency);
                    // Kraken买币订单成功，提现到地址名
                    withdrawalsAddressName(orderId, orderSize, false, price, cryptoCurrency);
                } else {
                    log.info("Kraken 订单 id_" + orderId + "下单完成,已卖出了" + orderSize + cryptoCurrency);
                }
            } else {
                log.info("Kraken 订单 id_" + orderId + ": 订单未完成，处理中");
            }
        } catch (Exception e) {
            log.info("Kraken 订单异常 :" + e);
        }
    }

    /**
     * 取消订单
     *
     * @param orderId
     */
    private void cancelKrakenOrderById(String orderId, boolean isSell, String transId, String cryptoCurrency) {
        try {
            log.info("======== cancel kraken Order id_" + orderId);

            KrakenAPIClient client = new KrakenAPIClient();
            Map<String, Object> params = new HashMap<>();
            params.put("txid", orderId);
            MyResponse myResponse = client.cancelOrder(params, krakenAllParam.get(cryptoCurrency));
            String cancelStatus = "";
            String message = "";
            if (myResponse.isSuccess()) {
                // 更新coinbasePro订单表
                cancelStatus = OrderStatusEnum.PRO_CANCEL.getValue();
                message = "TIMEOUT CANCEL KRAKEN ORDER!";
            } else {
                cancelStatus = OrderStatusEnum.PRO_FAIL.getValue();
                message = "CANCEL KRAKEN ORDER FAIL ! " + myResponse.getMessage();
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
//                log.info("======== 卖币 cancel kraken Order id_" + orderId + "进行提款操作========");
//                ExchangeOrder exchangeOrder = DBHelper.getHelper().queryCoinbaseOrderByOrderId(orderId);
//                withdrawalsAddressName(orderId, exchangeOrder.getSize(), true, exchangeOrder.getPrice());
//            }
        } catch (Exception e) {
            log.info("Cancel kraken order fail, id=" + orderId + ",message=" + e);
        }
    }

    /**
     * 获取存款地址
     *
     * @return
     */
    public String getDepositAddress(boolean toNew, String cryptoCurrency) {
        KrakenAPIClient client = new KrakenAPIClient();
        Map<String, Object> params = new HashMap<>();
        params.put("asset", ExchangeKrakenEnum.krakenPair.getDesc(cryptoCurrency));
        params.put("method", ExchangeKrakenEnum.krakenMethod.getDesc(cryptoCurrency));
        if (toNew) {
            params.put("new", true);
        }
        try {
            MyResponse result = client.depositAddresses(params, krakenAllParam.get(cryptoCurrency));
            log.info("kraken 存款地址result:" + JSONObject.toJSONString(result));
            if (result.isSuccess()) {
                JSONArray addressArray = JSONObject.parseArray(result.getMessage());
                //如果账号没地址，则创建地址
                if (addressArray.size() > 0) {
                    return addressArray.getJSONObject(0).getString("address");
                } else {
                    log.info("kraken 存款地址，暂无，重新创建一个");
                    return getDepositAddress(true, cryptoCurrency);
                }
            } else {
                log.info("kraken 存款地址 请求不成功");
            }
        } catch (Exception e) {
            log.error("kraken 地址没找到", e);
        }
        return "";
    }

    /**
     * 提取资金到设置的地址名称
     *
     * @param amount
     * @return
     */
    private void withdrawalsAddressName(String orderId, String amount, boolean isCancel, String price, String cryptoCurrency) {

        String transId = DBHelper.getHelper().queryCoinbaseOrderByOrderId(orderId).getTransId();
        BuyLog buyLog = DBHelper.getHelper().queryBuyLogByTransId(transId);

        log.info("======== kraken 提取"+cryptoCurrency+"到地址名称:" + krakenAllParam.get(cryptoCurrency).getKrakenWithdrawalsAddressName() + "  数量:" + amount + cryptoCurrency);
        if (StringUtils.isBlank(krakenAllParam.get(cryptoCurrency).getKrakenWithdrawalsAddressName())) {
            log.info("======== kraken 获取地址名称:krakenWithdrawalsAddressName为空");
            return;
        }
        KrakenAPIClient client = new KrakenAPIClient();
        Map<String, Object> params = new HashMap<>();
        params.put("asset", ExchangeKrakenEnum.krakenPair.getDesc(cryptoCurrency));
        params.put("key", krakenAllParam.get(cryptoCurrency).getKrakenWithdrawalsAddressName());
        params.put("amount", amount);
        String message = isCancel ? "Timeout Cancel order, " : "";
        try {
            MyResponse myResponse = client.withdraw(params, krakenAllParam.get(cryptoCurrency));
            //Test
//            MyResponse myResponse = new MyResponse("{\"refid\":\"AGBJZBE-WAA3TC-ESUQVI\"}");
//            myResponse.setSuccess(true);
            if (myResponse.isSuccess()) {
                log.info(message + "withdrawals to '" + krakenAllParam.get(cryptoCurrency).getKrakenWithdrawalsAddressName() + "' success");
                //添加提款记录
                String refid = JSONObject.parseObject(myResponse.getMessage()).getString("refid");
                TransferLog transferLog = new TransferLog();
                transferLog.setRefid(refid);
                transferLog.setType(TransferTypeEnum.TO_WALLET.getValue());
                transferLog.setTransId(transId);
                transferLog.setTerminalNo(Setting.terminalNo);
                transferLog.setAmount(new BigDecimal(amount).subtract(krakenAllParam.get(cryptoCurrency).getKrakenWithdrawalsFee()).multiply(new BigDecimal(100000000)).setScale(0, BigDecimal.ROUND_UP).toString());
                transferLog.setFee(krakenAllParam.get(cryptoCurrency).getKrakenWithdrawalsFee().multiply(new BigDecimal(100000000)).toString());
                transferLog.setFunds(buyLog.getCash());
                transferLog.setPrice(price);
                transferLog.setWallet(Integer.valueOf(buyLog.getChannel()));
                transferLog.setExchange(ExchangeEnum.KRAKEN.getValue());
                transferLog.setAddress(krakenAllParam.get(cryptoCurrency).getKrakenWithdrawalsAddressName());
                transferLog.setCryptoCurrency(cryptoCurrency);
                DBHelper.getHelper().addTransferLog(transferLog);

                //更新buy表手续费
                String channelFee = buyLog.getChannelFee();
                if (StringUtils.isBlank(channelFee)) {
                    channelFee = "0";
                }
                BigDecimal bigDecimal = new BigDecimal(channelFee);
                BigDecimal add = bigDecimal.add(krakenAllParam.get(cryptoCurrency).getKrakenWithdrawalsFee().multiply(new BigDecimal(100000000)));
                buyLog.setChannelFee(add.setScale(0, BigDecimal.ROUND_UP).toString());
                DBHelper.getHelper().updateBuyLogByTransId(buyLog);
            } else {
                message += ErrorTypeEnum.TO_WALLET.getValue() + "withdrawals to address named '" + krakenAllParam.get(cryptoCurrency).getKrakenWithdrawalsAddressName() + "' fail,"+myResponse.getMessage();
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
            message = "kraken withdrawals to " + krakenAllParam.get(cryptoCurrency).getKrakenWithdrawalsAddressName() + " fail: " + ex.getMessage();
            log.error("kraken 提取地址名称 " + krakenAllParam.get(cryptoCurrency).getKrakenWithdrawalsAddressName() + " failed :", ex);
        }
    }

    /**
     * 获取服务器时间(Get server time)
     */
    public int getTimeDiff() throws Exception {
        KrakenAPIClient client = new KrakenAPIClient();
        try {
            MyResponse result = client.getServerTime();
            if (result.isSuccess()) {
                int krakenTimeDiff = (JSONObject.parseObject(result.getMessage()).getLong("unixtime").intValue()) - (getTimestamp().intValue());
                log.info("kraken 时间差为" + krakenTimeDiff);
                return krakenTimeDiff;
            }
        } catch (Exception e) {
            log.error("serverTime", e);
            throw new Exception("serverTime");
        }
        return 0;
    }

    /**
     * 获取精确到秒的时间戳
     *
     * @return
     */
    public static Long getTimestamp() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        int length = timestamp.length();
        if (length > 3) {
            return Long.valueOf(timestamp.substring(0, length - 3));
        } else {
            return Long.valueOf(0);
        }
    }

    /**
     * 获取pro产品id
     * 默认XBTUSD
     *
     * @return
     */
    private String getProductId(String currencyPair) {
        return ExchangeKrakenEnum.PairEnum.getDesc(currencyPair);
    }


    /**
     * 校验必要参数
     *
     * @return
     */
    private boolean checkParam(String cryptoCurrency) {
        if (StringUtils.isEmpty(cryptoCurrency)){
            log.info("Kraken Incomplete parameters:cryptoCurrency");
            return false;
        }
        boolean result = StringUtils.isBlank(krakenAllParam.get(cryptoCurrency).getKrakenApiKey()) || StringUtils.isBlank(krakenAllParam.get(cryptoCurrency).getKrakenSecret());
        if (result) {
            log.info("Kraken Incomplete parameters");
        }
        return result;
    }


}
