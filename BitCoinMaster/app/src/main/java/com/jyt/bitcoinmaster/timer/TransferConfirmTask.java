package com.jyt.bitcoinmaster.timer;


import com.alibaba.fastjson.JSONObject;
import com.jyt.bitcoinmaster.exchange.Entity.MyResponse;
import com.jyt.bitcoinmaster.exchange.Entity.SellParam;
import com.jyt.bitcoinmaster.exchange.ExchangeFactory;
import com.jyt.bitcoinmaster.exchange.kraken.KrakenService;
import com.jyt.bitcoinmaster.wallet.IWallet;
import com.jyt.bitcoinmaster.wallet.WalletFactory;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransByTxIdRequest;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransByTxIdResult;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransConfirmRequest;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransConfirmResult;
import com.jyt.bitcoinmaster.wallet.entity.SendCoinRequest;
import com.jyt.bitcoinmaster.wallet.entity.SendCoinResult;
import com.jyt.hardware.cashoutmoudle.Sqlite.DBHelper;
import com.jyt.hardware.cashoutmoudle.bean.BuyLog;
import com.jyt.hardware.cashoutmoudle.bean.TransferLog;
import com.jyt.hardware.cashoutmoudle.bean.WithdrawLog;
import com.jyt.hardware.cashoutmoudle.enums.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 定时检查钱包和交易所之前币的转移结果
 * 更新确认状态
 */
public class TransferConfirmTask extends TimerTask {

    private ScheduledExecutorService threadExecutor = Executors.newScheduledThreadPool(3);

    private static Logger log = Logger.getLogger("BitCoinMaster");

    private ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();

    private final int TIMEOUT_RANGE = -6;//h

