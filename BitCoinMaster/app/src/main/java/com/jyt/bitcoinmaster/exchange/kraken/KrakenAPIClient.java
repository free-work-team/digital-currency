package com.jyt.bitcoinmaster.exchange.kraken;

import com.jyt.bitcoinmaster.exchange.Entity.MyResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Kraken API client
 *
 * @author Stéphane Bouclier
 */
public class KrakenAPIClient {

    public static String BASE_URL = "https://api.kraken.com";


    /**
     * Get server time
     *
     * @return server time
     */
    public MyResponse getServerTime() {
        HttpApiClient client = new HttpApiClient();
        return client.callPublic(BASE_URL, KrakenApiMethod.SERVER_TIME);
    }

    /**
     * Get assets information
     *
     * @return assets information
     */
    public MyResponse getAssetsInformation() {
        HttpApiClient client = new HttpApiClient();
        return client.callPublic(BASE_URL, KrakenApiMethod.ASSET_INFORMATION);
    }

    /**
     * Get assets information
     *
     * @param asset to retrieve information
     * @return assets information
     */
    public MyResponse getAssetsInformation(String asset) {
        HttpApiClient client = new HttpApiClient();

        Map<String, String> params = new HashMap<>();
        params.put("asset", asset);

        return client.callPublic(BASE_URL, KrakenApiMethod.ASSET_INFORMATION, params);
    }

    /**
     * Get tradable asset pairs
     *
     * @return asset pairs
     */
    public MyResponse getAssetPairs() {
        HttpApiClient client = new HttpApiClient();
        return client.callPublic(BASE_URL, KrakenApiMethod.ASSET_PAIRS);
    }


    /**
     * Get ticker information of pairs
     *
     * @param pair list of pair
     * @return ticker information
     */
    public MyResponse getTickerInformation(String pair) {
        HttpApiClient client = new HttpApiClient();

        Map<String, String> params = new HashMap<>();
        params.put("pair", pair);
        MyResponse tickerInformationResult = client.callPublic(BASE_URL, KrakenApiMethod.TICKER_INFORMATION, params);
        return tickerInformationResult;
    }

    /**
     * Get order book
     *
     * @param pair  asset pair
     * @param count maximum number of asks/bids
     * @return order book
     */
    public MyResponse getOrderBook(String pair, Integer count) {
        HttpApiClient client = new HttpApiClient();

        Map<String, String> params = new HashMap<>();
        params.put("pair", pair);
        params.put("count", String.valueOf(count));

        return client.callPublic(BASE_URL, KrakenApiMethod.ORDER_BOOK, params);
    }

    /**
     * Get order book
     *
     * @param pair asset pair
     * @return order book
     */
    public MyResponse getOrderBook(String pair) {
        HttpApiClient client = new HttpApiClient();

        Map<String, String> params = new HashMap<>();
        params.put("pair", pair);

        return client.callPublic(BASE_URL, KrakenApiMethod.ORDER_BOOK, params);
    }

    /**
     * Get recent trades
     *
     * @param pair asset pair
     * @return recent trades
     */
    public MyResponse getRecentTrades(String pair) {
        HttpApiClient client = new HttpApiClient();

        Map<String, String> params = new HashMap<>();
        params.put("pair", pair);

        return client.callPublicWithLastId(BASE_URL, KrakenApiMethod.RECENT_TRADES, params);
    }

    /**
     * Get recent trades
     *
     * @param pair  asset pair
     * @param since return trade data since given id
     * @return recent trades
     */
    public MyResponse getRecentTrades(String pair, Integer since) {
        HttpApiClient client = new HttpApiClient();

        Map<String, String> params = new HashMap<>();
        params.put("pair", pair);
        params.put("since", String.valueOf(since));

        return client.callPublicWithLastId(BASE_URL, KrakenApiMethod.RECENT_TRADES, params);
    }

    /**
     * Get recent spreads
     *
     * @param pair asset pair
     * @return recent spreads
     */
    public MyResponse getRecentSpreads(String pair) {
        HttpApiClient client = new HttpApiClient();

        Map<String, String> params = new HashMap<>();
        params.put("pair", pair);

        return client.callPublicWithLastId(BASE_URL, KrakenApiMethod.RECENT_SPREADS, params);
    }

    /**
     * Get recent spreads
     *
     * @param pair  asset pair
     * @param since return spreads since given id
     * @return recent spreads
     */
    public MyResponse getRecentSpreads(String pair, Integer since) {
        HttpApiClient client = new HttpApiClient();

        Map<String, String> params = new HashMap<>();
        params.put("pair", pair);
        params.put("since", String.valueOf(since));

        return client.callPublicWithLastId(BASE_URL, KrakenApiMethod.RECENT_SPREADS, params);
    }

    /**
     * Get account balance
     *
     * @return map of pair/balance
     */
    public MyResponse getAccountBalance(KrakenParam krakenParam) {
        HttpApiClient client = new HttpApiClient();
        return client.callPrivate(BASE_URL, KrakenApiMethod.ACCOUNT_BALANCE,krakenParam);
    }

