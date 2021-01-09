package com.jyt.terminal.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.dao.UserMapper;
import com.jyt.terminal.dto.QueryUserDTO;
import com.jyt.terminal.exception.TerminalException;
import com.jyt.terminal.model.User;
import com.jyt.terminal.service.IUserService;
import com.jyt.terminal.util.MD5Util;
import com.jyt.terminal.util.ShiroKit;

/**
 * 
 * @author zengcong
 * @date 2019年4月25日
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements  IUserService{

	
	private Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	
	
	@Autowired
	private UserMapper userMapper;
	
	@Override
	public List<Map<String, Object>> getUserList(Page<Map<String, Object>> page,QueryUserDTO queryUserDTO) {
		List<Map<String, Object>> list = userMapper.getUserList(page,queryUserDTO);
		return list;
	}

	@Override
	public int setStatus(Integer userId, int status) {
		return this.baseMapper.setStatus(userId, status);
	}

	@Override
	public int changePwd(Integer userId, String oldPwd, String newPwd) {
		if (newPwd.equals(oldPwd)) {
			throw new TerminalException(BizExceptionEnum.TWO_PWD_NOT_MATCH);
		}
    	User user = this.baseMapper.selectById(userId);
		String oldMd5 = MD5Util.md5(oldPwd, user.getSalt());
		if (user.getPassword().equals(oldMd5)) {
			String newMd5 = MD5Util.md5(newPwd, user.getSalt());
			 return this.baseMapper.changePwd(userId, newMd5);
		} else {
			throw new TerminalException(BizExceptionEnum.OLD_PWD_NOT_RIGHT);
		}
	}

	@Override
	public int setRoles(Integer userId, Integer roleIds) {
		return this.baseMapper.setRoles(userId, roleIds);
	}

	@Override
	public User getByAccount(String account) {
		// TODO Auto-generated method stub
		 return userMapper.getByAccount(account);
	}

	@Override
	public boolean validate(String account, String password,int type) {
		// TODO Auto-generated method stub
		User user = userMapper.getByAccount(account);
		
		 /*if (user == null || user.getType() != type){ 
			 log.info("登录失败，用户名不存在，用户名：{}",account);
			 return false;
		 }*/
 
		 if(user.getStatus()!=1 ) {
			 log.info("登录失败，状态不可用，用户名：{}，密码：{}",account);
         	return false;
		 }
	      String pwd = ShiroKit.md5(password, user.getSalt());
	       if(!StringUtils.equals(user.getPassword(), pwd)) {
	         log.info("登录失败，用户名或密码错，用户名：{}，密码：{}",account,password);
	          return false;
	       }
	            
		return true;
	}


	
	
}
