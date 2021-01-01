package com.jyt.hardware.printer;

public interface PrintInitListener {
    void initResult(boolean status,String message);
    void statusResult(boolean status,String message);
}
