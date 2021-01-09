package com.jyt.terminal.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.jyt.terminal.dto.QueryUserDTO;
import com.jyt.terminal.model.User;


/**
 * <p>
 * 管理员表 服务类
 * </p>
 *
 * @author stylefeng123
 * @since 2018-02-22
 */
public interface IUserService extends IService<User> {
	
	/**
	 * 获取用户列表
	 */
	List<Map<String,Object>> getUserList(Page<Map<String, Object>> page,QueryUserDTO queryUserDTO);

    /**
     * 修改用户状态
     */
    int setStatus(@Param("userId") Integer userId, @Param("status") int status);

    /**
     * 修改密码
     */
    int changePwd(Integer userId, String oldPwd,String newPwd);
    
    /**
     * 设置用户的角色
     */
    int setRoles(@Param("userId") Integer userId, @Param("roleIds") Integer roleIds);

    /**
     * 通过账号获取用户
     */
    User getByAccount(String account);

    /**
     * 验证用户名密码
     * @param account
     * @param password
     * @return
     */
    boolean validate(String account,String password,int type);
}
