package com.bookSuppliment.server.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.client.RestTemplate;

import com.bookSuppliment.server.app.filter.EmailPasswordAuthenticationFilter;
import com.bookSuppliment.server.app.filter.EmailPasswordAuthenticationProvider;
import com.bookSuppliment.server.app.repository.UserRepository;
import com.bookSuppliment.server.app.service.GoogleRecaptchaService;

@Configuration
public class KoupHeftSecurityConfiguration {
	
	@Autowired
	private UserRepository userRepository;
	
	@Bean
	public AuthenticationManager authenticationManager() {
		return new EmailPasswordAuthenticationProvider(userRepository);
	}
	
	@Bean
	public EmailPasswordAuthenticationFilter authenticationFilter() {
		return new EmailPasswordAuthenticationFilter(authenticationManager());
	}
	
	@Bean
	public FilterRegistrationBean < EmailPasswordAuthenticationFilter > filterRegistrationBean() {
		  FilterRegistrationBean < EmailPasswordAuthenticationFilter > registrationBean = new FilterRegistrationBean<>(authenticationFilter());
		  registrationBean.setEnabled(false);
		  
		  return registrationBean;
		 }
	
	@Bean
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeRequests(authorize -> authorize
					.requestMatchers("/products/*").authenticated()
					.requestMatchers("/").authenticated()
			)
			.formLogin(form -> form 
					.loginPage("/login")
					.permitAll()
			)
			.securityContext((securityContext) -> securityContext
					.requireExplicitSave(false)
			)
			.addFilterBefore(
					authenticationFilter(),
					UsernamePasswordAuthenticationFilter.class
			)
			;
		return http.build();
	}
}
