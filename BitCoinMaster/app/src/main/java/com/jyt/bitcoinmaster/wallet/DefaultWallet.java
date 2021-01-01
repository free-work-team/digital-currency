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

import org.apache.log4j.Logger;

import java.math.BigDecimal;


/**
 * 默认钱包接口
 */
public class DefaultWallet implements IWallet {

    private static Logger log = Logger.getLogger("BitCoinMaster");



    private static DefaultWallet defaultWallet;

    public static DefaultWallet getInstance() {

            if (null == defaultWallet) {
                synchronized (DefaultWallet.class) {
                    if(null == defaultWallet) {
                        defaultWallet = new DefaultWallet();
                    }
                }
            }
        return defaultWallet;
    }
    @Override
    public InitResult init(InitRequest request) {
        return null;
    }

    @Override
    public SendCoinResult sendCoin(SendCoinRequest sendCoinRequest) {
        return  null;
    }

    @Override
    public QueryTransCreateResult queryTransCreate(QueryTransCreateRequest request) {
        return null;
    }

    @Override
    public QueryTransConfirmResult queryTransConfirm(QueryTransConfirmRequest request) {
        return null;
    }

    @Override
    public QueryTransByTxIdResult queryTransByTxId(QueryTransByTxIdRequest request) {
        return null;
    }

    @Override
    public CreateAddressResult createAddress(CreateAddressRequest request) {
       return null;
    }

    @Override
    public QueryBalanceResult queryBalance(QueryBalanceRequest request) {
        return null;
    }

    @Override
    public QueryPriceResult queryMarketPrice(QueryPriceRequest request) {
        return null;
    }

    @Override
    public SendExchangeResult sendExchangeByCreate(WithdrawLog withdrawLog) {
        return  null;
    }

    @Override
    public SendExchangeResult sendExchangeByConfirm(WithdrawLog withdrawLog) {
        return  null;
    }

    @Override
    public BigDecimal getReserveFee(String cryptoCurrency) {
        return new BigDecimal(0);
    }


}
