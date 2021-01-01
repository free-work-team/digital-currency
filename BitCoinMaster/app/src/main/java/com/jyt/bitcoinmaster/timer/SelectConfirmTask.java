package com.jyt.bitcoinmaster.timer;


import com.jyt.bitcoinmaster.wallet.IWallet;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransConfirmRequest;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransConfirmResult;
import com.jyt.hardware.cashoutmoudle.enums.CodeMessageEnum;
import com.jyt.hardware.cashoutmoudle.enums.CryptoCurrencyEnum;
import com.jyt.hardware.cashoutmoudle.enums.TranStatusEnum;
import com.jyt.bitcoinmaster.wallet.WalletFactory;
import com.jyt.hardware.cashoutmoudle.Sqlite.DBHelper;
import com.jyt.hardware.cashoutmoudle.bean.WithdrawLog;
import com.jyt.hardware.cashoutmoudle.enums.ConfirmStatusEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 定时任务查询用户转到商户的币 是否达到设置里设置的确认数
 * 更新确认状态
 */
public class SelectConfirmTask extends TimerTask {

    private ScheduledExecutorService threadExecutor = Executors.newScheduledThreadPool(3);

    private static Logger log = Logger.getLogger("BitCoinMaster");

    @Override
    public void run() {
        try {
            // 检查确认数,确认状态为未确认，交易状态为处理中的数据
            ArrayList<WithdrawLog> withdrawlogs = DBHelper.getHelper().queryWithdrawLogByConfirmStatus(ConfirmStatusEnum.NO_CONFIRM.getValue(), "");
            log.info("检查卖币订单确认数,结果:" + withdrawlogs.size() + "条");
            for (final WithdrawLog withdrawlog : withdrawlogs) {
                if (StringUtils.isNotBlank(withdrawlog.getTransId())) {
                    threadExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                selectConfirm(withdrawlog);
                            } catch (Exception e) {
                                log.error("SelectConfirmTask", e);
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            log.info("SelectConfirmTask error :" + e);
        }
    }

    private void selectConfirm(WithdrawLog withdrawlog) {
        if(CryptoCurrencyEnum.BTC.getValue().equals(withdrawlog.getCryptoCurrency())){
            // 判断地址，和数量和确认数
            if(WalletFactory.checkAddress(withdrawlog.getTargetAddress(),withdrawlog.getAmount(),withdrawlog.getSellType())){
                DBHelper.getHelper().updateWithdrawToConfirmStatus(withdrawlog.getTransId());
                log.info("完成比特币检查订单确认数,地址:" + withdrawlog.getTargetAddress()+ " ,达到"+withdrawlog.getSellType()+" 确认,更新确认状态为 已确认");
            }
        }else if(CryptoCurrencyEnum.ETH.getValue().equals(withdrawlog.getCryptoCurrency())){
            //发送渠道
            IWallet wallet = WalletFactory.getWallet(Integer.valueOf(withdrawlog.getChannel()));
            QueryTransConfirmRequest request = new QueryTransConfirmRequest();
            request.setTransId(withdrawlog.getChannelTransId());
            request.setCryptoCurrency(withdrawlog.getCryptoCurrency());
            QueryTransConfirmResult transResult = wallet.queryTransConfirm(request);
            if(transResult.getCode().equals(CodeMessageEnum.SUCCESS.getCode()) && transResult.getStatus() == TranStatusEnum.CONFIRM.getValue()){
                DBHelper.getHelper().updateWithdrawToConfirmStatus(withdrawlog.getTransId());
                log.info("完成以太坊检查订单确认数,地址:" + withdrawlog.getTargetAddress()+ "，更新确认状态为 已确认");
            }
        }
    }
}
