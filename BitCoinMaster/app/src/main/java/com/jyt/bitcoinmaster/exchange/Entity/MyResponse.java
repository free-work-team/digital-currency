package com.jyt.bitcoinmaster.exchange.Entity;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

/**
 * @author huoChangGuo
 * @since 2019/9/23
 */
public class MyResponse {
    private boolean success;
    private Integer code;
    private String message;

    public MyResponse(){
        this.success = false;
        this.code = 500;
        this.message = "";
    }
    public MyResponse(String message){
        this.success = false;
        this.code = 500;
        this.message = message;
    }
    public MyResponse(boolean isSuccess){
        this.success = isSuccess;
        this.code = 200;
        this.message = message;
    }
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        if (!success && StringUtils.isNotEmpty(message) && message.contains("message") && JSONObject.parseObject(message).containsKey("message")) {
            return JSONObject.parseObject(message).getString("message");
        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
