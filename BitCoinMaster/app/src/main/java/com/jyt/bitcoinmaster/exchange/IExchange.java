package com.jyt.bitcoinmaster.exchange;

import com.jyt.bitcoinmaster.exchange.Entity.BuyParam;
import com.jyt.bitcoinmaster.exchange.Entity.InitParam;
import com.jyt.bitcoinmaster.exchange.Entity.MyResponse;
import com.jyt.bitcoinmaster.exchange.Entity.SellParam;
import com.jyt.hardware.cashoutmoudle.bean.TransferLog;

public interface IExchange {

    public String currencyPair ="";

    /**
     * 初始化参数
     *
     * @param param
     */
    boolean init(InitParam param);

    /**
     * 买币
     *
     * @param param
     */
    MyResponse buyOrder(BuyParam param);

    /**
     * 卖币
     *
     * @param param
     */
    MyResponse sellOrder(SellParam param);


    /**
     * 检查交易所订单完成否
     *
     * @param orderId
     * @param isBuy
     */
    void checkOrderFinish(String orderId, boolean isBuy , String cryptoCurrency);

    /**
     * 取消交易所订单
     * @param orderId
     * @param isSell
     */
    void cancelOrderById(String orderId, boolean isSell, String transId, String cryptoCurrency);


    /**
     * 查询存款地址,用于钱包 to 交易所
     */
    String getDepositAddress(String cryptoCurrency);

    /**
     * 查询存款是否到账
     */
    boolean checkDepositStatus(TransferLog transferLog);

    /**
     * 查询提款状态
     */
    TransferLog getWithdrawStatus(String refid,String cryptoCurrency);
}
