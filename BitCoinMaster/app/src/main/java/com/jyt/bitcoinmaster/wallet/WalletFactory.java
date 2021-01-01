package com.jyt.bitcoinmaster.wallet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jyt.bitcoinmaster.wallet.blockchain.BlockchainService;
import com.jyt.hardware.cashoutmoudle.enums.WalletEnum;
import com.jyt.bitcoinmaster.wallet.bitgo.BitGoService;
import com.jyt.bitcoinmaster.wallet.blockchain.HttpClient;
import com.jyt.bitcoinmaster.wallet.coinbase.CoinbaseService;

import org.apache.log4j.Logger;

import java.math.BigDecimal;

public class WalletFactory {
    private static Logger log = Logger.getLogger("BitCoinMaster");


    /**
     * 生成渠道对象
     *
     * @param strategy
     * @return
     */
    public static IWallet getWallet(Integer strategy){
        IWallet iWallet = null;
        WalletEnum wallet = WalletEnum.getEnum(strategy);
        switch (wallet) {
            case BITGO:
                iWallet = BitGoService.getInstance();
                break;
            case COINBASE:
                iWallet = CoinbaseService.getInstance();
                break;
            case BLOCKCHAIN:
                iWallet = BlockchainService.getInstance();
                break;
            default:
                iWallet = DefaultWallet.getInstance();
                break;
        }
        return iWallet;
    }

    /**
     * 通过address查询确认数
     * @param address
     * @param amount
     * @param confirmation
     * @return
     */
    public static boolean checkAddress(String address, String amount, String confirmation) {
        boolean flag = false;
        try {
            String response = HttpClient.getInstance().get("https://api.blockcypher.com", "/v1/btc/main/addrs/" + address, null);
            JSONObject responseObj = JSONObject.parseObject(response);
            if (responseObj.containsKey("txrefs")) {
                JSONArray addressTrans = responseObj.getJSONArray("txrefs");
                if (addressTrans.size() > 0) {
                    for (JSONObject currentTrans : addressTrans.toJavaList(JSONObject.class)) {
                        //spent 判断是接收币
                        if (currentTrans.containsKey("spent") && currentTrans.getInteger("tx_output_n") >= 0) {
                            // 判断金额
                            if (currentTrans.getBigInteger("value").intValue() == new BigDecimal(amount).intValue()) {
                                // 判断确认数
                                log.info("SelectConfirmTask: address:" + address + "-----------------confirmations:" + currentTrans.getInteger("confirmations"));
                                if (currentTrans.getInteger("confirmations") >= Integer.parseInt(confirmation)) {
                                    flag = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("通过address查询确认数异常，" + e.getMessage());
        }
        return flag;
    }

}
