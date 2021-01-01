package com.jyt.hardware.billacceptor.api;

import com.alibaba.fastjson.JSONObject;
import com.jyt.hardware.billacceptor.listener.DevEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import cpi.banknotedevices.IRecycler;
import cpi.banknotedevices.PayoutAlgorithm;
import cpi.banknotedevices.PowerUpPolicy;
import cpi.banknotedevices.Recycler;
import cpi.banknotedevices.RecyclerNoteTableEntry;
import cpi.banknotedevices.events.BoolResponseEvent;
import cpi.banknotedevices.events.CassetteStatusEvent;
import cpi.banknotedevices.events.DeviceStateEvent;
import cpi.banknotedevices.events.DocumentStatusEvent;
import cpi.banknotedevices.events.DownloadProgressEvent;
import cpi.banknotedevices.events.EscrowSessionSummaryEvent;
import cpi.banknotedevices.events.IRecyclerEventListener;
import cpi.banknotedevices.events.MissingNotesEvent;
import cpi.banknotedevices.events.TransportStatusEvent;
import cpi.banknotedevices.exceptions.OperationNotAllowedException;
import org.apache.log4j.Logger;

public class CPIDeviceCom implements IRecyclerEventListener {

    private static Logger log = Logger.getLogger("CPIDeviceCom");

    private static final int SETDEVICEENABLE = 1;
    private static final int SETDEVICEEPAY = 8;

    private static volatile CPIDeviceCom cpiDeviceCom;

    private static IRecycler icr;

    private DevEventListener devEventListener;

    private ScheduledExecutorService threadExecutor = Executors.newScheduledThreadPool(2);


    public static CPIDeviceCom getInstance(DevEventListener devEventListener) {
        if (cpiDeviceCom == null) {
            synchronized (CPIDeviceCom.class) {
                if (cpiDeviceCom == null) {
                    cpiDeviceCom = new CPIDeviceCom(devEventListener);
                }
            }
        }
        return cpiDeviceCom;
    }

    public CPIDeviceCom(DevEventListener devEventListener) {
        this.devEventListener = devEventListener;

        icr = new Recycler();
        // Attach Listeners
        icr.addRecyclerListener(this);
    }


    //初始化通讯
    public String open(String dev) {
        try {
            icr.close();
            Thread.sleep(1000);
            icr.open(dev, PowerUpPolicy.R);
            getCpiOpenStatus();
            return "";
        } catch (Exception e) {
            return "fail";
        }
    }


    //开关灯
    public boolean setAcceptance(boolean en) {
        try {
            if (en) {
                icr.enableAcceptance();
            } else {
                icr.disableAcceptance();
            }
        } catch (OperationNotAllowedException e) {
            return false;
        }
        return true;
    }

