package com.jyt.bitcoinmaster.activity;


import android.app.Application;
import com.jyt.hardware.config.Config;
import com.jyt.bitcoinmaster.handler.CrashHandler;


public class MyApp extends Application {

    private Config config;


    @Override
    public void onCreate() {
        super.onCreate();
        //开启全局未知异常捕获
        CrashHandler.getInstance().init(getApplicationContext());

    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
