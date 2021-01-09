package com.jyt.terminal.controller.admin;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.jyt.terminal.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.commom.PageFactory;
import com.jyt.terminal.commom.Permission;
import com.jyt.terminal.commom.enums.BitEnum.RoleStatus;
import com.jyt.terminal.commom.enums.BitException;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.commom.node.ZTreeNode;
import com.jyt.terminal.dao.RoleMapper;
import com.jyt.terminal.dto.RoleDTO;
import com.jyt.terminal.exception.TerminalException;
import com.jyt.terminal.factory.IConstantFactory;
import com.jyt.terminal.model.Role;
import com.jyt.terminal.model.User;
import com.jyt.terminal.service.IRoleService;
import com.jyt.terminal.service.IUserService;
import com.jyt.terminal.warpper.RoleWarpper;

/**
 * 角色控制器
 *
 * @author fengshuonan
 * @Date 2017年2月12日21:59:14
 */
@Controller
@RequestMapping("/role")
public class RoleController extends BaseController {

    private static String PREFIX = "/system/role";
    
    @Autowired
    private IRoleService roleService;

    @Autowired
    private IUserService userService;

    @Resource
    private RoleMapper roleMapper;
    
    @Autowired
    private IConstantFactory constantFactory;
    
    /**
     * 跳转到角色列表页面
     */
    @RequestMapping(value = "",method = RequestMethod.GET)
    @Permission
    public String index() {
        return PREFIX + "/role.html";
    }

    @RequestMapping(value = "",method = RequestMethod.POST)
    @ResponseBody
    @Permission
    public Object list(HttpServletRequest request, RoleDTO roleDTO) {
        Map<String, String> respParam = Utils.getAllRequestParam(request);
        String currentType = respParam.get("currentType");
        Page<Map<String, Object>> page = new PageFactory<Map<String, Object>>().defaultPage();
        int count = this.roleMapper.getRoleListCount(roleDTO);
        List<Map<String, Object>> roles = this.roleService.getRoleList(page,roleDTO);
        page.setRecords((List<Map<String, Object>>) new RoleWarpper(roles).warp(currentType));
        page.setTotal(count);
        return super.packForBT(page);
    }
    
    /**
     * 跳转到权限分配
     */
    @RequestMapping(value = "/setAuthority",method = RequestMethod.GET)
    @Permission
    public String roleAssign(HttpServletRequest request, Model model) {
        Map<String, String> respParam = Utils.getAllRequestParam(request);
        Integer roleId = Integer.valueOf(respParam.get("roleId"));
        if (ToolUtil.isEmpty(roleId)) {
            throw new BitException(BizExceptionEnum.REQUEST_NULL);
        }
        model.addAttribute("roleId", roleId);
        //model.addAttribute("roleName", constantFactory.getSingleRoleName(roleId));
        return PREFIX + "/role_assign.html";
    }
    
    /**
     * 配置权限
     */
    @RequestMapping(value = "/setAuthority",method = RequestMethod.POST)
    @Permission
    @ResponseBody
    public Tip setAuthority(@RequestParam("roleId") Integer roleId, @RequestParam("ids") String ids) {
        if (ToolUtil.isOneEmpty(roleId)) {
            throw new BitException(BizExceptionEnum.REQUEST_NULL);
        }
        this.roleService.setAuthority(roleId, ids);
        return SUCCESS_TIP;
    }
    
    /**
     * 跳转到添加角色
     */
    @RequestMapping(value = "/add",method = RequestMethod.GET)
    @Permission
    public String roleAdd() {
        return PREFIX + "/role_add.html";
    }

    /**
     * 角色新增
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @Permission
    @ResponseBody
    public Tip add(@Valid Role role, BindingResult result) {
        if (result.hasErrors()) {
            throw new TerminalException(BizExceptionEnum.REQUEST_NULL);
        }
        role.setStatus(RoleStatus.EFFECTIVE.getCode());
        role.setCreatetime(new Date());
        this.roleService.insert(role);
        return SUCCESS_TIP;
    }
    
    /**
     * 删除角色
     */
    @RequestMapping("/delate")
    @Permission
    @ResponseBody
    public Tip delete(@RequestParam Integer roleId) {
        if (ToolUtil.isEmpty(roleId)) {
            throw new TerminalException(BizExceptionEnum.REQUEST_NULL);
        }

        //不能删除超级管理员角色
        if (roleId.equals(Const.ADMIN_ROLE_ID)) {
            throw new TerminalException(BizExceptionEnum.CANT_DELETE_ADMIN);
        }

        //缓存被删除的角色名称
        //LogObjectHolder.me().set(constantFactory.getSingleRoleName(roleId));

        this.roleService.delRoleById(roleId);

        //删除缓存
        //CacheKit.removeAll(Cache.CONSTANT);
        return SUCCESS_TIP;
    }

    /**
     * 获取角色列表
     */
    @RequestMapping(value = "/roleTreeListByUserId/{userId}")
    @ResponseBody
    public List<ZTreeNode> roleTreeListByUserId(@PathVariable Integer userId) {
        User theUser = this.userService.selectById(userId);
        Integer roleId = theUser.getRoleId();
        if (ToolUtil.isEmpty(roleId)) {
        	List<ZTreeNode> roleTreeList = this.roleService.roleTreeList();
            return roleTreeList;
        } else {
            List<ZTreeNode> roleTreeListByUserId = this.roleService.roleTreeListByRoleId(roleId);
            return roleTreeListByUserId;
        }
    }

}
