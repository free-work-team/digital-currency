package com.jyt.bitcoinmaster.email;
 
import android.support.annotation.NonNull;

import org.apache.log4j.Logger;

import java.io.File;
 
/**
 * Created by Administrator on 2017/4/10.
 */
 
public class SendMailUtil {
    //qq
//    private static final String HOST = "smtp.qq.com";
//    private static final String PORT = "587";
//    private static final String FROM_ADD = "494129773@qq.com"; //发送方邮箱
//    private static final String FROM_PSW = "legovaxcvoaubghi";//发送方邮箱授权码

    private static final String HOST = "smtp.163.com";
    private static final String PORT = "25";
    private static final String FROM_ADD = "szwmyao@163.com"; //发送方邮箱
    private static final String FROM_PSW = "M3Purpose6";//发送方邮箱授权码

    private static Logger log = Logger.getLogger("BitCoinMaster");
 
    public static boolean send(final File file,String toAdd,String subject,String content){
        final MailInfo mailInfo = creatMail(toAdd,subject,content);
        final MailSender sms = new MailSender();
        log.info("发送邮件日志文件("+file.getName()+")给 ========"+toAdd);
        boolean flag = sms.sendFileMail(mailInfo,file);
        log.info("发送邮件结果 ========"+flag);
        // 删掉zip
        file.delete();
        return flag;
    }
 
    public static void send(String toAdd,String subject,String content){
        final MailInfo mailInfo = creatMail(toAdd,subject,content);
        final MailSender sms = new MailSender();
        log.info("发送邮件给 ========"+toAdd);
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = sms.sendTextMail(mailInfo);
                log.info("发送邮件结果 ========"+flag);
            }
        }).start();
    }
 
    @NonNull
    private static MailInfo creatMail(String toAdd,String subject,String content) {
        final MailInfo mailInfo = new MailInfo();
        mailInfo.setMailServerHost(HOST);
        mailInfo.setMailServerPort(PORT);
        mailInfo.setValidate(false);
        mailInfo.setUserName(FROM_ADD); // 你的邮箱地址
        mailInfo.setPassword(FROM_PSW);// 您的邮箱密码
        mailInfo.setFromAddress(FROM_ADD); // 发送的邮箱
        mailInfo.setToAddress(toAdd); // 发到哪个邮件去
        mailInfo.setSubject(subject); // 邮件主题
        mailInfo.setContent(content); // 邮件文本
        return mailInfo;
    }
}
