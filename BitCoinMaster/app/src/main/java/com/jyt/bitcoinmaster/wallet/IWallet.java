package com.jyt.bitcoinmaster.wallet;

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
import com.jyt.bitcoinmaster.wallet.entity.QueryTransByTxIdRequest;
import com.jyt.bitcoinmaster.wallet.entity.QueryTransByTxIdResult;
import com.jyt.bitcoinmaster.wallet.entity.SendCoinRequest;
import com.jyt.bitcoinmaster.wallet.entity.SendCoinResult;
import com.jyt.bitcoinmaster.wallet.entity.SendExchangeResult;
import com.jyt.hardware.cashoutmoudle.bean.WithdrawLog;

import java.math.BigDecimal;

public interface IWallet {

    /**
     * 初始化参数
     * @param param
     */
    InitResult init(InitRequest param);

    /**
     * 交易
     * @param request
     */
   SendCoinResult sendCoin(SendCoinRequest request);

    /**
     * 查询交易是否创建
     * @param request
     */
    QueryTransCreateResult queryTransCreate(QueryTransCreateRequest request);

    /**
     * 查询交易是否到账
     * @param request
     */
    QueryTransConfirmResult queryTransConfirm(QueryTransConfirmRequest request);

    /**
     * 根据txid(hash)查询交易记录
     * @param request
     */
    QueryTransByTxIdResult queryTransByTxId(QueryTransByTxIdRequest request);

    /**
     * 创建地址
     */
    CreateAddressResult createAddress(CreateAddressRequest request);

    /**
     * 查询账户余额
     */
    QueryBalanceResult queryBalance(QueryBalanceRequest request);

    /**
     * 查询市场价格
     * @param request
     * @return
     */
    QueryPriceResult queryMarketPrice(QueryPriceRequest request);

    /**
     * 发送比特币到交易所(策略1)
     * @param withdrawLog
     * @return
     */
    SendExchangeResult sendExchangeByCreate(WithdrawLog withdrawLog);

    /**
     * 发送比特币到交易所(策略2)
     * @param withdrawLog
     * @return
     */
    SendExchangeResult sendExchangeByConfirm(WithdrawLog withdrawLog);

    /**
     * 获取预留旷工费
     * @param
     * @return
     */
    BigDecimal getReserveFee(String cryptoCurrency);


}
