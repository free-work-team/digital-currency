package com.jyt.terminal.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.dto.QueryUserDTO;
import com.jyt.terminal.model.User;


/**
 * <p>
 * 管理员表 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2017-07-11o
 */
public interface UserMapper extends BaseMapper<User> {
	
	int getUserListCount(@Param("user")QueryUserDTO queryUserDTO);
	
	List<Map<String, Object>> getUserList(@Param("page")Page<Map<String, Object>> page,@Param("user")QueryUserDTO queryUserDTO);

	 /**
     * 通过账号获取用户
     */
    User getByAccount(@Param("account") String account);
    
    /**
     * 修改密码
     */
    int changePwd(@Param("userId") Integer userId, @Param("pwd") String pwd);
    
    /**
     * 修改用户状态
     */
    int setStatus(@Param("userId") Integer userId, @Param("status") int status);
    
    /**
     * 设置用户的角色
     */
    int setRoles(@Param("userId") Integer userId, @Param("roleId") Integer roleId);
}