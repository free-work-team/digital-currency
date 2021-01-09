package com.jyt.terminal.controller.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jyt.terminal.auth.util.JwtTokenUtil;
import com.jyt.terminal.commom.BaseResponse;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.controller.api.dto.AuthRequest;
import com.jyt.terminal.controller.api.dto.AuthResponse;
import com.jyt.terminal.service.ITerminalSettingService;

/**
 * 请求验证的
 *
 * @author fengshuonan
 * @Date 2017/8/24 14:22
 */
@RestController
public class AuthController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ITerminalSettingService terminalSettingService;

    @RequestMapping(value = "auth")
    public ResponseEntity<?> createAuthenticationToken(AuthRequest request) {
		
    	String account = request.getUserName();
    	
    	String password = request.getPassword();
    	
    	if(StringUtils.isBlank(account)||StringUtils.isBlank(password)){
    		
    		return ResponseEntity.ok(new BaseResponse(BizExceptionEnum.REQUEST_NULL));
    	}
        boolean validate = terminalSettingService.validate(request.getUserName(),request.getPassword());
      
        if (validate) {
        	
             String randomKey = jwtTokenUtil.getRandomKey();
             
             String token = jwtTokenUtil.generateToken(account, randomKey);
               
            return ResponseEntity.ok(new AuthResponse(BizExceptionEnum.SUCCESS,token, randomKey));
            
        } else {
        	return ResponseEntity.ok(new BaseResponse(400,"Terminal number or password error"));
        }
    }
    
}
