package com.jyt.terminal.auth.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jyt.terminal.auth.util.JwtTokenUtil;
import com.jyt.terminal.commom.BaseResponse;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.config.properties.JwtProperties;
import com.jyt.terminal.util.RenderUtil;

import io.jsonwebtoken.JwtException;



/**
 * 对客户端请求的jwt token验证过滤器
 *
 * @author fengshuonan
 * @Date 2017/8/24 14:04
 */
public class AuthFilter extends OncePerRequestFilter {

	private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JwtProperties jwtProperties;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
    	
    	String servletPath = request.getServletPath();
    	//非api请求，直接放过
    	if (!servletPath.startsWith("/" + jwtProperties.getApiPath())) {
            chain.doFilter(request, response);
            return;
        }
        final String requestHeader = request.getHeader(jwtProperties.getHeader());
       
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
        	
        	String authToken = requestHeader.substring(7);
        	 //验证token是否过期,包含了验证jwt是否正确
           
           try {
            	 boolean flag = jwtTokenUtil.isTokenExpired(authToken);
                if (flag) {
                    RenderUtil.renderJson(response, new BaseResponse(BizExceptionEnum.TOKEN_EXPIRED));
                    return;
                }
            } catch (JwtException e) {
                //有异常就是token解析失败
            	RenderUtil.renderJson(response, new BaseResponse(BizExceptionEnum.TOKEN_ERROR));
                log.error("token解析失败,请求方法:{}，token:{},异常",request.getServletPath(),authToken, e);
                return;
            }
        } else {
            //header没有带Bearer字段
        	RenderUtil.renderJson(response, new BaseResponse(BizExceptionEnum.TOKEN_ERROR));
            log.error("token解析失败,未获取到token,请求方法:{},请求头:{}",request.getServletPath(),requestHeader);
            return;
        }
        chain.doFilter(request, response);
    }
}