package top.woodwhale.blog.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailService {

    /**
     * 自动装配JavaMailSender类
     */
    @Autowired
    private JavaMailSender mailSender;

    /**
     * 配置中的发送邮箱的地址
     */
    @Value("${spring.mail.username}")
    private String sendFrom;

    /**
     * 异步发送邮箱验证码
     *
     * @param to   发送邮箱地址
     * @param code 验证码
     */
    @Async
    public void sendVerificationCodeEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sendFrom);
        message.setTo(to);
        message.setSubject("WBS博客系统注册验证码");
        message.setText("欢迎使用WBS博客系统，您的验证码是【" + code + "】，有效期为5分钟，若非本人操作，请忽略此邮件！");
        mailSender.send(message);
    }

    @Async
    public void sendCommentNotify(String to, String notifyContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sendFrom);
        message.setTo(to);
        message.setSubject("WBS博客系统评论通知");
        message.setText("来自WBS博客系统的评论通知：\n\t" + notifyContent);
        mailSender.send(message);
    }
}
