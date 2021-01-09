package com.jyt.terminal.controller.admin;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.naming.NoPermissionException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.jyt.terminal.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.commom.LogObjectHolder;
import com.jyt.terminal.commom.PageFactory;
import com.jyt.terminal.commom.Permission;
import com.jyt.terminal.commom.enums.BitEnum.UserStatus;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.common.db.Db;
import com.jyt.terminal.dao.UserMapper;
import com.jyt.terminal.dto.QueryUserDTO;
import com.jyt.terminal.exception.TerminalException;
import com.jyt.terminal.factory.UserFactory;
import com.jyt.terminal.model.User;
import com.jyt.terminal.service.IUserService;
import com.jyt.terminal.warpper.UserWarpper;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Controller
@RequestMapping("/mgr")
public class UserController extends BaseController{
	
	private static String PREFIX = "/system/user/";
	
	@Autowired
	public IUserService userService;
	@Autowired
	public UserMapper userMapper;
	/**
     * 跳转到查看用户列表的页面
     */
    @RequestMapping("")
	@Permission
	public String index() {
        return PREFIX + "user.html";
    }


	/**
     * 查询用户列表
     */
    @ApiOperation(value="查询用户列表",notes="分页查询用户列表")
    @RequestMapping(value = "",method = RequestMethod.POST)
	@Permission
	@ResponseBody
    public Object list(@ApiParam(name="客户查询对象",required=true) HttpServletRequest request, QueryUserDTO queryUserDTO) {
		Map<String, String> respParam = Utils.getAllRequestParam(request);
		String currentType = respParam.get("currentType");
    	Page<Map<String, Object>> page = new PageFactory<Map<String, Object>>().defaultPage();
        int count = this.userMapper.getUserListCount(queryUserDTO);
        List<Map<String, Object>> result = this.userService.getUserList(page, queryUserDTO);
        page.setRecords((List<Map<String, Object>>) new UserWarpper(result).warp(currentType));
        page.setTotal(count);
        return super.packForBT(page);
    }

	/**
	 * 跳转到新增用户页面
	 */
	@RequestMapping(value = "/add",method = RequestMethod.GET)
	@Permission
	public String addView(Model model) {
		return PREFIX + "user_add.html";
	}

	/**
	 * 提交新增用户
	 */
	@RequestMapping(value = "/add",method = RequestMethod.POST)
	@Permission
	@ResponseBody
	public Tip add(@Valid QueryUserDTO user, BindingResult result) {
		if (result.hasErrors()) {
			throw new TerminalException(BizExceptionEnum.REQUEST_NULL);
		}

		// 判断账号是否重复
		User theUser = userService.getByAccount(user.getAccount());
		if (theUser != null) {
			throw new TerminalException(BizExceptionEnum.USER_ALREADY_REG);
		}
		// 完善账号信息
		user.setSalt(ShiroKit.getRandomSalt(5));
		//String password = ToolUtil.getRandomString(8);
		String phone = user.getPhone();
		String password = phone.substring(phone.length()-6, phone.length());
		user.setPassword(ShiroKit.md5(password, user.getSalt()));
		user.setStatus(UserStatus.ENABLE.getValue());
		user.setCreatetime(new Date());
		User insertUser = UserFactory.createUser(user);
		this.userService.insert(insertUser);
		return SUCCESS_TIP;
	}

	/**
	 * 跳转到详情管理员页面
	 */
	@RequestMapping("/detail/{userId}")
	@Permission
	public String userDetail(@PathVariable Integer userId, Model model) {
		if (ToolUtil.isEmpty(userId)) {
			throw new TerminalException(BizExceptionEnum.REQUEST_NULL);
		}
		//assertAuth(userId);
		User user = buildUserDetail(userId, model);
		LogObjectHolder.me().set(user);
		return PREFIX + "user_detail.html";
	}
	private User buildUserDetail(Integer userId, Model model) {
		User user = this.userService.selectById(userId);
		user.setPhone(user.getPhone());
		model.addAttribute(user);
		return user;
	}
	
	/**
	 * 跳转到修改用户信息页面
	 */
	@Permission
	@RequestMapping("/edit/{userId}")
	public String userEdit(@PathVariable Integer userId, Model model) {
		if (ToolUtil.isEmpty(userId)) {
			throw new TerminalException(BizExceptionEnum.REQUEST_NULL);
		}
		//assertAuth(userId);
		User user = buildUserDetail(userId, model);
		LogObjectHolder.me().set(user);
		return PREFIX + "user_edit.html";
	}
	
	/**
	 * 修改用户信息
	 *
	 * @throws NoPermissionException
	 */
	@RequestMapping("/edit")
	@Permission
	@ResponseBody
	public Tip edit(@Valid QueryUserDTO user, BindingResult result) throws NoPermissionException {
		if (result.hasErrors()) {
			throw new TerminalException(BizExceptionEnum.REQUEST_NULL);
		}
		User oldUser = userService.selectById(user.getId());
		try {
			this.userService.updateById(UserFactory.editUser(user, oldUser));
		} catch (Exception e) {
			throw new TerminalException(BizExceptionEnum.USER_ACCOUNT_USE);
		}
		
		return SUCCESS_TIP;
	}
	
