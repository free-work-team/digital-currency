package com.jyt.terminal.commom;

import java.util.List;
import java.util.Map;

/**
 * 控制器查询结果的包装类基类
 *
 * @author fengshuonan
 * @date 2017年2月13日 下午10:49:36
 */
public abstract class LanBaseControllerWarpper {

    public Object obj = null;

    public LanBaseControllerWarpper(Object obj) {
        this.obj = obj;
    }

    @SuppressWarnings("unchecked")
    public Object warp(String currentType) {
        if (this.obj instanceof List) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) this.obj;
            for (Map<String, Object> map : list) {
                warpTheMap(currentType,map);
            }
            return list;
        } else if (this.obj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) this.obj;
            warpTheMap(currentType,map);
            return map;
        } else {
            return this.obj;
        }
    }

    protected abstract void warpTheMap(String currentType,Map<String, Object> map);
    
}
