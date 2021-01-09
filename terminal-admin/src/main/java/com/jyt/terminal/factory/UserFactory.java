package com.jyt.terminal.factory;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.jyt.terminal.commom.enums.BitException;
import com.jyt.terminal.dto.QueryUserDTO;
import com.jyt.terminal.model.User;
import com.jyt.terminal.util.EncryptionUtils;
import com.jyt.terminal.util.ToolUtil;


/**
 * 用户创建工厂
 *
 * @author fengshuonan
 * @date 2017-05-05 22:43
 */
public class UserFactory {

    public static User createUser(QueryUserDTO userDto) throws BitException {
        if (userDto == null) {
            return null;
        } else {
            User user = new User();
            BeanUtils.copyProperties(userDto, user);
            //user.setPhone(EncryptionUtils.encrypt(user.getPhone()));
            user.setUpdateTime(new Date());
            return user;
        }
    }

    public static User editUser(QueryUserDTO newUser, User oldUser) {
        if (newUser == null || oldUser == null) {
            return oldUser;
        } else {
        	if (ToolUtil.isNotEmpty(newUser.getAccount())) {
                oldUser.setAccount(newUser.getAccount());
            }
            if (ToolUtil.isNotEmpty(newUser.getName())) {
                oldUser.setName(newUser.getName());
            }
            if (ToolUtil.isNotEmpty(newUser.getEmail())) {
                oldUser.setEmail(newUser.getEmail());
            }
            if (ToolUtil.isNotEmpty(newUser.getPhone())) {
                oldUser.setPhone(newUser.getPhone());
            }
            /*if (ToolUtil.isNotEmpty(newUser.getType())) {
                oldUser.setType(newUser.getType());
            }*/
            oldUser.setUpdateTime(new Date());
            return oldUser;
        }
    }
}
