//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jyt.hardware.config;

import java.io.Serializable;

public class Config implements Serializable {
    private static final long serialVersionUID = 3420064362325627396L;
    private String PrintComm = "USB";
    private String PrintCom = "/dev/ttyS0";
    private String CPIdev = "/dev/ttyS0";
    private String PrintBaudrate = "9600";
    private String ScannerDev = "/dev/ttyS1";

    private int HostType = 2;
    private int CDMType;
    private String CDMCOM;
    private String DoorCOM;
    private int alarmId;
    private int LedVendor = 2;
    private String CommCOM;

    private int LedId3;
    private int CameraDev = 0;
    private int FaceCameraDev = 1;

    private String LEDCOM;
    private int LedId = 7;

    private String LEDCOM2;
    private int LedId2 = 7;

    private String FaceRegistration;



    public Config() {
    }

    public String getFaceRegistration() {
        return FaceRegistration;
    }

    public void setFaceRegistration(String faceRegistration) {
        FaceRegistration = faceRegistration;
    }

    public String getLEDCOM2() {
        return LEDCOM2;
    }

    public void setLEDCOM2(String LEDCOM2) {
        this.LEDCOM2 = LEDCOM2;
    }

    public String getPrintComm() {
        return this.PrintComm;
    }

    public void setPrintComm(String printComm) {
        this.PrintComm = printComm;
    }

    public String getPrintCom() {
        return this.PrintCom;
    }

    public void setPrintCom(String printCom) {
        this.PrintCom = printCom;
    }

    public String getCPIdev() {
        return CPIdev;
    }

    public void setCPIdev(String CPIdev) {
        this.CPIdev = CPIdev;
    }

    public String getPrintBaudrate() {
        return this.PrintBaudrate;
    }

    public void setPrintBaudrate(String printBaudrate) {
        this.PrintBaudrate = printBaudrate;
    }

    public int getHostType() {
        return this.HostType;
    }

    public void setHostType(int hostType) {
        this.HostType = hostType;
    }

    public String getLedComId() {
        if (this.LEDCOM == null) {
            return null;
        } else {
            return this.LEDCOM.startsWith("/dev/ttyS") ? "02" : "01";
        }
    }

    public String getLedDev() {
        return this.getComDev(this.LEDCOM);
    }

    public String getCDMDev() {
        return this.getComDev(this.CDMCOM);
    }

    public String getDoorDev() {
        return this.getComDev(this.DoorCOM);
    }

    private String getComDev(String com) {
        String dev = null;

        try {
            if (com == null) {
                dev = null;
            } else if (com.equals("com2")) {
                dev = "02";
            } else if (com.equals("com1")) {
                dev = "01";
            } else if (com.equals("/dev/ttyS3")) {
                dev = "02";
            } else if (com.equals("/dev/ttyS0")) {
                dev = "01";
            } else if (com.contains("ttyUSB") && com.length() == 12) {
                dev = "0" + com.substring(com.length() - 1);
            } else if (com.contains("ttyS") && com.length() == 10) {
                dev = "0" + com.substring(com.length() - 1);
            } else {
                String devInt = com.substring(com.length() - 2);
                dev = Integer.toHexString(Integer.parseInt(devInt));
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return dev;
    }

    public String getDoorCOM() {
        return this.DoorCOM;
    }

    public void setDoorCOM(String doorCOM) {
        this.DoorCOM = doorCOM;
    }

    public String getLEDCOM() {
        return this.LEDCOM;
    }

    public void setLEDCOM(String lEDCOM) {
        this.LEDCOM = lEDCOM;
    }

    public String getCommCOM() {
        return this.CommCOM;
    }

    public void setCommCOM(String commCOM) {
        this.CommCOM = commCOM;
    }

    public int getLedVendor() {
        return this.LedVendor;
    }

    public void setLedVendor(int ledVendor) {
        this.LedVendor = ledVendor;
    }

    public String getCDMCOM() {
        return this.CDMCOM;
    }

    public void setCDMCOM(String cDMCOM) {
        this.CDMCOM = cDMCOM;
    }

    public int getLedId() {
        return this.LedId;
    }

    public void setLedId(int ledId) {
        this.LedId = ledId;
    }

    public int getCDMType() {
        return this.CDMType;
    }

    public void setCDMType(int cDMType) {
        this.CDMType = cDMType;
    }

    public int getAlarmId() {
        return this.alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public int getLedId2() {
        return this.LedId2;
    }

    public void setLedId2(int ledId2) {
        this.LedId2 = ledId2;
    }

    public int getLedId3() {
        return this.LedId3;
    }

    public void setLedId3(int ledId3) {
        this.LedId3 = ledId3;
    }

    public int getFlashingLight() {
        return this.LedVendor == 3 ? 1 : 2;
    }

    public int getAlwaysLight() {
        return this.LedVendor == 3 ? 2 : 1;
    }

    public int getCloseLight() {
        return 0;
    }

    public String getScannerDev() {
        return ScannerDev;
    }

    public void setScannerDev(String scannerDev) {
        ScannerDev = scannerDev;
    }

    public int getCameraDev() {
        return CameraDev;
    }

    public void setCameraDev(int cameraDev) {
        CameraDev = cameraDev;
    }

    public int getFaceCameraDev() {
        return FaceCameraDev;
    }

    public void setFaceCameraDev(int faceCameraDev) {
        FaceCameraDev = faceCameraDev;
    }

    public String toString() {
        return "Config [PrintComm=" + this.PrintComm + ",PrintCom=" + this.PrintCom + ",PrintBaudrate=" + this.PrintBaudrate + " , HostType=" + this.HostType + ",CDMType=" + this.CDMType + ", CDMCOM=" + this.CDMCOM + ", DoorCOM=" + this.DoorCOM + ", LEDCOM=" + this.LEDCOM + ", alarmId=" + this.alarmId + ", LedVendor=" + this.LedVendor + ", CommCOM=" + this.CommCOM + ", LedId=" + this.LedId + ", LedId2=" + this.LedId2 + ", LedId3=" + this.LedId3 + "]";
    }
}
