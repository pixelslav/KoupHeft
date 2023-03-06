package com.bookSuppliment.server.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ConfirmationController {
	
	// TODO Having found a better logging option, move the logger to a separate place
	private static final Logger logger = LoggerFactory.getLogger(ConfirmationController.class);
    
	private UserRepository userRepository;
	
	public ConfirmationController(UserRepository userRepository) {
		this.userRepository = userRepository;
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
		
		RegistrationForm form = (RegistrationForm) request.getSession().getAttribute("registration_form");
		User newUser = createUserFromRegistrationForm(form);
		userRepository.save(newUser);
		
		HttpSession session = request.getSession();
		removeRegistrationFormFromSession(session);
		removeConfirmationCodeFromSession(session);
		
		return "registration-successfully";
	}
	
	private void removeRegistrationFormFromSession(HttpSession session) {
		session.removeAttribute("registration_form");
		logger.debug("The registration form has been removed from the session");
	}
	
	private void removeConfirmationCodeFromSession(HttpSession session) {
		session.removeAttribute("confirmation_code");
		logger.debug("The confirmation code has been removed from the session");
	}
	
	private User createUserFromRegistrationForm(RegistrationForm form) {
		User userFromRegistration = new User();
		userFromRegistration.setUsername(form.getName());
		userFromRegistration.setEmail(form.getEmail());
		userFromRegistration.setPassword(form.getPassword());
		
		logger.debug(String.format(
				"User with value %s has been created from registration form in the session",
				userFromRegistration.toString()
				));
		
		return userFromRegistration;
	}
}
