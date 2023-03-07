package com.bookSuppliment.server.app;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices.RememberMeTokenAlgorithm;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.client.RestTemplate;

import com.bookSuppliment.server.app.filter.EmailPasswordAuthenticationFilter;
import com.bookSuppliment.server.app.filter.EmailPasswordAuthenticationProvider;
import com.bookSuppliment.server.app.filter.SimpleAuthenticationManager;
import com.bookSuppliment.server.app.repository.UserRepository;
import com.bookSuppliment.server.app.service.GoogleRecaptchaService;
import com.bookSuppliment.server.app.service.DefaultUserDetailsService;

@Configuration
public class KoupHeftSecurityConfiguration {
	
	@Value("${remeber-service.secret-key}")
	private String secretKeyRememberService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Bean
	public AuthenticationManager authenticationManager() {
		ArrayList<AuthenticationProvider> providersList = new ArrayList<AuthenticationProvider>();
		providersList.add(new EmailPasswordAuthenticationProvider(userRepository));
		
		SimpleAuthenticationManager manager = new SimpleAuthenticationManager();
		manager.setAuthenticationProviders(providersList);
		return manager;
	}
	
	@Bean
	public EmailPasswordAuthenticationFilter authenticationFilter() {
		SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
		failureHandler.setDefaultFailureUrl("/login?error");
		
		EmailPasswordAuthenticationFilter filter = new EmailPasswordAuthenticationFilter(authenticationManager());
		filter.setRememberMeServices(rememberMeServices());
		filter.setAuthenticationFailureHandler(failureHandler);
		return filter;
	}
	
	@Bean
	public FilterRegistrationBean < EmailPasswordAuthenticationFilter > filterRegistrationBean() {
		  FilterRegistrationBean < EmailPasswordAuthenticationFilter > registrationBean = new FilterRegistrationBean<>(authenticationFilter());
		  registrationBean.setEnabled(false);
		  
		  return registrationBean;
		 }
	
	@Bean
	public DefaultUserDetailsService userDetailsService() {
		return new DefaultUserDetailsService();
	}
	
	@Bean
	RememberMeServices rememberMeServices() {
		TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices(secretKeyRememberService, userDetailsService());
		rememberMe.setAlwaysRemember(true);
		return rememberMe;
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
			.logout().logoutUrl("/logout").deleteCookies("JSESSIONID").deleteCookies("remember-me")
			.and()
			.rememberMe((remember) -> remember
					.userDetailsService(userDetailsService())
					.key(secretKeyRememberService)
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
