package com.jyt.terminal.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.commom.node.ZTreeNode;
import com.jyt.terminal.dto.RoleDTO;
import com.jyt.terminal.model.Role;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2017-07-11
 */
public interface RoleMapper extends BaseMapper<Role> {

	int getRoleListCount(@Param("entity") RoleDTO roleDTO);
	/**
	 * 根据条件查询角色列表
	 *
	 * @return
	 * @date 2017年2月12日 下午9:14:34
	 */
	List<Map<String, Object>> getRoleList(@Param("page") Page<Map<String, Object>> page, @Param("entity") RoleDTO roleDTO);

	/**
	 * 删除某个角色的所有权限
	 *
	 * @param roleId
	 *            角色id
	 * @return
	 * @date 2017年2月13日 下午7:57:51
	 */
	int deleteRolesById(@Param("roleId") Integer roleId);

	/**
	 * 获取角色列表树
	 *
	 * @return
	 * @date 2017年2月18日 上午10:32:04
	 */
	List<ZTreeNode> roleTreeList();

	/**
	 * 获取角色列表树
	 *
	 * @return
	 * @date 2017年2月18日 上午10:32:04
	 */
	List<ZTreeNode> roleTreeListByRoleId(@Param("roleId") Integer roleIds);
}