	/**
	 * 删除用户信息（逻辑删除）
	 */
	@RequestMapping("/delete")
	@Permission
	@ResponseBody
	public Tip delete(@RequestParam Integer userId) {
		if (ToolUtil.isEmpty(userId)) {
			throw new TerminalException(BizExceptionEnum.REQUEST_NULL);
		}
		//不能删除超级管理员
        if (userId.equals(Const.ADMIN_ID)) {
            throw new TerminalException(BizExceptionEnum.CANT_DELETE_ADMIN);
        }
		this.userService.setStatus(userId, 3);
		return SUCCESS_TIP;
	}
	
	/**
	 * 跳转到修改密码界面
	 */
	@RequestMapping("/user_chpwd")
	public String chPwd() {
		return PREFIX + "user_chpwd.html";
	}
	
	/**
	 * 修改当前用户的密码
	 */
	@RequestMapping("/changePwd")
	@ResponseBody
	public Object changePwd( String oldPwd,  String newPwd,  String rePwd,  String checkCode) {
		if (newPwd.equals(oldPwd)) {
			throw new TerminalException(BizExceptionEnum.TWO_PWD_NOT_MATCH);
		}
		//String checkCodeBak = String.valueOf(getSession().getAttribute(SmsTemplate.MODIFY_PWD.getValue()));
		/*if (StringUtils.isBlank(checkCodeBak) || !checkCodeBak.equals(checkCode)) {
			throw new BitException(BizExceptionEnum.CHECK_CODE_NOT_MATCH);
		}*/
		//getSession().removeAttribute(SmsTemplate.MODIFY_PWD.getValue());
		Integer userId = ShiroKit.getUser().getId();
		User user = userService.selectById(userId);
		String oldMd5 = ShiroKit.md5(oldPwd, user.getSalt());
		if (user.getPassword().equals(oldMd5)) {
			String newMd5 = ShiroKit.md5(newPwd, user.getSalt());
			user.setPassword(newMd5);
			user.updateById();
			return SUCCESS_TIP;
		} else {
			throw new TerminalException(BizExceptionEnum.OLD_PWD_NOT_RIGHT);
		}
	}
	
	/**
	 * 跳转到查看用户详情页面
	 */
	@RequestMapping("/user_info")
	public String userInfo(Model model) {
		Integer userId = ShiroKit.getUser().getId();
		if (ToolUtil.isEmpty(userId)) {
			throw new TerminalException(BizExceptionEnum.REQUEST_NULL);
		}
		User user = buildUserDetail(userId, model);
		LogObjectHolder.me().set(user);
		return PREFIX + "user_view.html";
	}
	
	/**
	 * 跳转到角色分配页面
	 */
	@RequestMapping("/setRole/{userId}")
	@Permission
	public String roleAssign(@PathVariable Integer userId, Model model) {
		if (ToolUtil.isEmpty(userId)) {
			throw new TerminalException(BizExceptionEnum.REQUEST_NULL);
		}
		User user = (User) Db.create(UserMapper.class).selectOneByCon("id", userId);
		model.addAttribute("userId", userId);
		model.addAttribute("userAccount", user.getAccount());
		return PREFIX + "user_roleassign.html";
	}
	/**
	 * 分配角色
	 */
	@RequestMapping("/setRole")
	@Permission
	@ResponseBody
	public Tip setRole(@RequestParam("userId") Integer userId, @RequestParam("roleIds") Integer roleId) {
		if (ToolUtil.isOneEmpty(userId)) {
			throw new TerminalException(BizExceptionEnum.REQUEST_NULL);
		}
		//assertAuth(userId);
		this.userService.setRoles(userId, roleId);
		return SUCCESS_TIP;
	}
	/**
	 * 解冻
	 */
	@RequestMapping("/unfreeze")
	@Permission
	@ResponseBody
	public Tip userEnable(@RequestParam Integer userId) {
		if (ToolUtil.isEmpty(userId)) {
			throw new TerminalException(BizExceptionEnum.REQUEST_NULL);
		}
		this.userService.setStatus(userId, UserStatus.ENABLE.getValue());
		return SUCCESS_TIP;
	}
	/**
	 * 冻结
	 */
	@RequestMapping("/freeze")
	@Permission
	@ResponseBody
	public Tip userDisenable(@RequestParam Integer userId) {
		if (ToolUtil.isEmpty(userId)) {
			throw new TerminalException(BizExceptionEnum.REQUEST_NULL);
		}
		this.userService.setStatus(userId, UserStatus.DISABLE.getValue());
		return SUCCESS_TIP;
	}
}