    @Override
    public void run() {
        try {
            String beforeTimeOut = beforeTimeOut();
            // 定时检查钱包和交易所之前币的转移结果，检查处理中的数据
            List<TransferLog> transferLogs = DBHelper.getHelper().queryTransferLog(beforeTimeOut);
            log.info("检查钱包和交易所之前币的转移,结果:" + transferLogs.size() + "条");
            for (TransferLog transferLog : transferLogs) {
                Object o = concurrentHashMap.putIfAbsent(transferLog.getTransId(), "");
                final String transId = transferLog.getTransId();
                if (o == null) {
                    threadExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            checkConfirm(transId);
                        }
                    });
                } else {
                    log.info("定时检查钱包和交易所之前币的转移结果 交易在缓存中已存在:" + transferLog.getTransId());
                }
            }
        } catch (Exception e) {
            log.error("TransferConfirmTask error :", e);
        }
    }

    /**
     * 判断是否到账
     * 两种：
     * 类型1，toExchange，调用交易所接口查询
     * 类型2, toWallet ，调用钱包接口查询
     *
     * @param transId
     */
    private void checkConfirm(String transId) {
        // 钱包到交易所
        try {
            TransferLog transferLog = DBHelper.getHelper().queryTransferByTransId(beforeTimeOut(), transId);
            log.info("判断是否到账的数据 transferLog：" + JSONObject.toJSONString(transferLog));
            if (transferLog == null) {
                return;
            }

            if (TransferTypeEnum.TO_EXCHANGE.getValue().equals(transferLog.getType())) {
                if(CryptoCurrencyEnum.BTC.getValue().equals(transferLog.getCryptoCurrency())){
                    //coinbase 多查一波txid
                    if (WalletEnum.COINBASE.getValue() == transferLog.getWallet()) {
                        if (StringUtils.isBlank(transferLog.getTxId())) {
                            QueryTransConfirmRequest request = new QueryTransConfirmRequest();
                            request.setTransId(transferLog.getRefid());
                            request.setCryptoCurrency(transferLog.getCryptoCurrency());
                            QueryTransConfirmResult result = WalletFactory.getWallet(WalletEnum.COINBASE.getValue()).queryTransConfirm(request);
                            if (StringUtils.isNotBlank(result.getTxId())) {
                                transferLog.setTxId(result.getTxId());
                                log.info("[check transferLog] coinbase查到txid= " + result.getTxId());
                                DBHelper.getHelper().updateTransferLog(transferLog);
                            } else {
                                log.info("[check transferLog] coinbase未返回txid");
                                return;
                            }
                        }
                    }
                    boolean checkResult = ExchangeFactory.getExchange(transferLog.getExchange()).checkDepositStatus(transferLog);
                    if (checkResult) {
                        log.info("[check transferLog] 钱包到交易所，到账");
                        transferLog.setStatus(TransferStatusEnum.CONFIRM.getValue());
                        boolean updateResult = DBHelper.getHelper().updateTransferLog(transferLog);
                        if (updateResult) {
                            log.info("[check transferLog] 钱包到交易所，到账,更新数据，即将卖币");
                            try {
                                Thread.sleep(30000);//延时30秒再走卖币
                                log.info("延时10s执行卖币");
                            } catch (Exception e) {
                                log.error("延时10s执行卖币", e);
                            }
                            String currency = DBHelper.getHelper().queryWithdrawEixst(transferLog.getTransId()).getCurrency();
                            MyResponse response = ExchangeFactory.getExchange(Integer.valueOf(transferLog.getExchange())).sellOrder(new SellParam(transferLog.getTransId(), transferLog.getTerminalNo(), transferLog.getFunds(), new BigDecimal(transferLog.getAmount()).divide(new BigDecimal(100000000)).toString(), transferLog.getPrice(),transferLog.getCryptoCurrency(),currency));
                        }
                    }
                }else if(CryptoCurrencyEnum.ETH.getValue().equals(transferLog.getCryptoCurrency())){
                    //以太坊目前无法查询，15分钟后默认交易所到账，进行卖币
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date createTime = df.parse(transferLog.getCreateTime());
                    Date nowTime = new Date();
                    Date compareTime = new Date(nowTime.getTime() - 900000);
                    if(createTime.before(compareTime)){
                        log.info("[check transferLog] 以太坊钱包到交易所，已达15分钟 默认到账");
                        transferLog.setStatus(TransferStatusEnum.CONFIRM.getValue());
                        boolean updateResult = DBHelper.getHelper().updateTransferLog(transferLog);
                        if (updateResult) {
                            log.info("[check transferLog] 以太坊钱包到交易所，默认到账,更新数据，即将卖币");
                            String currency = DBHelper.getHelper().queryWithdrawEixst(transferLog.getTransId()).getCurrency();
                            MyResponse response = ExchangeFactory.getExchange(Integer.valueOf(transferLog.getExchange())).sellOrder(new SellParam(transferLog.getTransId(), transferLog.getTerminalNo(), transferLog.getFunds(), new BigDecimal(transferLog.getAmount()).divide(new BigDecimal(100000000)).toString(), transferLog.getPrice(),transferLog.getCryptoCurrency(),currency));
                        }
                    }
                }

            }


            // 交易所到钱包
            if (TransferTypeEnum.TO_WALLET.getValue().equals(transferLog.getType())) {

                if (ExchangeEnum.KRAKEN.getValue() == transferLog.getExchange()) {
                    if (StringUtils.isBlank(transferLog.getTxId())) {
                        TransferLog transfer = ExchangeFactory.getExchange(ExchangeEnum.KRAKEN.getValue()).getWithdrawStatus(transferLog.getRefid(),transferLog.getCryptoCurrency());
                        if (StringUtils.isNotBlank(transfer.getTxId())) {
                            transferLog.setAddress(transfer.getAddress());
                            transferLog.setTxId(transfer.getTxId());
                            transferLog.setFee(transfer.getFee());
                            log.info("[check transferLog] kraken查到txid= " + transfer.getTxId());
                            DBHelper.getHelper().updateTransferLog(transferLog);
                        } else {
                            log.info("[check transferLog] kraken未返回txid");
                            return;
                        }
                    }
                }
                //查询订单交易状态是否完成
                IWallet wallet = WalletFactory.getWallet(transferLog.getWallet());
                QueryTransByTxIdRequest queryTransRequest = new QueryTransByTxIdRequest();
                queryTransRequest.setAddress(transferLog.getAddress());
                queryTransRequest.setTxId(transferLog.getTxId());
                queryTransRequest.setAmount(transferLog.getAmount());
                queryTransRequest.setCryptoCurrency(transferLog.getCryptoCurrency());
                QueryTransByTxIdResult queryTransResult = wallet.queryTransByTxId(queryTransRequest);

                if (CodeMessageEnum.SUCCESS.getCode().equals(queryTransResult.getCode()) && TransferStatusEnum.CONFIRM.getValue().equals(queryTransResult.getStatus() + "")) {
                    //更新transfer
                    DBHelper dbHelper = DBHelper.getHelper();
                    transferLog.setStatus(TransferStatusEnum.CONFIRM.getValue());
                    boolean updateResult = dbHelper.updateTransferLog(transferLog);
                    if (updateResult) {
                        //完成发送比特币给客户
                        BuyLog buyLog = dbHelper.queryBuyLogByTransId(transferLog.getTransId());

                        if (ExchangeStrategyEnum.NO_WALLET.getValue() == buyLog.getExchangeStrategy() && (TranStatusEnum.PENDING.getValue()+"").equals(buyLog.getStatus()) ) {
                            //交易策略为2，才进行转币给客户。
                            SendCoinRequest sendCoinRequest = new SendCoinRequest();
                            sendCoinRequest.setReceiveAddress(buyLog.getAddress());
                            sendCoinRequest.setAmount(buyLog.getAmount());
                            sendCoinRequest.setCryptoCurrency(buyLog.getCryptoCurrency());
                            SendCoinResult sendCoinResult = wallet.sendCoin(sendCoinRequest);
                            if (CodeMessageEnum.SUCCESS.getCode().equals(sendCoinResult.getCode())) {
                                // 分销
                                UploadTimer.agencyProfit(buyLog.getTransId(),"buy");

                                //更新buy表
                                String channelFee = buyLog.getChannelFee();
                                if (StringUtils.isBlank(channelFee)) {
                                    channelFee = "0";
                                }
                                BigDecimal add = new BigDecimal(channelFee).add(new BigDecimal(sendCoinResult.getFee()));
                                buyLog.setChannelFee(add.setScale(0, BigDecimal.ROUND_UP).toString());
                                buyLog.setChannelTransId(sendCoinResult.getTransId());
                                buyLog.setStatus(TranStatusEnum.CONFIRM.getValue() + "");
                            } else {
                                buyLog.setStatus(TranStatusEnum.FAIL.getValue() + "");
                                buyLog.setRemark(ErrorTypeEnum.TO_TRAN.getValue() + sendCoinResult.getMessage());
                            }
                            dbHelper.updateBuyLogByTransId(buyLog);
                        }
                    }
                }

            }
        } catch (Exception e) {
            log.error("定时检查钱包和交易所之前币的转移结果 fail", e);
        } finally {
            concurrentHashMap.remove(transId);
            log.info("concurrentHashMap remove transId: "+transId);
        }
    }


    private String dateToString(Date dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(dateStr);
    }

    private String beforeTimeOut() {
        String beforeTime = dateToString(getTimeOutDateTime(new Date()));
        return beforeTime;
    }

    private Date getTimeOutDateTime(Date transTime) {
        Calendar c = Calendar.getInstance();
        c.setTime(transTime);
        c.add(Calendar.HOUR_OF_DAY, TIMEOUT_RANGE);
        return c.getTime();
    }
}
