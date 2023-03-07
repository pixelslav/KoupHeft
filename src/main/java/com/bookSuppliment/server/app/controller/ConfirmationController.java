package com.bookSuppliment.server.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bookSuppliment.server.app.dtos.ConfirmationCode;
import com.bookSuppliment.server.app.dtos.RegistrationForm;
import com.bookSuppliment.server.app.entity.User;
import com.bookSuppliment.server.app.repository.UserRepository;
import com.bookSuppliment.server.app.service.EmailRegistrationService;
import com.bookSuppliment.server.app.service.SessionService;
import com.bookSuppliment.server.app.service.DefaultUserDetailsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ConfirmationController {
	
	// TODO Having found a better logging option, move the logger to a separate place
	private static final Logger logger = LoggerFactory.getLogger(ConfirmationController.class);
    
	private UserRepository userRepository;
	private SessionService sessionService;
	
	public ConfirmationController(
			UserRepository userRepository,
			SessionService sessionService
	) {
		this.userRepository = userRepository;
		this.sessionService = sessionService;
	}
	
	@GetMapping("/registration/confirmation")
	public String getEmailConfirmationForm(@ModelAttribute(name="form_code") ConfirmationCode code, HttpServletRequest request) {
		
		if (request.getSession().getAttribute("registration_form") == null &&
			request.getSession().getAttribute("confirmation_code") == null
		) {
			logger.debug("The session has not registration form and confirmation code");
			return "redirect:/registration";
		}
		
		logger.debug("The session has registration form and confirmation code");
		return "email-confirmation";
	}
	
	@PostMapping("/registration/confirmation")
	public String processCode(@ModelAttribute(name="form_code") ConfirmationCode formCode, HttpServletRequest request, BindingResult bindingResult) {
		if (!request.getSession().getAttribute("confirmation_code").equals(formCode.getCode())) {
			logger.debug(String.format(
					"The code in the session with value %s and the code in the form with value %s do not match",
					request.getSession().getAttribute("confirmation_code"), formCode.getCode()
			));
			bindingResult.addError(new FieldError("product_code", "code", "Code ist ungültig"));
			return "email-confirmation";
		}
		HttpSession session = request.getSession();
		
		RegistrationForm form = (RegistrationForm) session.getAttribute("registration_form");
		User newUser = createUserFromRegistrationForm(form);
		userRepository.save(newUser);
		
		sessionService.removeAtrributeFromSession(session, "registration_form");
		sessionService.removeAtrributeFromSession(session, "confirmation_code");
		
		return "registration-successfully";
	}
	
	private User createUserFromRegistrationForm(RegistrationForm registrationForm) {
		User userFromRegistration = new User();
		userFromRegistration.setUsername(registrationForm.getName());
		userFromRegistration.setEmail(registrationForm.getEmail());
		userFromRegistration.setPassword(registrationForm.getPassword());
		
		logger.debug(String.format(
				"User with value %s has been created from registration form",
				userFromRegistration.toString()
				));
		
		return userFromRegistration;
	}
}