    //入钞与退钞
    public boolean setEscrowAction(boolean en) {
        try {
            if (en) {
                icr.stackDocument();
            } else {
                icr.returnDocument();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //设置进入循环钞箱
    public boolean setPS(int[] arrEnables) {
        try {
            icr.setRecyclerNoteTableEnables(arrEnables);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //清除允许进入循环钞箱面额
    public boolean clearRecyclerNote() {
        try {
            icr.clearRecyclerNoteTableEnables();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //获取面额列表
    public ArrayList<RecyclerNoteTableEntry> getList() {
        ArrayList<RecyclerNoteTableEntry> entryList = new ArrayList<>();
        try {
            RecyclerNoteTableEntry[] arrNoteTable = icr.getRecyclerNoteTable();
            // Loop through each entry and add a row to the datagrid
            for (int i = 0; i < arrNoteTable.length; i++) {
                entryList.add(arrNoteTable[i]);
            }
        } catch (Exception e) {
        }
        return entryList;
    }

    public String getCurrency() {
        String currency = "";
        try {
            RecyclerNoteTableEntry[] arrNoteTable = icr.getRecyclerNoteTable();
            // Loop through each entry and add a row to the datagrid
            currency = arrNoteTable[0].getISO();
        } catch (Exception e) {
        }
        return currency;
    }

    //出钞
    public boolean dispenseByValue(double dblValue) {
        try {
            icr.dispenseByValue(dblValue, PayoutAlgorithm.BEST_CHANGE);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //清机
    public void empty() {
        try {
            icr.floatDownAll();
        } catch (Exception e) {
            devEventListener.devEventResult(SETDEVICEENABLE, -1, "Payout emptied Fail!", "");
        }
    }


    @Override
    public void escrowSessionSummaryReported(EscrowSessionSummaryEvent escrowSessionSummaryEvent) {
        log.info("CPI--------------escrowSessionSummaryReported:" + JSONObject.toJSONString(escrowSessionSummaryEvent));
    }

    @Override
    public void missingNotesReported(MissingNotesEvent missingNotesEvent) {
        log.info("CPI--------------missingNotesReported:" + JSONObject.toJSONString(missingNotesEvent));
    }

    @Override
    public void transportOpenedWhilePoweredOff() {
        log.info("CPI--------------transportOpenedWhilePoweredOff");
    }

    @Override
    public void transportStatusChanged(TransportStatusEvent transportStatusEvent) {
        log.info("CPI--------------transportStatusChanged:" + JSONObject.toJSONString(transportStatusEvent));
    }

    @Override
    public void cassetteStatusChanged(CassetteStatusEvent cassetteStatusEvent) {
        log.info("CPI--------------cassetteStatusChanged:" + JSONObject.toJSONString(cassetteStatusEvent));
    }

    @Override
    public void cheatDetected() {
        log.info("CPI--------------cheatDetected");
    }

    @Override
    public void clearAuditCompleted(BoolResponseEvent boolResponseEvent) {
        log.info("CPI--------------clearAuditCompleted:" + JSONObject.toJSONString(boolResponseEvent));
    }

    @Override
    public void deviceStateChanged(DeviceStateEvent deviceStateEvent) {
        log.info("CPI--------------deviceStateChanged:" + JSONObject.toJSONString(deviceStateEvent));
        String stateName = deviceStateEvent.getState().name();
        switch (stateName) {
//            case "INITIALIZING":
//                getCpiOpenStatus();
//                break;
            case "FLOATING_DOWN":
                getEmptyStatus();
                break;
            default:
                break;
        }
    }

    @Override
    public void documentStatusReported(DocumentStatusEvent documentStatusEvent) {
        String eventName = documentStatusEvent.getEvent().name();
//        log.info("CPI--------------documentStatusReported:" + JSONObject.toJSONString(documentStatusEvent));
        int code;//未知
        String realValues = "";
        try {
            realValues = JSONObject.parseObject(JSONObject.toJSONString(documentStatusEvent.getDocument())).getString("value");
        } catch (Exception e) {
            log.warn("documentStatusReported " + eventName + " get realValues fail");
        }
        switch (eventName) {
            case "ESCROWED"://缓冲
                code = 2;
                log.info("[CPIDeviceCom]Bill Escrow:" + realValues);
                devEventListener.devEventResult(SETDEVICEENABLE, code, eventName, realValues);
                break;
            case "STACKED"://入钞end
                code = 7;
                log.info("[CPIDeviceCom]Bill Credit:" + realValues);
                devEventListener.devEventResult(SETDEVICEENABLE, code, eventName, realValues);
                break;
            case "RETURNED"://拒钞
                code = 4;
                log.info("[CPIDeviceCom]Bill Returned:" + realValues);
                devEventListener.devEventResult(SETDEVICEENABLE, code, eventName, realValues);
                break;
             case "DISPENSED"://出钞
                 code = 20;
                 log.info("[CPIDeviceCom]Bill Dispensed:" + realValues);
                 devEventListener.devEventResult(SETDEVICEENABLE, code, eventName, realValues);
                 break;
            default:
                break;
        }
    }

    @Override
    public void downloadProgressReported(DownloadProgressEvent downloadProgressEvent) {
        log.info("CPI--------------downloadProgressReported:" + JSONObject.toJSONString(downloadProgressEvent));
    }

    /**
     * 初始化获取状态
     */
    private void getCpiOpenStatus() {
        final int outTimeSecond = 15000;//超时时间 15秒
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                long expireTime = new Date().getTime() + outTimeSecond;
                while (true) {
                    try {
                        if (new Date().getTime() > expireTime) {
                            //超时
                            devEventListener.devEventResult(SETDEVICEENABLE, -1, "CashAcceptor connection timed out!", "");
                            break;
                        } else {
                            //初始化完成
                            if ("HOST_DISABLED".equals(icr.getDeviceState().name())) {
                                devEventListener.devEventResult(SETDEVICEENABLE, 0, "already", "");
                                break;
                            }
                        }
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        log.error("初始化系统异常:{}", e);
                        devEventListener.devEventResult(SETDEVICEENABLE, -1, "CashAcceptor connection failed!", "");
                        break;
                    }
                }
            }
        });
    }


    /**
     * 清机后获取完成
     */
    private void getEmptyStatus() {
        final int outTimeSecond = 480000;//超时时间 8分钟
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                long expireTime = new Date().getTime() + outTimeSecond;
                while (true) {
                    try {
                        if (new Date().getTime() > expireTime) {
                            //超时
//                            devEventListener.devEventResult(SETDEVICEENABLE, -1, "CPI Payout emptied timed out!", "");
                            break;
                        } else {
                            //初始化完成
                            if ("IDLE".equals(icr.getDeviceState().name())) {
                                devEventListener.devEventResult(SETDEVICEENABLE, 25, "Payout emptied", "");
                                break;
                            }
                        }
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        log.error("清机系统异常:{}", e);
                        devEventListener.devEventResult(SETDEVICEENABLE, -1, "CashAcceptor connection failed!", "");
                        break;
                    }
                }
            }
        });
    }

}
