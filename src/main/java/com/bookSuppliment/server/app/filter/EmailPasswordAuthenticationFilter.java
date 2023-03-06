package com.bookSuppliment.server.app.filter;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bookSuppliment.server.app.dtos.RecaptchaResponse;
import com.bookSuppliment.server.app.service.GoogleRecaptchaService;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class EmailPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	private static final Logger logger = Logger.getLogger(EmailPasswordAuthenticationFilter.class.getName());
	
	public static final String SPRING_SECURITY_FORM_EMAIL_KEY = "email";
	
	private boolean postOnly = true;
	
	private String emailParameter = SPRING_SECURITY_FORM_EMAIL_KEY;
	
	@Autowired
    private GoogleRecaptchaService googleRecaptchaService;
	
	public EmailPasswordAuthenticationFilter() {
		super();
		this.googleRecaptchaService = googleRecaptchaService;
	}
	
	public EmailPasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		if (postOnly && !request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}
		
		String email = obtainEmail(request);
		String password = obtainPassword(request);
		
		if (!isRecaptchaValid(request)) {
			throw new AuthenticationServiceException("Captcha is invalid");
		}
		
		EmailPasswordAuthenticationToken authRequest = new EmailPasswordAuthenticationToken(email,
				password);
		setDetails(request, authRequest);
		return this.getAuthenticationManager().authenticate(authRequest);
	}
	
	private boolean isRecaptchaValid(HttpServletRequest request) {
	    String recaptchaUserToken = request.getParameter("g-recaptcha-response");
	    RecaptchaResponse recaptchaResponse = googleRecaptchaService.getRecaptchaResponseForToken(recaptchaUserToken);
	    return recaptchaResponse.isSuccess();
	}

	protected void setDetails(HttpServletRequest request, EmailPasswordAuthenticationToken authRequest) {
		authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
	}
    
	@Nullable
	protected String obtainEmail(HttpServletRequest request) {
		return request.getParameter(emailParameter);
	}
}
