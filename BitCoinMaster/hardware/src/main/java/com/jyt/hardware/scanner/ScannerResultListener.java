package com.jyt.hardware.scanner;

public interface ScannerResultListener {
    void onError(String message);
    void onTimeOut();
     void scannerResult(byte[] bytes,int size);
}
