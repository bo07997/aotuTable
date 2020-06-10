package com.baitian.autotable.service.mail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author ldb
 * @Package com.baitian.autotable.service.mail.service
 * @date 2020/6/10 14:39
 */
@Service
public class MailService {

	@Autowired
	private JavaMailSenderImpl mailSender;

	public void send(String content) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom("aox@aobi.com");
		// 收件人邮箱
		helper.setTo(new String[] { "projectxdev@aobi.com", "projectxtest@aobi.com" });
		//		helper.setTo("liudianbo@aobi.com");
		// 标题
		helper.setSubject("自动导表通知邮件");
		// 正文
		helper.setText(content, true);
		// 发送
		mailSender.send(message);
	}
}
