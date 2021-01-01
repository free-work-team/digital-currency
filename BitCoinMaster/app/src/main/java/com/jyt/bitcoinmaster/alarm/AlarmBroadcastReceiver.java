package com.jyt.bitcoinmaster.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.jyt.bitcoinmaster.exchange.ExchangeFactory;
import com.jyt.bitcoinmaster.statics.Setting;
import com.jyt.hardware.cashoutmoudle.Sqlite.DBHelper;
import com.jyt.hardware.cashoutmoudle.bean.ExchangeOrder;
import com.jyt.hardware.cashoutmoudle.enums.OrderStatusEnum;
import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.Locale;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    private ScheduledExecutorService threadExecutor = Executors.newScheduledThreadPool(5);

    public static String ACTION_ALARM = "action_alarm";

    private static Logger log = Logger.getLogger("BitCoinMaster");


    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_ALARM.equals(intent.getAction())) {
            // 执行取消任务
            try {
                repeat(context, intent);
            } catch (Exception e) {
                log.error("计划任务error" + e);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runTask();
                }
            }).start();
        }
    }

    //重复任务
    private void repeat(Context context, Intent intent) {

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 重复定时任务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY, pendingIntent);
        }

    }


    // 执行取消任务
    private void runTask() {
        String beforeTimeOut = beforeTimeOut();
        String afterTimeOut = afterTimeOut();
        try {
            //查寻12h前的pending 订单
            ArrayList<ExchangeOrder> orders = DBHelper.getHelper().queryCoinbaseOrderByDate(OrderStatusEnum.PRO_PENDING.getValue(), beforeTimeOut, afterTimeOut);
            log.info("取消订单:交易所 查寻前一天pending订单,结果:" + orders.size() + "条,时间条件:" + beforeTimeOut + "至" + afterTimeOut + ",即将逐一取消");
            for (final ExchangeOrder order : orders) {
                if (StringUtils.isNotBlank(order.getId())) {
                    threadExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                           Integer exchangeValue = DBHelper.getHelper().getExchangeByOrderId(order.getId());
                           ExchangeFactory.getExchange(exchangeValue).cancelOrderById(order.getId(), "sell".equals(order.getSide()),order.getTransId(),order.getCryptoCurrency());
                        }
                    });
                }
            }
        } catch (Exception e) {
            log.info("cancel order error :" + e);
        }
    }


    private String dateToString(Date dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        return sdf.format(dateStr);
    }

    private String beforeTimeOut() {
        String beforeTime = dateToString(getTimeOutDateTime(new Date(), -1));
        return beforeTime;
    }

    private String afterTimeOut() {
        String beforeTime = dateToString(getTimeOutDateTime(new Date(), 0));
        return beforeTime;
    }

    private Date getTimeOutDateTime(Date transTime, int day) {
        Calendar c = Calendar.getInstance();
        c.setTime(transTime);
        c.add(Calendar.DAY_OF_YEAR, day);
        return c.getTime();
    }

}
