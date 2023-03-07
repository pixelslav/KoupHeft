package com.bookSuppliment.server.app.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bookSuppliment.server.app.component.ConfirmationCodeGenerator;
import com.bookSuppliment.server.app.dtos.RecaptchaResponse;
import com.bookSuppliment.server.app.dtos.RegistrationForm;
import com.bookSuppliment.server.app.dtos.SessionAttribute;
import com.bookSuppliment.server.app.entity.User;
import com.bookSuppliment.server.app.repository.UserRepository;
import com.bookSuppliment.server.app.service.EmailRegistrationService;
import com.bookSuppliment.server.app.service.GoogleRecaptchaService;
import com.bookSuppliment.server.app.service.SessionService;
import com.bookSuppliment.server.app.service.DefaultUserDetailsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class RegistrationController {
	
	// TODO Having found a better logging option, move the logger to a separate place
	private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
	
	@Autowired
	private ConfirmationCodeGenerator confirmationCodeGenerator;
	
	private EmailRegistrationService emailRegistrationService;
	private DefaultUserDetailsService userDetailsService;
	private SessionService sessionService;
	
	public RegistrationController(
			EmailRegistrationService emailRegistrationService,
			SessionService sessionService,
			DefaultUserDetailsService userDetailsService
	) {
		this.emailRegistrationService = emailRegistrationService;
		this.sessionService = sessionService;
		this.userDetailsService = userDetailsService;
	}
	
	@GetMapping("/registration")
	public String getRegisterForm(@ModelAttribute("form") RegistrationForm form) {
		return "registration-form";
	}
	
	@PostMapping("/registration")
	public String registerUser(@Valid @ModelAttribute("form") RegistrationForm form, BindingResult bindingResult, HttpServletRequest request) {
		
		if (bindingResult.hasErrors()) {
			logger.debug("Registration data is incorrect");
			return "registration-form";
		}
		
		if (userDetailsService.isUserWithSuchEmailExist(form.getEmail())) {
			logger.debug("Such user is already exist");
			bindingResult.addError(new FieldError("form", "email", "Benutzer mit dieser E-Mail-Adresse existiert bereits"));
			return "registration-form";
		}
		logger.debug("Registration request is correct");
		
		String confirmationCode = confirmationCodeGenerator.generate();
		
		HttpSession session = request.getSession();
		SessionAttribute formAttribute = new SessionAttribute("registration_form", form);
		SessionAttribute codeAttribute = new SessionAttribute("confirmation_code", confirmationCode);
		sessionService.addAttributeToSession(session, formAttribute);
		sessionService.addAttributeToSession(session, codeAttribute);
		
		sendConfirmationCodeToUser(form.getEmail(), confirmationCode);
		
		return "redirect:/registration/confirmation";
	}
	
	private void sendConfirmationCodeToUser(String email, String confirmationCode) {
		emailRegistrationService.sendRegistrationConfirmationCodeToEmail(email, confirmationCode);
		logger.debug("The message with confirmation code has sended to the user");
	}
}
