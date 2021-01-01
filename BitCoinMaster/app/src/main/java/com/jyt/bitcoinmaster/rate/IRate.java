package com.jyt.bitcoinmaster.rate;


import com.jyt.bitcoinmaster.rate.entity.QueryPriceRequest;
import com.jyt.bitcoinmaster.rate.entity.QueryPriceResult;

public interface IRate {

    QueryPriceResult queryMarketPrice(QueryPriceRequest request);
}
