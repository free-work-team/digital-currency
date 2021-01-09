package com.jyt.terminal.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jyt.terminal.commom.node.ZTreeNode;
import com.jyt.terminal.dao.MenuMapper;
import com.jyt.terminal.dao.RelationMapper;
import com.jyt.terminal.dao.RoleMapper;
import com.jyt.terminal.dto.RoleDTO;
import com.jyt.terminal.model.Relation;
import com.jyt.terminal.model.Role;
import com.jyt.terminal.service.IRoleService;
import com.jyt.terminal.util.Convert;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Resource
    private RoleMapper roleMapper;
    
    @Resource
    private MenuMapper menuMapper;

    @Resource
    private RelationMapper relationMapper;
    
    
    @Override
    public List<Map<String, Object>> getRoleList(Page<Map<String, Object>> page, RoleDTO roleDTO) {
        return this.baseMapper.getRoleList(page,roleDTO);
    }

    @Override
    @Transactional(readOnly = false)
    public void setAuthority(Integer roleId, String ids) {

        // 删除该角色所有的权限
        this.roleMapper.deleteRolesById(roleId);
        //查询门户ID
        Long  mid = menuMapper.getMenuIdsByPcode("0");
       
        // 添加新的权限
        for (Long id : Convert.toLongArray(true, Convert.toStrArray(",", ids))) {
        	if(id == null ){
        		this.roleMapper.deleteRolesById(roleId);
        	}else if(!id.equals(mid)){
        		Relation relation = new Relation();
                relation.setRoleid(roleId);
                relation.setMenuid(id);
                this.relationMapper.insert(relation);
        	}
            
        }
    }
    
    @Override
    public List<ZTreeNode> roleTreeListByRoleId(Integer roleIds) {
        return this.baseMapper.roleTreeListByRoleId(roleIds);
    }

    @Override
    @Transactional(readOnly = false)
    public void delRoleById(Integer roleId) {
        //删除角色
        this.roleMapper.deleteById(roleId);

        // 删除该角色所有的权限
        this.roleMapper.deleteRolesById(roleId);
    }

    

    /**@Override
    public int deleteRolesById(Integer roleId) {
        return this.baseMapper.deleteRolesById(roleId);
    }
*/
    @Override
    public List<ZTreeNode> roleTreeList() {
        return this.baseMapper.roleTreeList();
    }


}
