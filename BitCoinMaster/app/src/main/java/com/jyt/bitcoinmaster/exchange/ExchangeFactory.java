package com.jyt.bitcoinmaster.exchange;

import com.jyt.bitcoinmaster.exchange.coinbasePro.CoinbaseProService;
import com.jyt.bitcoinmaster.exchange.kraken.KrakenService;
import com.jyt.hardware.cashoutmoudle.enums.ExchangeEnum;

public class ExchangeFactory {



    /**
     * 生成渠道对象
     *
     * @param strategy
     * @return
     */
    public static IExchange getExchange(Integer strategy){
        IExchange iexchange = null;
        ExchangeEnum exchange = ExchangeEnum.getEnum(strategy);
        switch (exchange) {
            case NO:
                iexchange = new DefaultExchange();
                break;
            case COINBASEPRO:
                 iexchange = CoinbaseProService.getInstance();
                break;
            case KRAKEN:
                iexchange = KrakenService.getInstance();
                break;
            default:
              break;
        }
        return iexchange;
    }

}
