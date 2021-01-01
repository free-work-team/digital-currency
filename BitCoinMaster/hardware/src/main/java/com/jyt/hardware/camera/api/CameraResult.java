package com.jyt.hardware.camera.api;

/**
 * Created by Administrator on 2018/1/2.
 */

public class CameraResult {
    private String err;
    private boolean success;

    public CameraResult(){

    }
    public CameraResult(boolean succese,String err){
        this.success=success;
        this.err = err;
    }
    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