    /**
     * Get tradable balance
     *
     * @return trade balance
     */
    public MyResponse getTradeBalance(KrakenParam krakenParam) {
        HttpApiClient client = new HttpApiClient();
        return client.callPrivate(BASE_URL, KrakenApiMethod.TRADE_BALANCE,krakenParam);
    }

    /**
     * Get open orders
     *
     * @return open orders
     */
    public MyResponse getOpenOrders(KrakenParam krakenParam) {
        HttpApiClient client = new HttpApiClient();
        return client.callPrivate(BASE_URL, KrakenApiMethod.OPEN_ORDERS,krakenParam);
    }

    /**
     * Get closed orders
     *
     * @return closed orders
     */
    public MyResponse getClosedOrders(KrakenParam krakenParam) {
        HttpApiClient client = new HttpApiClient();
        return client.callPrivate(BASE_URL, KrakenApiMethod.CLOSED_ORDERS,krakenParam);
    }

    /**
     * Get trades history
     *
     * @return trades history
     */
    public MyResponse getTradesHistory(KrakenParam krakenParam) {
        HttpApiClient client = new HttpApiClient();
        return client.callPrivate(BASE_URL, KrakenApiMethod.TRADES_HISTORY,krakenParam);
    }

    /**
     * Get ledgers information
     *
     * @return ledgers information
     */
    public MyResponse getLedgersInformation(KrakenParam krakenParam) {
        HttpApiClient client = new HttpApiClient();
        return client.callPrivate(BASE_URL, KrakenApiMethod.LEDGERS_INFORMATION,krakenParam);
    }

    /**
     * Get trade volume
     *
     * @return trade volume
     */
    public MyResponse getTradeVolume(KrakenParam krakenParam) {
        HttpApiClient client = new HttpApiClient();
        return client.callPrivate(BASE_URL, KrakenApiMethod.TRADE_VOLUME,krakenParam);
    }

    /**
     * AddOrder
     *
     * @param params
     * @return
     */
    public MyResponse postAddOrder(Map<String, Object> params,KrakenParam krakenParam) {
        HttpApiClient client = new HttpApiClient();
        return client.callPrivate(BASE_URL, KrakenApiMethod.ADD_ORDER, params,krakenParam);
    }

    /**
     * QueryOrders
     *
     * @param params
     * @return
     */
    public MyResponse queryOrders(Map<String, Object> params,KrakenParam krakenParam) {
        HttpApiClient client = new HttpApiClient();
        return client.callPrivate(BASE_URL, KrakenApiMethod.QUERYORDERS, params,krakenParam);
    }

    /**
     * cancelOrder
     *
     * @param params
     * @return
     */
    public MyResponse cancelOrder(Map<String, Object> params,KrakenParam krakenParam) {
        HttpApiClient client = new HttpApiClient();
        return client.callPrivate(BASE_URL, KrakenApiMethod.CANCELORDER, params,krakenParam);
    }

    /**
     * Withdraw
     *
     * @param params
     * @return
     */
    public MyResponse withdraw(Map<String, Object> params,KrakenParam krakenParam) {
        HttpApiClient client = new HttpApiClient();
        return client.callPrivate(BASE_URL, KrakenApiMethod.WITHDRAW, params,krakenParam);
    }

    /**
     * withdrawInfo
     *
     * @param params
     * @return
     */
    public MyResponse withdrawInfo(Map<String, Object> params,KrakenParam krakenParam) {
        HttpApiClient client = new HttpApiClient();
        return client.callPrivate(BASE_URL, KrakenApiMethod.WITHDRAWINFO, params,krakenParam);
    }

    /**
     * WithdrawStatus
     *
     * @param params
     * @return
     */
    public MyResponse withdrawStatus(Map<String, Object> params,KrakenParam krakenParam) {
        HttpApiClient client = new HttpApiClient();
        return client.callPrivate(BASE_URL, KrakenApiMethod.WITHDRAWSTATUS, params,krakenParam);
    }

    /**
     * depositMethods
     *
     * @param params
     * @return
     */
    public MyResponse depositMethods(Map<String, Object> params,KrakenParam krakenParam) {
        HttpApiClient client = new HttpApiClient();
        return client.callPrivate(BASE_URL, KrakenApiMethod.DEPOSITMETHODS, params,krakenParam);
    }

    /**
     * depositAddresses
     *
     * @param params
     * @return
     */
    public MyResponse depositAddresses(Map<String, Object> params,KrakenParam krakenParam) {
        HttpApiClient client = new HttpApiClient();
        return client.callPrivate(BASE_URL, KrakenApiMethod.DEPOSITADDRESSES, params,krakenParam);
    }

    /**
     * depositStatus
     *
     * @param params
     * @return
     */
    public MyResponse depositStatus(Map<String, Object> params,KrakenParam krakenParam) {
        HttpApiClient client = new HttpApiClient();
        return client.callPrivate(BASE_URL, KrakenApiMethod.DEPOSITSTATUS, params,krakenParam);
    }
}
