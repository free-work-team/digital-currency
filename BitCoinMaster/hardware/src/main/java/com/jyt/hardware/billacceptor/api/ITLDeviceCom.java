package com.jyt.hardware.billacceptor.api;

import android.content.Context;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.jyt.hardware.billacceptor.listener.DevEventListener;


import org.apache.log4j.Logger;

import device.itl.sspcoms.BarCodeReader;
import device.itl.sspcoms.DeviceEvent;
import device.itl.sspcoms.DeviceEventListener;
import device.itl.sspcoms.DevicePayoutEventListener;
import device.itl.sspcoms.DeviceSetupListener;
import device.itl.sspcoms.ItlCurrency;
import device.itl.sspcoms.PayoutRoute;
import device.itl.sspcoms.SSPDevice;
import device.itl.sspcoms.SSPPayoutEvent;
import device.itl.sspcoms.SSPSystem;
import device.itl.sspcoms.SSPUpdate;

/**
 * Created by tbeswick on 05/04/2017.
 */

public class ITLDeviceCom  extends Thread implements DeviceSetupListener,DeviceEventListener , DevicePayoutEventListener {
    private static final int SETDEVICEENABLE =1;
    private static Logger log =  Logger.getLogger("BitCoinMaster");

    private static volatile ITLDeviceCom itlDeviceCom;
    private static boolean isrunning = false;
    private static SSPSystem ssp;
    private FT_Device ftDev = null;
    static final int READBUF_SIZE = 256;
    static final int WRITEBUF_SIZE = 4096;
    byte[] rbuf = new byte[READBUF_SIZE];
    byte[] wbuf = new byte[WRITEBUF_SIZE];
    int mReadSize = 0;
    private SSPDevice sspDevice = null;

    private DevEventListener devEventListener;
    private int flag;

   public static  ITLDeviceCom getInstance(DevEventListener devEventListener) {
        if (itlDeviceCom == null) {
            synchronized (ITLDeviceCom.class) {
                if (itlDeviceCom == null) {
                    itlDeviceCom = new ITLDeviceCom(devEventListener);
                }
            }
        }
        return itlDeviceCom;
    }
    public ITLDeviceCom(DevEventListener devEventListener){
        this.devEventListener = devEventListener;
        ssp = new SSPSystem();
        ssp.setOnDeviceSetupListener(this);
        ssp.setOnEventUpdateListener(this);
        ssp.setOnPayoutEventListener(this);
    }


    public boolean openDevice(Context context,D2xxManager ftD2xx) {

            if (ftD2xx == null) {
                return false;
            }

           int devCount = 0;
            // Get the connected USB FTDI devoces
            devCount = ftD2xx.createDeviceInfoList(context);
            if (devCount == 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                devCount = ftD2xx.createDeviceInfoList(context);
            }

        D2xxManager.FtDeviceInfoListNode[] deviceList = new D2xxManager.FtDeviceInfoListNode[devCount];
        ftD2xx.getDeviceInfoList(devCount, deviceList);

        // none connected
        if (devCount <= 0) {
            log.info("[ITLService]:" + "获取纸币接收器驱动列表失败");
            return false;
        }

        if (ftDev == null) {
            ftDev = ftD2xx.openByIndex(context, 0);
        } else {
            if (ftDev.isOpen()) {
                // if open and run thread is stopped, start thread
                SetConfig(9600, (byte) 8, (byte) 2, (byte) 0, (byte) 0);
                ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
                ftDev.restartInTask();
                return true;
            }
            synchronized (ftDev) {
                ftDev = ftD2xx.openByIndex(context, 0);
            }
        }
        // run thread
        if (ftDev.isOpen()) {
            SetConfig(9600, (byte) 8, (byte) 2, (byte) 0, (byte) 0);
            ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
            ftDev.restartInTask();
        }
        return true;
    }

    private void SetConfig(int baud, byte dataBits, byte stopBits, byte parity, byte flowControl) {
        if (!ftDev.isOpen()) {
            return;
        }

        // configure our port
        // reset to UART mode for 232 devices
        ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);
        ftDev.setBaudRate(baud);

        switch (dataBits) {
            case 7:
                dataBits = D2xxManager.FT_DATA_BITS_7;
                break;
            case 8:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
            default:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
        }

        switch (stopBits) {
            case 1:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
            case 2:
                stopBits = D2xxManager.FT_STOP_BITS_2;
                break;
            default:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
        }

        switch (parity) {
            case 0:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
            case 1:
                parity = D2xxManager.FT_PARITY_ODD;
                break;
            case 2:
                parity = D2xxManager.FT_PARITY_EVEN;
                break;
            case 3:
                parity = D2xxManager.FT_PARITY_MARK;
                break;
            case 4:
                parity = D2xxManager.FT_PARITY_SPACE;
                break;
            default:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
        }

