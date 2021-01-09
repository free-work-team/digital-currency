package com.jyt.terminal.controller.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.jyt.terminal.config.properties.BitProperties;
import com.jyt.terminal.util.EncryptionUtils;
import com.jyt.terminal.util.FileUtil;
import com.jyt.terminal.util.VerifyCodeUtils;

/**
 * 生成验证码
 * @className KaptchaController
 * @author wangwei
 * @date 2019年4月29日
 *
 */
@Controller
@RequestMapping("/kaptcha")
public class KaptchaController {

    @Autowired
    private BitProperties bitProperties;

    @Autowired
    private Producer producer;

    /**
     * 生成验证码
     */
    @RequestMapping("")
    public void index(HttpServletRequest request, HttpServletResponse response) {
    	response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		// 生成随机字串
		String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
		// 存入会话session
		// 存入会话session
		HttpSession session = request.getSession(true);
		// 删除以前的
		session.removeAttribute(Constants.KAPTCHA_SESSION_KEY);
		session.setAttribute(Constants.KAPTCHA_SESSION_KEY, verifyCode.toLowerCase());
		// 生成图片
		int w = 130, h = 50;
		try {
			VerifyCodeUtils.outputImage(w, h, response.getOutputStream(),verifyCode);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    	
    	
    }

    /**
     * 返回图片
     * @param pictureId
     * @param response
     */
    @RequestMapping("/{pictureId}")
    public void renderPicture(@PathVariable("pictureId") String pictureId, HttpServletResponse response) {
        String path = bitProperties.getFileUploadPath() + pictureId;
        try {
            byte[] bytes = FileUtil.toByteArray(path);
            response.getOutputStream().write(bytes);
        } catch (Exception e) {
            //如果找不到图片就返回一个默认图片
            try {
                response.sendRedirect("/static/img/girl.gif");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    
    
}
