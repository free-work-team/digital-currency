package com.jyt.bitcoinmaster.cashAcceptor;

import android.content.Context;
import com.alibaba.fastjson.JSONObject;
import com.jyt.bitcoinmaster.activity.MyApp;
import com.jyt.bitcoinmaster.statics.Setting;
import com.jyt.hardware.billacceptor.api.CPIDeviceCom;
import com.jyt.hardware.billacceptor.listener.DevEventListener;
import com.jyt.hardware.cashoutmoudle.Sqlite.DBHelper;
import com.jyt.hardware.cashoutmoudle.bean.HardwareConfig;
import com.jyt.hardware.config.Config;
import cpi.banknotedevices.RecyclerNoteTableEntry;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class CPIService implements ICashAcceptor {

    private static Logger log = Logger.getLogger("CPIService");

    private Context context;
    private CPIDeviceCom cpiDeviceCom;
    private DevEventListener devEventListener;

    private final Integer MAX_INSERT = 100;

    private volatile static CPIService cpiService;

    public static CPIService getInstance(Context context, DevEventListener devEventListener) {
        if (null == cpiService) {
            synchronized (CPIService.class) {
                if (null == cpiService) {
                    cpiService = new CPIService(context, devEventListener);
                }
            }
        }
        return cpiService;
    }


    public CPIService(Context context, DevEventListener devEventListener) {
        this.context = context;
        this.devEventListener = devEventListener;
    }

    /**
     * connnect devices
     */
    @Override
    public String connDevices() {
        cpiDeviceCom = CPIDeviceCom.getInstance(devEventListener);
        Config config = ((MyApp) context.getApplicationContext()).getConfig();
        String CPIdev =  config.getCPIdev();
        return cpiDeviceCom.open(CPIdev);
    }

    @Override
    public void initDevices() {
        try {
            Setting.currency = cpiDeviceCom.getCurrency();
            log.info("获取币种为：" + Setting.currency);
            Thread.sleep(1000);
            resetPS();
        } catch (Exception e) {
            log.error("initDevices error", e);
        }
    }

    // 将数据库中的ps 重新设置一遍
    private void resetPS() {
        HardwareConfig psConfig = DBHelper.getHelper().queryKeyEixst("RecyclingEnabled");
        if (psConfig != null) {
            if (StringUtils.isNotEmpty(psConfig.getHwValue())) {
                cpiDeviceCom.setPS(new int[]{new BigDecimal(psConfig.getHwValue()).intValue()});
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
        cpiDeviceCom.setAcceptance(enable);
    }

    /**
     * Set Accept a bill from escrow
     */
    @Override
    public void setEscrowAction(boolean isAction) {
            cpiDeviceCom.setEscrowAction(isAction);
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
        int totalValue = 0;
        int realvalue = 0;
        ArrayList<RecyclerNoteTableEntry> list = cpiDeviceCom.getList();
        for (RecyclerNoteTableEntry recy : list) {
            int vl = new BigDecimal(recy.getValue()).multiply(new BigDecimal(recy.getCount())).intValue();
            if (recy.isRecyclingEnabled()) {
                totalValue += vl;
                realvalue = new BigDecimal(recy.getValue()).intValue();
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
        cpiDeviceCom.dispenseByValue(Double.valueOf(amount));
    }

    /**
     * 获取缓存余额
     *
     * @return
     */
    @Override
    public int getEscrowLast() {
        int totalValue = 0;
        ArrayList<RecyclerNoteTableEntry> list = cpiDeviceCom.getList();
        for (RecyclerNoteTableEntry recy : list) {
            int vl = new BigDecimal(recy.getValue()).multiply(new BigDecimal(recy.getCount())).intValue();
            if (recy.isRecyclingEnabled()) {
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
        List<Denomination> denominations = new ArrayList<>();
        ArrayList<RecyclerNoteTableEntry> list = cpiDeviceCom.getList();
        Denomination deno;
        for (RecyclerNoteTableEntry recy:list ) {
            deno = new Denomination();
            deno.setIndex(recy.getIndex());
            deno.setRealvalue(String.valueOf(recy.getValue()));
            deno.setRoute(recy.isRecyclingEnabled()?Denomination.PAYOUTSTORE:Denomination.CASHBOX);
            deno.setCountry(recy.getISO());
            denominations.add(deno);
        }
        return JSONObject.toJSONString(denominations);
    }


    /**
     * 设置入钞Cashbox
     */
    @Override
    public void setCB(String psCount) {
        cpiDeviceCom.clearRecyclerNote();
    }

    /**
     * 设置入钞PayoutStore
     */
    @Override
    public void setPS(String psCount, int index) {
        try {
            cpiDeviceCom.clearRecyclerNote();
            Thread.sleep(1000);
            cpiDeviceCom.setPS(new int[]{index});
            // 存储PS面额到数据库
            List<HardwareConfig> hardwareConfigs = new ArrayList<>();
            HardwareConfig hardwareConfig = new HardwareConfig();
            hardwareConfig.setHwKey("RecyclingEnabled");
            hardwareConfig.setHwValue(String.valueOf(index));
            hardwareConfigs.add(hardwareConfig);
            DBHelper.getHelper().saveHardwareConfig(hardwareConfigs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 清机，清除缓存中到钞箱内a
     */
    @Override
    public void payEmpty() {
        cpiDeviceCom.empty();
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
