package com.jyt.terminal.service.impl;

import java.util.List;

import com.jyt.terminal.commom.enums.BitEnum;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jyt.terminal.dao.MenuMapper;
import com.jyt.terminal.dao.UserMapper;
import com.jyt.terminal.dto.ShiroUser;
import com.jyt.terminal.factory.IConstantFactory;
import com.jyt.terminal.model.User;
import com.jyt.terminal.service.IShiro;



@Service
public class ShiroFactroy implements IShiro {

	@Autowired
	private UserMapper userMapper;
	@Autowired
    private MenuMapper menuMapper;
	@Autowired
	private IConstantFactory constantFactory;
    
    @Override
    public User user(String account) {

       User user = userMapper.getByAccount(account);
        // 账号不存在
       if (null == user) {
            throw new CredentialsException();
        }
        // 账号被冻结
        if (user.getStatus() == BitEnum.UserStatus.DISABLE.getValue()) {
            throw new LockedAccountException("Account is frozen !");
        }
        // 账号已删除
        if (user.getStatus() == BitEnum.UserStatus.DELETE.getValue()) {
            throw new LockedAccountException("Account does not exist !");
        }
        // 账号被冻结
        if (user.getStatus() == BitEnum.UserStatus.FREEZE.getValue()) {
            throw new LockedAccountException("The password is entered incorrectly three times in a row, and the account is frozen !");
        }
    	return user;
    }

    @Override
    public ShiroUser shiroUser(User user) {
    	
        ShiroUser shiroUser = new ShiroUser();

        shiroUser.setId(user.getId());
        shiroUser.setAccount(user.getAccount());
        shiroUser.setName(user.getName());
        shiroUser.setRoleId(user.getRoleId());
        String roleName = constantFactory.getSingleRoleName(user.getRoleId());
        shiroUser.setRoleName(roleName);
       
        return shiroUser;
    }


    @Override
    public SimpleAuthenticationInfo info(ShiroUser shiroUser, User user, String realmName) {
        String credentials = user.getPassword();

        // 密码加盐处理
        String source = user.getSalt();
        ByteSource credentialsSalt = new Md5Hash(source);
        return new SimpleAuthenticationInfo(shiroUser, credentials, credentialsSalt, realmName);
    }

	@Override
	public List<String> findPermissionsByRoleId(Integer roleId) {
		return menuMapper.getResUrlsByRoleId(roleId);
	}





   

}
