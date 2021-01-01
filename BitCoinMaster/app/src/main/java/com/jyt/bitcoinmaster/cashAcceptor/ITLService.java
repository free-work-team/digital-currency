package com.jyt.bitcoinmaster.cashAcceptor;

import android.content.Context;
import com.alibaba.fastjson.JSONObject;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

import com.jyt.bitcoinmaster.statics.Setting;
import com.jyt.hardware.billacceptor.api.ITLDeviceCom;
import com.jyt.hardware.billacceptor.listener.DevEventListener;
import com.jyt.hardware.cashoutmoudle.Sqlite.DBHelper;
import com.jyt.hardware.cashoutmoudle.bean.HardwareConfig;
import device.itl.sspcoms.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class ITLService implements ICashAcceptor {

    private static Logger log = Logger.getLogger("ITLService");

    private Context context;
    private ITLDeviceCom itlDeviceCom;
    private DevEventListener devEventListener;
    private D2xxManager ftD2xx = null;

    private final Integer MAX_INSERT = 60;


    private volatile static ITLService itlService;

    public static ITLService getInstance(Context context, DevEventListener devEventListener) {
        if (null == itlService) {
            synchronized (ITLService.class) {
                if (null == itlService) {
                    itlService = new ITLService(context, devEventListener);
                }
            }
        }
        return itlService;
    }


    public ITLService(Context context, DevEventListener devEventListener) {
        this.context = context;
        this.devEventListener = devEventListener;
        try {
            ftD2xx = D2xxManager.getInstance(context);
        } catch (D2xxManager.D2xxException ex) {
            log.error("ITLService", ex);
        }
    }

    /**
     * connnect devices
     */
    @Override
    public String connDevices() {
        String result ="";
        itlDeviceCom = ITLDeviceCom.getInstance(devEventListener);
        if (itlDeviceCom.openDevice(context,ftD2xx)) {
           itlDeviceCom.setup(0, false, true, 0x0123456701234567L);
            if(itlDeviceCom.getIsrunning()) {
                result =  "success";
            }else{
                itlDeviceCom.start();
            }
            itlDeviceCom.SetEscrowMode(true);
        } else {
            result =  "fail";
        }
        return result;
    }

    @Override
    public void initDevices() {
        try {
            Setting.currency = itlDeviceCom.getCurrency().GetDevice().shortDatasetVersion;
            log.info("获取币种为：" + Setting.currency);
            resetPS();
        } catch (Exception e) {
            log.error("initDevices error", e);
        }
    }

    // 将数据库中的ps 重新设置一遍
    private void resetPS() {
        HardwareConfig psConfig = DBHelper.getHelper().queryKeyEixst("PayoutStore");
        if (psConfig != null) {
            if (StringUtils.isNotEmpty(psConfig.getHwValue())) {
                setPSInit(psConfig.getHwValue());
            }
        }
    }

    private void setPSInit(String psCount) {
        double theCount = Double.valueOf(psCount);
        SSPDevice sspDevice = itlDeviceCom.getCurrency().GetDevice();
        for (ItlCurrency itlCurrency : sspDevice.currency) {
            if (itlCurrency.realvalue == theCount) {
                PayoutRoute rt = PayoutRoute.PayoutStore;
                itlDeviceCom.SetPayoutRoute(itlCurrency, rt);
            } else {
                PayoutRoute rt = PayoutRoute.Cashbox;
                itlDeviceCom.SetPayoutRoute(itlCurrency, rt);
            }
        }
    }


    /**
     * Device enable/disable toggle
     *
     * @param enable
     */
    @Override
    public void setDeviceEnable(boolean enable) {
        itlDeviceCom.SetDeviceEnable(enable);
    }

    /**
     * Set Accept a bill from escrow
     */
    @Override
    public void setEscrowAction(boolean isAction) {
        if (isAction) {
            itlDeviceCom.SetEscrowAction(SSPSystem.BillAction.Accept);
        } else {
            itlDeviceCom.SetEscrowAction(SSPSystem.BillAction.Reject);
        }
    }

    /**
     * 判断是否出钞
     * 1.判断出钞量是否整除 当前面额
     * 2.判断当前面额余额是否足够
     *
     * @param tranCash
     * @return
     */
    @Override
    public boolean checkOutCash(int tranCash) {
        SSPDevice sspDevice = itlDeviceCom.getCurrency().GetDevice();
        int totalValue = 0;
        int realvalue = 0;
        for (ItlCurrency itlCurrency : sspDevice.currency) {
            double vl = itlCurrency.realvalue * (double) itlCurrency.storedLevel;
            if ("PS".equals(itlCurrency.route.toString())) {
                totalValue += vl;
                realvalue = Double.valueOf(itlCurrency.realvalue).intValue();
            }
        }
        //1.判断出钞量是否整除 当前面额
        //2.判断当前面额余额是否足够
        return realvalue != 0 && (tranCash % realvalue == 0) && totalValue >= tranCash;
    }

    /**
     * Enter payment amount
     *
     * @param amount
     */
    @Override
    public void pay(String amount) {
        ItlCurrency curpay = new ItlCurrency();
        curpay.country = itlDeviceCom.getCurrency().GetDevice().shortDatasetVersion;
        curpay.value = Integer.valueOf(amount) * 100;
        itlDeviceCom.PayoutAmount(curpay);
    }

    /**
     * 获取缓存余额
     *
     * @return
     */
    @Override
    public int getEscrowLast() {
        SSPDevice sspDevice = itlDeviceCom.getCurrency().GetDevice();
        int totalValue = 0;
        for (ItlCurrency itlCurrency : sspDevice.currency) {
            double vl = itlCurrency.realvalue * (double) itlCurrency.storedLevel;
            if ("PS".equals(itlCurrency.route.toString())) {
                totalValue += vl;
            }
        }
        return totalValue;
    }


    /**
     * 获取面额
     */
    @Override
    public String getCurrencyList() {
        SSPDevice sspDevice = itlDeviceCom.getCurrency().GetDevice();
//        if (sspDevice.currency == null) {
//            try {
//                Thread.sleep(10000);
//                sspDevice = itlDeviceCom.getCurrency().GetDevice();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        return JSONObject.toJSONString(sspDevice.currency);
    }


    /**
     * 设置入钞Cashbox
     */
    @Override
    public void setCB(String psCount) {
        double theCount = Double.valueOf(psCount);
        SSPDevice sspDevice = itlDeviceCom.getCurrency().GetDevice();
        for (ItlCurrency itlCurrency : sspDevice.currency) {
            if (itlCurrency.realvalue == theCount) {
                PayoutRoute rt = PayoutRoute.Cashbox;
                itlDeviceCom.SetPayoutRoute(itlCurrency, rt);
            }
        }
    }

    /**
     * 设置入钞PayoutStore
     */
    @Override
    public void setPS(String psCount,int index) {
        double theCount = Double.valueOf(psCount);
        SSPDevice sspDevice = itlDeviceCom.getCurrency().GetDevice();
        for (ItlCurrency itlCurrency : sspDevice.currency) {
            if (itlCurrency.realvalue == theCount) {
                PayoutRoute rt = PayoutRoute.PayoutStore;
                itlDeviceCom.SetPayoutRoute(itlCurrency, rt);
            } else {
                PayoutRoute rt = PayoutRoute.Cashbox;
                itlDeviceCom.SetPayoutRoute(itlCurrency, rt);
            }
        }
        // 存储PS面额到数据库
        List<HardwareConfig> hardwareConfigs = new ArrayList<>();
        HardwareConfig hardwareConfig = new HardwareConfig();
        hardwareConfig.setHwKey("PayoutStore");
        hardwareConfig.setHwValue(psCount);
        hardwareConfigs.add(hardwareConfig);
        DBHelper.getHelper().saveHardwareConfig(hardwareConfigs);
    }


    /**
     * 清机，清除缓存中到钞箱内a
     */
    @Override
    public void payEmpty() {
        itlDeviceCom.EmptyPayout();
    }


    /**
     * 最大加钞数
     * @return
     */
    @Override
    public Integer getMaxInsert() {
        return MAX_INSERT;
    }
}
