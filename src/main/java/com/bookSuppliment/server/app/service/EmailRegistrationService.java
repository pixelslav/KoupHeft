package com.bookSuppliment.server.app.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailRegistrationService {
	
	@Value("${email-sender.email}")
    private String emailToSendMessages;
	
	private JavaMailSender javaMailSender;
	private SpringTemplateEngine springTemplateEngine;
	
	private final String NAME_OF_CONFIRMATION_CODE_TEMPLATE = "confirmation.html";
	
	public EmailRegistrationService(JavaMailSender javaMailSender, SpringTemplateEngine springTemplateEngine) {
		this.javaMailSender = javaMailSender;
		this.springTemplateEngine = springTemplateEngine;
	}
	
	public void sendRegistrationConfirmationCodeToEmail(String email, String code) {
		String templatesContent = loadConfirmationTemplateForCode(code);
		
		MimeMessage message = createConfirmationMessage(email, templatesContent);
		
		javaMailSender.send(message);
	}
	
	private MimeMessage createConfirmationMessage(String email, String contentInHtml) {
		MimeMessage message = javaMailSender.createMimeMessage();
	    MimeMessageHelper helper = null;
		try {
			helper = new MimeMessageHelper(message, true);
			helper.setFrom(emailToSendMessages);
		    helper.setTo(email);
		    helper.setSubject("Confirm your account!");
		    helper.setText(contentInHtml, true);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		return message;
	}
	
	private String loadConfirmationTemplateForCode(String code) {
		Context context = new Context();
		context.setVariables(Map.of("code", code));
		
		return springTemplateEngine.process(NAME_OF_CONFIRMATION_CODE_TEMPLATE, context);
	}
}
