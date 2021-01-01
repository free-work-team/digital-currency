package com.jyt.bitcoinmaster.exchange;

import com.jyt.bitcoinmaster.exchange.Entity.BuyParam;
import com.jyt.bitcoinmaster.exchange.Entity.InitParam;
import com.jyt.bitcoinmaster.exchange.Entity.MyResponse;
import com.jyt.bitcoinmaster.exchange.Entity.SellParam;
import com.jyt.hardware.cashoutmoudle.bean.TransferLog;

public class DefaultExchange implements IExchange {

    @Override
    public boolean init(InitParam param) {
        return false;
    }

    @Override
    public MyResponse buyOrder(BuyParam param) {
        return new MyResponse();
    }

    @Override
    public MyResponse sellOrder(SellParam param) {
        return new MyResponse();
    }

    @Override
    public void checkOrderFinish(String orderId, boolean isBuy ,String cryptoCurrency ) {

    }

    @Override
    public void cancelOrderById(String orderId, boolean isSell, String transId,String cryptoCurrency) {

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
    public TransferLog getWithdrawStatus(String refid,String cryptoCurrency) {
        return new TransferLog();
    }
}
