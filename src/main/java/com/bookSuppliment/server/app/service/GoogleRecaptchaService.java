package com.bookSuppliment.server.app.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bookSuppliment.server.app.dtos.RecaptchaResponse;

@Component
public class GoogleRecaptchaService {
	
    @Value("${captcha.google.recaptcha.secret-key}")
    private String RecaptchaSecretKey;
    
    @Value("${captcha.google.recaptcha.public-key}")
    private String RecaptchaPublicKey;
    
    private final RestTemplate restTemplate;
    
    public GoogleRecaptchaService(RestTemplate restTemplate) {
    	this.restTemplate = restTemplate;
    }
    
	public RecaptchaResponse getRecaptchaResponseForToken(String recaptchaUserToken) {
    	
    	Map<String, Object> urlParameters = new HashMap<String, Object>();
    	urlParameters.put("secret", RecaptchaSecretKey);
    	urlParameters.put("response", recaptchaUserToken);
    	
    	RecaptchaResponse response = restTemplate.postForObject(
    					"https://www.google.com/recaptcha/api/siteverify?secret={secret}&response={response}",
    					null,
    					RecaptchaResponse.class,
    					urlParameters
    	);
    	
    	return response;
    }
}
