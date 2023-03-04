package com.bookSuppliment.server.app;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;

import com.bookSuppliment.server.app.component.ProductCodeGenerator;
import com.bookSuppliment.server.app.entity.Notebook;
import com.bookSuppliment.server.app.entity.ProductCode;
import com.bookSuppliment.server.app.entity.User;
import com.bookSuppliment.server.app.repository.ProductCodeRepository;
import com.bookSuppliment.server.app.repository.ProductRepository;
import com.bookSuppliment.server.app.repository.UserRepository;

@Configuration
public class KoupHeftConfiguration {
	
	@Value("${email-sender.email}")
    private String emailToSendMessages;
	
	@Value("${email-sender.password}")
    private String emailPassword;
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	public JavaMailSender getJavaMailSender() {
	    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	    mailSender.setHost("smtp.gmail.com");
	    mailSender.setPort(465);
	    
	    mailSender.setUsername(emailToSendMessages);
	    mailSender.setPassword(emailPassword);
	    
	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.ssl.enable", "true");
	    props.put("mail.debug", "true");
	    
	    return mailSender;
	}
	
//	@Bean
//	public ApplicationRunner dataLoader(
//			ProductRepository productRepository,
//			ProductCodeRepository productCodeRepository,
//			UserRepository userRepository,
//			ProductCodeGenerator productCodeGenerator
//	) {
//		return args -> {
//			Notebook testNotebook = new Notebook();
//			testNotebook.setTitle("Test Excercise Notebook");
//			productRepository.save(testNotebook);
//			
//			ProductCode productCode = new ProductCode();
//		    productCode.setCode(productCodeGenerator.generate());
//		    productCode.setProduct(testNotebook);
//		    productCodeRepository.save(productCode);
//		    
//			User user = new User();
//			user.setUsername("anton");
//		    user.setEmail("test@gmail.com");
//		    user.setPassword("testpassword");
//		    userRepository.save(user);
//		};
//	}
}
