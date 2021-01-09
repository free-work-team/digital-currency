package com.jyt.terminal.aop;


import javax.security.auth.login.LoginException;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authc.DisabledAccountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.jyt.terminal.commom.BaseResponse;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.controller.admin.ErrorTip;
import com.jyt.terminal.exception.InvalidKaptchaException;
import com.jyt.terminal.exception.TerminalException;
import com.jyt.terminal.support.HttpKit;
import io.jsonwebtoken.JwtException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * 全局的的异常拦截器（拦截所有的控制器）（带有@RequestMapping注解的方法上都会拦截）
 *
 * @author fengshuonan
 * @date 2016年11月12日 下午3:19:56
 */
@ControllerAdvice
public class GlobalExceptionHandler  {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    /**
     * 拦截jwt相关异常
     */
    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> jwtException(JwtException e) {
    	log.error("JWT请求异常:", e);
        return  ResponseEntity.ok(new BaseResponse(BizExceptionEnum.TOKEN_ERROR));
    }
    
    
    /**
     * 拦截业务异常
     */
    /*@ExceptionHandler(TerminalException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<?> notFount(TerminalException e) {
      
        log.error("业务异常:{}", e.getMessage());
        
        return  ResponseEntity.ok(new BaseResponse(e.getCode(),e.getMessage()));
    }*/
    /**
     * 拦截业务异常
     */
    @ExceptionHandler(TerminalException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorTip notFount(TerminalException e) {
    	HttpKit.getRequest().setAttribute("tip", e.getMessage());
        log.error("业务异常:", e);
        return new ErrorTip(e.getCode(), e.getMessage());
    }
    
    /**
     * 拦截相关异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public  ResponseEntity<?> hruException(Exception e) {
    	
    	log.error("系统异常",e);
 
        return  ResponseEntity.ok(new BaseResponse(BizExceptionEnum.SERVER_ERROR));
    }
    
    /**
     * 账号密码错误异常
     */
    @ExceptionHandler(CredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String credentials(CredentialsException e, Model model) {
        //String username = HttpKit.getRequest().getParameter("username");
        //LogManager.me().executeLog(logTaskFactory.loginLog(username, "账号密码错误", HttpKit.getIp()));
        //model.addAttribute("tips", "accountOrPasswordError");//账号密码错误
        model.addAttribute("tips", "Account or password is wrong");
        return "/login.html";
    }
    
    /**
     * 验证码错误异常
     */
    @ExceptionHandler(InvalidKaptchaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String credentials(InvalidKaptchaException e, Model model) {
        //String username = getRequest().getParameter("username");
        //LogManager.me().executeLog(logTaskFactory.loginLog(username, "验证码错误", getIp()));
        //model.addAttribute("tips", "verificationCodeError");//验证码错误
        model.addAttribute("tips", "Verification code error");
        return "/login.html";
    }
    
    /**
     * 登录异常
     */
    @ExceptionHandler(LoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String accountNotFound(LoginException e, Model model) {
        //String username = getRequest().getParameter("username");
        //LogManager.me().executeLog(logTaskFactory.loginLog(username, "账户不存在", getIp()));
        model.addAttribute("tips", e.getMessage());
        return "/login.html";
    }

    /**
     * 无权访问该资源异常
     */
    @ExceptionHandler(UndeclaredThrowableException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String credentials(UndeclaredThrowableException e, Model model) {
        model.addAttribute("tip", "权限异常");
        return "/403.html";
//        return new ErrorTip(BizExceptionEnum.NO_PERMITION.getCode(), BizExceptionEnum.NO_PERMITION.getMessage());
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String notFount(RuntimeException e, Model model) {
        model.addAttribute("tip", "服务器未知运行时异常");
        return "/404.html";
//        return new ErrorTip(BizExceptionEnum.SERVER_ERROR.getCode(), BizExceptionEnum.SERVER_ERROR.getMessage());
    }

    /**
     * 账号被冻结异常
     */
    @ExceptionHandler(DisabledAccountException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String accountLocked(DisabledAccountException e, Model model) {
        model.addAttribute("tips", e.getMessage());
        return "/login.html";
    }
    
}
