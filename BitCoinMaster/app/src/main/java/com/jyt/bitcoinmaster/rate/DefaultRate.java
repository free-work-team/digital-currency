package com.jyt.bitcoinmaster.rate;


import com.jyt.bitcoinmaster.rate.entity.QueryPriceRequest;
import com.jyt.bitcoinmaster.rate.entity.QueryPriceResult;

public class DefaultRate implements IRate {
    @Override
    public QueryPriceResult queryMarketPrice(QueryPriceRequest request) {
        return new QueryPriceResult();
    }
}
