package com.jyt.bitcoinmaster.statics;


import com.jyt.hardware.cashoutmoudle.bean.Crypto;
import com.jyt.hardware.cashoutmoudle.bean.CryptoSettings;

public class Setting {

    public static String webAddress;
    public static String terminalNo;
    public static String password;
    public static String merchantName;
    public static String hotline;
    public static String email;
    public static String currency;
    public static String online;
    public static String limitCash;
    public static String kycUrl;
    public static Integer way = 2;//default
    public static Integer kycEnable;
    public static Crypto cryptoSettings;

    //public static Integer rateSource;
    //public static String buyTransactionFee;
    //public static String sellTransactionFee;
    //public static String buySingleFee;
    //public static String sellSingleFee;
    //public static String minNeedBitcoin;
    //public static String minNeedCash;
    //public static String rate;
    //public static String sellType;
    //public static String channel; // 钱包
    //public static Integer hotWallet;// 交易所
   // public static String currencyPair;// 交易所交易对
    //public static String orderType;// 下单类型 limit market
    //public static JSONObject channelParam;

    /**
     *  0.不使用交易所，从热钱包发送和接收币；
     *  1.热钱包预存币，从热钱包发送和接收币，然后在交易所买卖币，如果购买成功，把这些币发送到热钱包；
     *  2.热钱包不预存币，直接在交易所买卖币。
     */
    //public static Integer exchangeStrategy;



}
