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
import com.jyt.bitcoinmaster.wallet.entity.QueryTransCreateRequest;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransCreateResult;
import com.jyt.hardware.cashoutmoudle.Sqlite.DBHelper;
import com.jyt.hardware.cashoutmoudle.bean.WithdrawLog;
import com.jyt.hardware.cashoutmoudle.enums.TransferStatusEnum;
import com.jyt.hardware.cashoutmoudle.enums.TransferTypeEnum;
import com.jyt.hardware.cashoutmoudle.enums.WalletEnum;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 通过btc地址检查2小时内订单是否创建（支付）
 */
public class CheckPendingTask extends TimerTask {

    private ScheduledExecutorService threadExecutor = Executors.newScheduledThreadPool(3);

    private static Logger log = Logger.getLogger("BitCoinMaster");

    private final int TIMEOUT_RANGE = -2;//h 最近发现最长 1h 23min 开始确认

    @Override
    public void run() {
        String beforeTimeOut = beforeTimeOut();
        try {
            //单向跳过卖币流程
            if (Setting.way == null || Setting.way != 2) {
                return;
            }
            //查寻是否创建订单
            ArrayList<WithdrawLog> withdrawlogs = DBHelper.getHelper().queryWithdrawLogByTransStatus(Integer.valueOf(TranStatusEnum.INIT.getValue()).toString(), beforeTimeOut);
            log.info("查询卖币是否创建订单,结果:" + withdrawlogs.size() + "条,时间条件:" + beforeTimeOut + "至现在");
            for (final WithdrawLog withdrawlog : withdrawlogs) {
                if (StringUtils.isNotBlank(withdrawlog.getTargetAddress())) {
                    threadExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                checkByAddress(withdrawlog);
                            } catch (Exception e) {
                                log.error("CheckPendingTask", e);
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            log.info("checkTransactionPending error :" , e);
        }
    }

    /**
     * bitgo/coinbase 通过地址查看是否支付
     *
     * @param withdrawlog
     */
    private void checkByAddress(WithdrawLog withdrawlog) {
        CryptoSettings cryptoSetting = Setting.cryptoSettings.getCryptoSetting(withdrawlog.getCryptoCurrency());
        //发送钱包查询
        IWallet wallet = WalletFactory.getWallet(Integer.valueOf(withdrawlog.getChannel()));
        QueryTransCreateRequest request = new QueryTransCreateRequest();
        request.setWithdrawLog(withdrawlog);
        request.setCryptoCurrency(withdrawlog.getCryptoCurrency());
        QueryTransCreateResult transResult = wallet.queryTransCreate(request);
        if(CodeMessageEnum.SUCCESS.getCode().equals(transResult.getCode())){
            DBHelper.getHelper().updateWithdrawNew(withdrawlog.getTargetAddress(), transResult.getTransId(), TranStatusEnum.PENDING.getValue(), "", 0, 0);
            log.info("地址："+withdrawlog.getTargetAddress()+" 的订单 已创建! ");

            SendExchangeResult sendExchangeResult = wallet.sendExchangeByCreate(withdrawlog);
            if(CodeMessageEnum.SUCCESS.getCode().equals(sendExchangeResult.getCode())){
                if(ExchangeEnum.COINBASEPRO.getValue() !=Integer.valueOf(withdrawlog.getStrategy())){
                    TransferLog transferLog = new TransferLog();
                    transferLog.setTxId(sendExchangeResult.getTxId());
                    transferLog.setTransId(withdrawlog.getTransId());
                    transferLog.setTerminalNo(Setting.terminalNo);
                    transferLog.setFunds(withdrawlog.getCash());
                    transferLog.setAmount(sendExchangeResult.getActualAmount());
                    String orderType = JSONObject.parseObject(cryptoSetting.getChannelParam()).getString("order_type");
                    if (orderType == ExchangeProEnum.OrderTypeEnum.LIMIT.getValue()){
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
