package com.jyt.hardware.billacceptor.listener;

public interface DevEventListener {

    void devEventResult(int what,int code, String eventValues,String realValues);

    void devEventResult(boolean success, String msg);
}
