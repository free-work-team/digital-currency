package com.jyt.terminal.factory;

import java.util.List;
import java.util.Map;


/**
 * 常量生产工厂的接口
 *
 * @author fengshuonan
 * @date 2017-06-14 21:12
 */
public interface IConstantFactory {

    /**
     * 根据用户id获取用户名称
     *
     * @author stylefeng
     * @Date 2017/5/9 23:41
     */
    String getUserNameById(Integer userId);

    /**
     * 根据用户id获取用户账号
     *
     * @author stylefeng
     * @date 2017年5月16日21:55:371
     */
    //String getUserAccountById(Integer userId);

    /**
     * 通过角色ids获取角色名称
     */
    //String getRoleName(String roleIds);

    /**
     * 通过角色id获取角色名称
     */
    String getSingleRoleName(Integer roleId);


    /**
     * 获取部门名称
     */
    //String getDeptName(Integer deptId);

    /**
     * 获取菜单的名称们(多个)
     */
    //String getMenuNames(String menuIds);

    /**
     * 获取菜单名称
     */
    //String getMenuName(Long menuId);

    /**
     * 获取菜单名称通过编号
     */
    //String getMenuNameByCode(String code,String appcode);

    /**
     * 获取字典名称
     */
    //String getDictName(Integer dictId);

    /**
     * 获取通知标题
     */
    //String getNoticeTitle(Integer dictId);

    /**
     * 根据字典名称和字典中的值获取对应的名称
     */
    //String getDictsByName(String name, Integer val);
    
    /**
     * 根据字典名称和字典中的值获取对应的名称
     */
    //Map<String,String> getBankTypeMap();
    

}
