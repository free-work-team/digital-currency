package com.jyt.bitcoinmaster.timer;

import com.jyt.bitcoinmaster.exchange.ExchangeFactory;
import com.jyt.bitcoinmaster.statics.Setting;
import com.jyt.hardware.cashoutmoudle.Sqlite.DBHelper;
import com.jyt.hardware.cashoutmoudle.bean.ExchangeOrder;
import com.jyt.hardware.cashoutmoudle.enums.OrderStatusEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 检查24小时内coinbasePro 订单是否完成
 */
public class CheckOrderTask extends TimerTask {

    private ScheduledExecutorService threadExecutor = Executors.newScheduledThreadPool(3);

    private static Logger log = Logger.getLogger("BitCoinMaster");

    private final int TIMEOUT_RANGE = -24;//h


    @Override
    public void run() {
        String beforeTimeOut = beforeTimeOut();
        try {
            //查寻是否创建订单
            ArrayList<ExchangeOrder> orders = DBHelper.getHelper().queryCoinbaseOrder(OrderStatusEnum.PRO_PENDING.getValue(), beforeTimeOut);
            log.info("查寻交易所下单是否完成,结果:" + orders.size() + "条,时间条件:" + beforeTimeOut + "至现在");

            for (final ExchangeOrder order : orders) {
                if (StringUtils.isNotBlank(order.getId())) {
                    threadExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ExchangeFactory.getExchange(DBHelper.getHelper().getExchangeByOrderId(order.getId())).checkOrderFinish(order.getId(), "buy".equals(order.getSide()),order.getCryptoCurrency());
                            } catch (Exception e) {
                                log.error("CheckOrderTask", e);
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            log.info("check order error :" + e);
        }
    }

    private String dateToString(Date dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(dateStr);
    }

    private String beforeTimeOut() {
        String beforeTime = dateToString(getTimeOutDateTime(new Date()));
        return beforeTime;
    }

    private Date getTimeOutDateTime(Date transTime) {
        Calendar c = Calendar.getInstance();
        c.setTime(transTime);
        c.add(Calendar.HOUR_OF_DAY, TIMEOUT_RANGE);
        return c.getTime();
    }
}
