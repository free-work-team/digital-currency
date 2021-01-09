package com.jyt.terminal.controller.admin;


import java.util.List;
import javax.annotation.Resource;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.jyt.terminal.intercept.SessionCacheHolder;
import com.jyt.terminal.service.ILanguageService;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.code.kaptcha.Constants;
import com.jyt.terminal.commom.enums.BitEnum.UserStatus;
import com.jyt.terminal.commom.node.MenuNode;
import com.jyt.terminal.config.TerminalProperties;
import com.jyt.terminal.dto.ShiroUser;
import com.jyt.terminal.exception.InvalidKaptchaException;
import com.jyt.terminal.model.User;
import com.jyt.terminal.service.IMenuService;
import com.jyt.terminal.service.IUserService;
import com.jyt.terminal.util.ApiMenuFilter;
import com.jyt.terminal.util.ShiroKit;
import com.jyt.terminal.util.ToolUtil;



/**
 * 登录控制器
 *
 * @author fengshuonan
 * @Date 2017年1月10日 下午8:25:24
 */
@Controller
public class LoginController extends BaseController {
	
	@Autowired
    private IMenuService menuService;

    @Autowired
    private IUserService userService;
   
    @Autowired
    private TerminalProperties properties;

    @Resource
    private ILanguageService languageService;

    /**
     * 跳转到主页
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
    	  //获取菜单列表
		
    	Integer roleId = ShiroKit.getUser().getRoleId();
        if (roleId == null) {
            ShiroKit.getSubject().logout();
            model.addAttribute("tips", "User has no role");
            return "/login.html";
        }
        List<MenuNode> menus = menuService.getMenusByRoleIds(roleId);
        List<MenuNode> titles = MenuNode.buildTitle(menus);
        titles = ApiMenuFilter.build(titles);

        model.addAttribute("titles", titles);
        model.addAttribute("languageStorage", JSONObject.toJSONString(languageService.getAllList()));
        return "/index.html";
    }

    /**
     * 跳转到登录页面
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        if (ShiroKit.isAuthenticated() || ShiroKit.getUser() != null) {
            return REDIRECT + "/";
        } else {
            return "/login.html";
        }
    }

    /**
     * 点击登录执行的动作
     * @throws LoginException 
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginVali() throws LoginException {
    	String username = super.getPara("username").trim();
        String password = super.getPara("password").trim();
        String remember = super.getPara("remember");
		try {
	        //验证验证码是否正确
	        if (properties.getKaptchaOpen()) {
	            String kaptcha = super.getPara("kaptcha").trim();
	            String code = (String) super.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
	            if (ToolUtil.isEmpty(kaptcha) || !kaptcha.equalsIgnoreCase(code)) {
	                throw new InvalidKaptchaException();
	            }
	        }

	        Subject currentUser = ShiroKit.getSubject();
	        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray());

	        if ("on".equals(remember)) {
	            token.setRememberMe(true);
	        } else {
	            token.setRememberMe(false);
	        }

	        currentUser.login(token);

	        ShiroUser shiroUser = ShiroKit.getUser();
	     	        
	        super.getSession().setAttribute("shiroUser", shiroUser);
	        super.getSession().setAttribute("username", shiroUser.getAccount());

	        ShiroKit.getSession().setAttribute("sessionFlag", true);
	        return REDIRECT + "/";
		} catch (ExcessiveAttemptsException e) {
			User user = userService.getByAccount(username);
        	userService.setStatus(user.getId(), UserStatus.FREEZE.getValue());
        	throw new LoginException("The password is entered incorrectly three times in a row, and the account is frozen");
		}
        
    }

	/**
     * 退出登录
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logOut() {
        SessionCacheHolder.loginSessionCache.remove(ShiroKit.getUser().getAccount());
        ShiroKit.getSubject().logout();
        deleteAllCookie();
        return REDIRECT + "/login";
    }
    
    
	

}
