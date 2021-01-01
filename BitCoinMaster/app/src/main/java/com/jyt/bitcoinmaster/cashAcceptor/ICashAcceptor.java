package com.jyt.bitcoinmaster.cashAcceptor;

public interface ICashAcceptor {

    //连接设备，初始化用
    String connDevices();

    // 获取币种，重置ps
    void initDevices();

    //开、关灯
    void setDeviceEnable(boolean enable);

    //入钞、拒钞
    void setEscrowAction(boolean isAction);

    // 获取余额，是否卖币
    int getEscrowLast();

    //面额列表
    String getCurrencyList();

    //最大加钞数
    Integer getMaxInsert();

    //清机
    void payEmpty();

    // 设置单币种缓冲区，并清除其他面额缓冲区设置
    void setPS(String psCount,int index);

    //设置入钞Cashbox
    void setCB(String psCount);

    boolean checkOutCash(int tranCash);

    void pay(String amount);
}
