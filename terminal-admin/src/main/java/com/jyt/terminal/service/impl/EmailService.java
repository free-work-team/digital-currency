package com.jyt.terminal.service.impl;

import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.jyt.terminal.model.EmailSetting;
import com.jyt.terminal.util.EncryptionUtils;
import com.jyt.terminal.util.ToolUtil;

@Component
public class EmailService {
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	/*@Autowired
	private  JavaMailSender javaMailSender;*/
	@Autowired
	private JavaMailSenderImpl  javaMailSender;
	
	@Value("${spring.mail.username}")
	private String sender;
	
	@Async(value="emailThreadPool")
	public void sendMail(String template,String subject,Map<String, Object>map,String email,EmailSetting emailSetting) {
		logger.info("开始发送主题为{}，邮件给{}",subject,email);
		try {
			String text = getTextByTemplate(template, map);
			send(email,subject, text,true,emailSetting);
		} catch (Exception e) {
			logger.warn("邮件发送错误",e);
		} 
	}

	private String getTextByTemplate(String template, Map<String, Object>model) throws Exception {
		return FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerConfigurer.getConfiguration().getTemplate(template), model);
	}

	private String send(String email,String subject, String text,boolean htmlFlag,EmailSetting emailSetting) throws Exception {
		MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(sender);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(text,htmlFlag);
        if(ToolUtil.isNotEmpty(emailSetting)){
        	helper.setFrom(emailSetting.getEmail());
        	javaMailSender.setHost(emailSetting.getHost());
        	javaMailSender.setUsername(emailSetting.getEmail());
        	javaMailSender.setPassword(EncryptionUtils.decrypt(emailSetting.getPassword()));
        }
		javaMailSender.send(message);
		return text;
	}
}

