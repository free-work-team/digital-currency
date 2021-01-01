package com.jyt.bitcoinmaster.rate;

import com.jyt.bitcoinmaster.rate.coinbase.CoinbaseService;
import com.jyt.hardware.cashoutmoudle.enums.RateSourceEnum;

/**
 * 汇率的工厂
 */
public class RateFactory {

    /**
     * 生成汇率对象
     *
     * @param rateSource
     * @return
     */
    public static IRate getRate(Integer rateSource) {
        IRate iRate = null;
        RateSourceEnum exchange = RateSourceEnum.getEnum(rateSource);
        switch (exchange) {
            case COINBASE:
                iRate = CoinbaseService.getInstance();
                break;
            default:
                iRate = new DefaultRate();
                break;
        }
        return iRate;
    }

}
