package com.bookSuppliment.server.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bookSuppliment.server.app.dtos.RegistrationForm;
import com.bookSuppliment.server.app.dtos.SessionAttribute;

import jakarta.servlet.http.HttpSession;

@Service
public class SessionService {
	
	private static final Logger logger = LoggerFactory.getLogger(SessionService.class);
	
	public void addAttributeToSession(HttpSession session, SessionAttribute attribute) {
		session.setAttribute(attribute.getAttributeName(), attribute.getAtrributeValue());
		logger.debug(String.format(
				"Parameter \"%s\" with value \"%s\" had setted to the session",
				attribute.getAttributeName(),
				attribute.getAtrributeValue().toString()
		));
	}
	
	public void removeAtrributeFromSession(HttpSession session, String attributeName) {
		session.removeAttribute(attributeName);
		logger.debug(String.format(
				"Parameter \"%s\" has been removed from the session",
				attributeName
		));
	}
}
