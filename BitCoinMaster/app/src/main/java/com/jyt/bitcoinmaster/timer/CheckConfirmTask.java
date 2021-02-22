package com.jyt.bitcoinmaster.timer;

import com.alibaba.fastjson.JSONObject;
import com.jyt.bitcoinmaster.exchange.ExchangeFactory;
import com.jyt.bitcoinmaster.statics.Setting;
import com.jyt.bitcoinmaster.wallet.entity.SendExchangeResult;
import com.jyt.hardware.cashoutmoudle.bean.CryptoSettings;
import com.jyt.hardware.cashoutmoudle.bean.TransferLog;
import com.jyt.hardware.cashoutmoudle.enums.CodeMessageEnum;
import com.jyt.hardware.cashoutmoudle.enums.ErrorTypeEnum;
import com.jyt.hardware.cashoutmoudle.enums.ExchangeEnum;
import com.jyt.hardware.cashoutmoudle.enums.ExchangeProEnum;
import com.jyt.hardware.cashoutmoudle.enums.TranStatusEnum;
import com.jyt.bitcoinmaster.wallet.IWallet;
import com.jyt.bitcoinmaster.wallet.WalletFactory;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransConfirmRequest;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransConfirmResult;
import com.jyt.hardware.cashoutmoudle.Sqlite.DBHelper;
import com.jyt.hardware.cashoutmoudle.bean.WithdrawLog;
import com.jyt.hardware.cashoutmoudle.enums.TransferStatusEnum;
import com.jyt.hardware.cashoutmoudle.enums.TransferTypeEnum;
import com.jyt.hardware.cashoutmoudle.enums.WalletEnum;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 检查bitgo/coinbase 的订单是否完成,更新数据库为confirm
 */
public class CheckConfirmTask extends TimerTask {

    private ScheduledExecutorService threadExecutor = Executors.newScheduledThreadPool(3);

    private static Logger log = Logger.getLogger("BitCoinMaster");

    @Override
    public void run() {
        try {
            //单向跳过卖币流程
            if (Setting.way == null || Setting.way != 2) {
                return;
            }
            // 检查是否完成订单
            ArrayList<WithdrawLog> withdrawlogs = DBHelper.getHelper().queryWithdrawLogByTransStatus(Integer.valueOf(TranStatusEnum.PENDING.getValue()).toString(), "");
            log.info("检查卖币是否到账,结果:" + withdrawlogs.size() + "条");
            for (final WithdrawLog withdrawlog : withdrawlogs) {
                if (StringUtils.isNotBlank(withdrawlog.getTransId())) {
                    threadExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                checkByTransId(withdrawlog);
                            } catch (Exception e) {
                                log.error("CheckConfirmTask", e);
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            log.info("checkTransaction error :" , e);
        }
    }





    private void checkByTransId(WithdrawLog withdrawlog) {
        CryptoSettings cryptoSetting = Setting.cryptoSettings.getCryptoSetting(withdrawlog.getCryptoCurrency());

        //发送渠道
        IWallet wallet = WalletFactory.getWallet(Integer.valueOf(withdrawlog.getChannel()));
        QueryTransConfirmRequest request = new QueryTransConfirmRequest();
        request.setTransId(withdrawlog.getChannelTransId());
        request.setCryptoCurrency(withdrawlog.getCryptoCurrency());
        QueryTransConfirmResult transResult = wallet.queryTransConfirm(request);
        if(transResult.getCode().equals(CodeMessageEnum.SUCCESS.getCode()) && transResult.getStatus() == TranStatusEnum.CONFIRM.getValue()){
            boolean confirmResult=DBHelper.getHelper().updateWithdrawToConfirm(withdrawlog.getTransId(), TranStatusEnum.CONFIRM.getValue());
            // 分销
            if (confirmResult) {
                UploadTimer.agencyProfit(withdrawlog.getTransId(),"sell");
            }
            log.info("transId："+withdrawlog.getTransId()+" 的订单 已确认!");
            //订单已确认，钱已到账，可取款，（保险）
            DBHelper.getHelper().updateWithdrawToConfirmStatus(withdrawlog.getTransId());

            SendExchangeResult sendExchangeResult = wallet.sendExchangeByConfirm(withdrawlog);
            if(CodeMessageEnum.SUCCESS.getCode().equals(sendExchangeResult.getCode())){
                if(ExchangeEnum.COINBASEPRO.getValue() != cryptoSetting.getExchange()){
                    TransferLog transferLog = new TransferLog();
                    transferLog.setTxId(sendExchangeResult.getTxId());
                    transferLog.setTransId(withdrawlog.getTransId());
                    transferLog.setTerminalNo(Setting.terminalNo);
                    transferLog.setFunds(withdrawlog.getCash());
                    transferLog.setAmount(sendExchangeResult.getActualAmount());
                    String orderType = JSONObject.parseObject(cryptoSetting.getChannelParam()).getString("order_type");
                    if (ExchangeProEnum.OrderTypeEnum.LIMIT.getValue().equals(orderType)){
                        transferLog.setPrice(withdrawlog.getExchangeRate());
                    }
                    transferLog.setFee(sendExchangeResult.getFee());
                    transferLog.setType(TransferTypeEnum.TO_EXCHANGE.getValue());
                    transferLog.setWallet(Integer.valueOf(withdrawlog.getChannel()));
                    transferLog.setExchange(Integer.valueOf(withdrawlog.getStrategy()));
                    transferLog.setAddress(ExchangeFactory.getExchange(cryptoSetting.getExchange()).getDepositAddress(withdrawlog.getCryptoCurrency()));
                    transferLog.setRefid(sendExchangeResult.getTransId());
                    transferLog.setCryptoCurrency(withdrawlog.getCryptoCurrency());
                    DBHelper.getHelper().addTransferLog(transferLog);

                    //修改渠道手续费
                    DBHelper.getHelper().updateWithdrawFee(withdrawlog.getTransId(),sendExchangeResult.getFee());
                }
            }else if(CodeMessageEnum.FAIL.getCode().equals(sendExchangeResult.getCode())){
                //钱包发送交易所失败，更新提现表状态
                DBHelper.getHelper().updateWithdrawRemark(withdrawlog.getTransId(), ErrorTypeEnum.TO_EXCHANGE.getValue() + sendExchangeResult.getMessage(),TranStatusEnum.ERROR.getValue());
            }

        }
    }
}
