package com.jyt.terminal.util;

import com.jyt.terminal.util.SpringContextHolder;
import com.jyt.terminal.config.properties.BitProperties;

/**
 * 验证码工具类
 */
public class KaptchaUtil {

    /**
     * 获取验证码开关
     */
    public static Boolean getKaptchaOnOff() {
        return SpringContextHolder.getBean(BitProperties.class).getKaptchaOpen();
    }
}