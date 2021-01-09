package com.jyt.terminal.intercept;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class SessionCacheHolder {
    /**
     * 用户account, SessionId
     */
    public static Map<String, Serializable> loginSessionCache = Maps.newConcurrentMap();

    /**
     * session map
     */
    public static Map<Serializable, Date> oldSessionMap = Maps.newConcurrentMap();
}
