package com.jyt.terminal.auth.converter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.jyt.terminal.auth.security.DataSecurityAction;
import com.jyt.terminal.auth.util.JwtTokenUtil;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.config.properties.JwtProperties;
import com.jyt.terminal.exception.TerminalException;
import com.jyt.terminal.support.HttpKit;
import com.jyt.terminal.util.MD5Util;



/**
 * 带签名的http信息转化器
 *
 * @author fengshuonan
 * @date 2017-08-25 15:42
 */
public class WithSignMessageConverter extends FastJsonHttpMessageConverter {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private DataSecurityAction dataSecurityAction;

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {

        InputStream in = inputMessage.getBody();
        Object o = JSON.parseObject(in, super.getFastJsonConfig().getCharset(), BaseTransferEntity.class, super.getFastJsonConfig().getFeatures());

        //先转化成原始的对象
        BaseTransferEntity baseTransferEntity = (BaseTransferEntity) o;

        //校验签名
        String requestHeader = HttpKit.getRequest().getHeader(jwtProperties.getHeader());
        String token =requestHeader.substring(7);
        String md5KeyFromToken = jwtTokenUtil.getMd5KeyFromToken(token);  
        String object = baseTransferEntity.getObject();
        String json = dataSecurityAction.unlock(object);
        String encrypt = MD5Util.encrypt(object + md5KeyFromToken);

        if (encrypt.equals(baseTransferEntity.getSign())) {//验签成功
        		
        	JSONObject jsonobj=JSONObject.parseObject(json);
        	
        	String termNo = jwtTokenUtil.getUsernameFromToken(token);//获取用户信息 
        	
             jsonobj.put("termNo",termNo);
         
        	 //校验签名后再转化成应该的对象
        	return jsonobj.toJavaObject(type);
        	
        } else {     
            throw new TerminalException(BizExceptionEnum.SIGN_ERROR);
        }

       
    }
}
