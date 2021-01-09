package com.jyt.terminal.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.jyt.terminal.commom.node.ZTreeNode;
import com.jyt.terminal.dto.RoleDTO;
import com.jyt.terminal.model.Role;

/**
 * 角色相关业务
 *
 * @author fengshuonan
 * @Date 2017年1月10日 下午9:11:57
 */
public interface IRoleService extends IService<Role> {

    /**
     * 
     *根据条件查询角色列表
     * @return
     */
    List<Map<String, Object>> getRoleList(Page<Map<String, Object>> page, RoleDTO roleDTO);

    /**
     * 设置某个角色的权限
     */
    void setAuthority(Integer roleId, String ids);
    
    /**
     * 获取角色列表树
     */
    List<ZTreeNode> roleTreeListByRoleId(Integer roleId);

    /**
     * 删除角色
     */
    void delRoleById(Integer roleId);
    /**
     * 删除某个角色的所有权限
     */
    //int deleteRolesById(@Param("roleId") Integer roleId);

    /**
     * 获取角色列表树
     */
    List<ZTreeNode> roleTreeList();

    

}
