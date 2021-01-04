package com.jyt.bitcoinmaster.utils;


import java.util.Arrays;

public class StatusDescribe {

    public static String getStatusDescribe(int getStatus) {
        Arrays.fill(statusList, 0);
        try {
            int status = getStatus;
            if (status == -1) {
                statusList[0] = 1;
                return "数据传输错误,请检查连接或者重新发送";
            }
            StringBuilder builder = new StringBuilder();
            StringBuffer descriptBuffer = new StringBuffer();
            StringBuffer troubleBuffer = new StringBuffer();
            //传感应状态
            if ((status & 0x200) > 0) {
                statusList[1] = 1;
                descriptBuffer.append("少纸, ");//[1]
                troubleBuffer.append("PaperFew|");

            }


            if ((status & 0x400) > 0 || (status & 0x08) > 0) {
                statusList[2] = 1;
                descriptBuffer.append("缺纸, ");
                troubleBuffer.append("OutOfPaper|");

            }
            //脱机状态
            if ((status & 0x4) > 0) {
                descriptBuffer.append("发生错误, ");
                troubleBuffer.append("happen error|");

            }
            if ((status & 0x20) > 0) {
                statusList[4] = 1;
                descriptBuffer.append("盖板打开, ");
                troubleBuffer.append("box open|");

            }
            //打印机状态
            if ((status & 0x1) > 0) {
                statusList[3] = 1;
                descriptBuffer.append("脱机, ");
                troubleBuffer.append("Offline|");

            }
            if ((status & 0x2) > 0 || (status & 0x10) > 0) {
                descriptBuffer.append("正在feed, ");//[8]
                troubleBuffer.append("feeding|");

            }

            //错误状态
            if ((status & 0x100) > 0) {
                statusList[5] = 1;
                descriptBuffer.append("机械错误, ");
                troubleBuffer.append("MachineError|");

            }


            if ((status & 0x40) > 0) {
                statusList[6] = 1;
                descriptBuffer.append("可自动恢复错误, ");
                troubleBuffer.append("CorrectingError|");

            }


            if ((status & 0x80) > 0) {
                statusList[7] = 1;
                descriptBuffer.append("不可恢复错误, ");
                troubleBuffer.append("NotCorrectError|");

            }

            String descript = descriptBuffer.toString().trim();

            if (!descript.isEmpty()) {
                descript = descript.substring(0, descript.length() - 1);
            } else {
                descript = "正常";
            }


            return descript;
        } catch (Exception e) {

            return "Offline123123";
        }
    }

    //解析用控制通道获取的状态

    /**
     * 0x10位 机器选择位
     * 0x08位 为打印状态正常
     * 0x40位 PNE 纸将尽
     * 0x02位  过温
     * 0x04位  切刀错误
     * 0x20位  PE 缺纸
     * 0x80位 盖打开
     * 0x01位 出纸器有纸
     */
    public static String getStatusDescribeWithControl(int getStatus) {
        Arrays.fill(statusList, 0);
        try {
            int status = getStatus;
            if (status == -1) {
                statusList[0] = 1;
                return "数据传输错误,请检查连接或者重新发送";
            }
            StringBuilder builder = new StringBuilder();
            StringBuffer descriptBuffer = new StringBuffer();
            StringBuffer troubleBuffer = new StringBuffer();
            //传感应状态
            if ((status & 0x40) > 0) {
                statusList[1] = 1;
                descriptBuffer.append("少纸, ");//[1]
                troubleBuffer.append("PaperFew|");

            }


            if ((status & 0x20) > 0) {
                statusList[2] = 1;
                descriptBuffer.append("缺纸, ");
                troubleBuffer.append("OutOfPaper|");

            }
            //脱机状态
            if ((status & 0x4) > 0) {
                descriptBuffer.append("发生错误, ");
                troubleBuffer.append("happen error|");

            }
            if ((status & 0x80) > 0) {
                statusList[4] = 1;
                descriptBuffer.append("盖板打开, ");
                troubleBuffer.append("box open|");

            }

            String descript = descriptBuffer.toString().trim();

            if (!descript.isEmpty()) {
                descript = descript.substring(0, descript.length() - 1);
            } else {
                descript = "正常";
            }


            return descript;
        } catch (Exception e) {

            return "Offline123123";
        }
    }

    private static int[] statusList = {
            //错误列表
            0,                            //数据传输错误0
            0,                            //少纸1
            0,                            //缺纸2
            0,                            //脱机3
            0,                            //机头抬起4
            0,                            //机械错误5
            0,                            //可自动恢复错误6
            0,                            //不可恢复错误7
    };

    /**
     * 判断打印机是否可以正常打印
     */
    public static boolean isPrinterOk() {
        for (int i = 0; i < statusList.length; i++) {
            if (statusList[i] != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断缺纸
     *
     * @return
     */
    public static boolean isOutOfPaper() {
        if (statusList[2] == 1) {
            return true;
        }
        return false;
    }

    /**
     * 判断脱机
     *
     * @return
     */
    public static boolean isOffline() {
        if (statusList[0] == 1 || statusList[3] == 1) {
            return true;
        }
        return false;
    }
}