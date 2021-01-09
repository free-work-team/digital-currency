package com.jyt.terminal.task;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.jyt.terminal.commom.enums.BitEnum.UserStatus;
import com.jyt.terminal.model.User;
import com.jyt.terminal.service.IUserService;
import com.jyt.terminal.shiro.cache.CacheKit;

@Component
@EnableScheduling
public class UnfreezeTask {
	Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Resource
    IUserService userService;
	
	@Scheduled(cron = "0 0 0 * * ?") 
    public void emptyEhcache() {
		//清理密码错误次数缓存
		String passwordRetryCache ="passwordRetryCache";
		CacheKit.removeAll(passwordRetryCache);
		LOGGER.info("账户密码错误次数缓存清理完成");
		List<User> userList = userService.selectList(new EntityWrapper<User>().eq("status", UserStatus.FREEZE.getValue()));
		for(User u : userList){
			userService.setStatus(u.getId(), UserStatus.ENABLE.getValue());
		}
    }

}
