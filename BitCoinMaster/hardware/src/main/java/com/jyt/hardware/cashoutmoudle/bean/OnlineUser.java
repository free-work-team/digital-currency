package com.jyt.hardware.cashoutmoudle.bean;

/**
 * 线上登录信息
 *
 * @author huoChangGuo
 * @since 2019/5/6
 */
public class OnlineUser {
    private String terminalNo;
    private String password;
    private String webAddress;


    public String getTerminalNo() {
        return terminalNo;
    }

    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }
}