        ftDev.setDataCharacteristics(dataBits, stopBits, parity);

        short flowCtrlSetting;
        switch (flowControl) {
            case 0:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
            case 1:
                flowCtrlSetting = D2xxManager.FT_FLOW_RTS_CTS;
                break;
            case 2:
                flowCtrlSetting = D2xxManager.FT_FLOW_DTR_DSR;
                break;
            case 3:
                flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
                break;
            default:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
        }

        ftDev.setFlowControl(flowCtrlSetting, (byte) 0x0b, (byte) 0x0d);
    }


    public void setup( int address, boolean escrow, boolean essp, long key){
        ssp.SetAddress(address);
        ssp.EscrowMode(escrow);
        ssp.SetESSPMode(essp, key);

    }

    @Override
    public void run(){

        int readSize = 0;
        ssp.Run();

        isrunning = true;
        while(isrunning){
            // poll for transmit data
            synchronized (ftDev) {
                int newdatalen = ssp.GetNewData(wbuf);
                if (newdatalen > 0) {
                    if(ssp.GetDownloadState() != SSPSystem.DownloadSetupState.active) {
                        ftDev.purge((byte) 1);
                    }
                    ftDev.write(wbuf, newdatalen);
                    ssp.SetComsBufferWritten(true);
                }
            }

            // poll for received
            synchronized (ftDev) {
                readSize = ftDev.getQueueStatus();
                if (readSize > 0) {
                    mReadSize = readSize;
                    if (mReadSize > READBUF_SIZE) {
                        mReadSize = READBUF_SIZE;
                    }
                    readSize = ftDev.read(rbuf,mReadSize );
                    ssp.ProcessResponse(rbuf, readSize);
                }
                //    } // end of if(readSize>0)
            }  // end of synchronized
            try {
                sleep(100);
//                itlDeviceCom.interrupt();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void Stop()
    {
        ssp.Close();
        isrunning = false;
    }

    public boolean getIsrunning() {
        return isrunning;
    }

    public boolean SetSSPDownload(final SSPUpdate update)
    {
        return ssp.SetDownload(update);

    }


    public void SetEscrowMode(boolean mode)
    {
        if(ssp != null) {
            ssp.EscrowMode(mode);
        }

    }

    public void SetDeviceEnable(boolean en) {
        this.flag = flag;
        if (ssp != null) {
            if (en) {
                ssp.EnableDevice();
            }else {
                ssp.DisableDevice();
            }
        }
    }
    public void SetEscrowAction(SSPSystem.BillAction action) {
        if(ssp != null){
            ssp.SetBillEscrowAction(action);
        }
    }


    public void SetBarcocdeConfig(BarCodeReader cfg)
    {
        if(ssp != null){
            ssp.SetBarCodeConfiguration(cfg);
        }
    }


    public int GetDeviceCode()
    {
        if(ssp != null){
            return sspDevice.headerType.getValue();
        }else{
            return -1;
        }
    }

    public void SetPayoutRoute(ItlCurrency cur, PayoutRoute rt)
    {
        if(ssp != null) {
            ssp.SetPayoutRoute(cur, rt);
        }
    }


    public void PayoutAmount(ItlCurrency cur)
    {
        if(ssp != null) {
            ssp.PayoutAmount(cur);
        }
    }

    public void FloatAmount(ItlCurrency amt, ItlCurrency min)
    {
        if(ssp != null){
            ssp.FloatAmount(amt,min);
        }
    }


    public void EmptyPayout()
    {
        if(ssp != null){
            ssp.EmptyPayout();
        }
    }


    @Override
    public void OnNewDeviceSetup(final SSPDevice dev) {
        sspDevice = dev;
        devEventListener.devEventResult(SETDEVICEENABLE,0,"Ready","");
        log.info("[ITLDeviceCom]: OnNewDeviceSetup:初始化完成");
    }

    @Override
    public void OnDeviceDisconnect(SSPDevice sspDevice) {
        devEventListener.devEventResult(SETDEVICEENABLE,-1,"connection failed","");
        log.info("[ITLDeviceCom]: OnNewDeviceSetup:初始化失败");
    }

    @Override
    public void OnDeviceEvent(DeviceEvent deviceEvent) {
        deviceEventResult(deviceEvent);
        log.info("[ITLDeviceCom]: devEventResult:"+deviceEvent.event.name());
    }


    private void deviceEventResult(final DeviceEvent dev) {
       log.info("+++++++++++++++++++++++++++++++++++++纸币dev.event:"+dev.event);
        int code = 999;//未知
        String eventValues = "";
        String realValues = "";
        switch (dev.event) {
            case BackInService:
                eventValues = "back in service";
                break;
            case CommunicationsFailure:
                code= -1;
                eventValues="Device coms Failure";
                break;
            case Ready:
                code = 1;
                eventValues = "Ready";
                return;
            case BillRead:
                code = 1;
                eventValues = "Reading";
                return;
            case BillEscrow:
                code = 2;
                eventValues = "Bill Escrow";
                realValues = String.valueOf((int) dev.value);
                log.info("[ITLDeviceCom]Bill Escrow:"+realValues);
                break;
            case BillStacked:
                code = 3;
                eventValues = "Bill Stacked";
                return;
            case BillReject:
                code = 4;
                eventValues = "Bill Reject";
                break;
            case BillJammed:
                code = 5;
                eventValues = "Bill jammed";
                break;
            case BillFraud:
                code = 6;
                eventValues = "Bill Fraud";
                break;
            case BillCredit:
                code = 7;
                eventValues = "Bill Credit";
                realValues = String.valueOf((int) dev.value);
                log.info("[ITLDeviceCom]Bill Credit:"+realValues);
                break;
            case Full:
                code = -1;
                eventValues = "Bill Cashbox full";
                break;
            case Initialising:
                code = 9;
                eventValues = "Initialising";
                break;
            case Disabled:
                code = 10;
                eventValues = "Disabled";
                return;
            case SoftwareError:
                code = 11;
                eventValues = "Software error";
                break;
            case AllDisabled:
                code = 12;
                eventValues = "All channels disabled";
                break;
            case CashboxRemoved:
                code = -1;
                eventValues = "Cashbox removed";
                break;
            case CashboxReplaced:
                code = -1;
                eventValues = "Cashbox replaced";

                break;
            case NotePathOpen:
                code = 15;
                eventValues = "Note path open";
                break;
            case BarCodeTicketEscrow:
                code = 16;
                eventValues = "Barcode ticket escrow";
                break;
            case BarCodeTicketStacked:
                code = 17;
                eventValues = "Barcode ticket stacked";
                break;
            case BillStoredInPayout:
                code = 18;
                eventValues = "Bill Stored in payout";
                realValues = String.valueOf((int) dev.value);
                break;
            case PayoutOutOfService:
                code = 19;
                eventValues = "Payout out of service!";
                break;
            case Dispensing:
                code = 20;
                eventValues = "Bill dispensing:" + " " +
                        (int) dev.value + ".00";
                realValues = String.valueOf((int) dev.value);
                log.info("[ITLDeviceCom]"+eventValues);
                break;
            case Dispensed:
                code = 21;
                eventValues = "Bill Dispensed:" + " " +
                        (int) dev.value + ".00";
                log.info("[ITLDeviceCom]"+eventValues);
                break;
            case Emptying:
                code = 22;
                eventValues = "Payout emptying...";
                break;
            case Emptied:
                code = 23;
                eventValues = "Payout emptied";
                break;
            case SmartEmptying:
                code = 24;
                eventValues = "Payout emptying..."+ " " +
                        (int) dev.value + ".00";
                break;
            case SmartEmptied:
                code = 25;
                eventValues = "Payout emptied";
                realValues = String.valueOf((int) dev.value);
                break;
            case BillTransferedToStacker:
                code = 26;
                eventValues = "BillTransferedToStacker";
                break;
            case BillHeldInBezel:
                code = 27;
                eventValues = "BillHeldInBezel";
                break;
            case BillInStoreAtReset:
                code = 28;
                eventValues = "BillInStoreAtReset";
                break;
            case BillInStackerAtReset:
                code = 29;
                eventValues = "BillInStackerAtReset";
                break;
            case BillDispensedAtReset:
                code = 30;
                eventValues = "BillDispensedAtReset";
                break;
            case NoteFloatRemoved:
                code = 31;
                eventValues = "NF detatched";
                break;
            case NoteFloatAttached:
                code = 32;
                eventValues = "NF attached";
                break;
            case DeviceFull:
                code = 33;
                eventValues = "Payout Device Full";
                break;
            case RefillBillCredit:
                code = 34;
                eventValues = "RefillBillCredit";
                break;
            case ErrorDuringPayout:
                code = 35;
                eventValues = "ErrorDuringPayout";
                break;
        }
//        log.info("[ITLDeviceCom]:"+"code="+code+",eventValues="+eventValues+",realValues="+realValues);
        devEventListener.devEventResult(SETDEVICEENABLE,code,eventValues,realValues);
    }

   @Override
   public void OnNewPayoutEvent(SSPPayoutEvent sspPayoutEvent) {
       log.info("[ITLDeviceCom]: payResult:" + sspPayoutEvent.event.name());
       //出钱结束,关闭接收模式
       if ("PayoutEnded".equals(sspPayoutEvent.event.name())) {
           SetDeviceEnable(false);
       }
   }

    public  SSPSystem getCurrency(){
        return ssp;
    }

}
