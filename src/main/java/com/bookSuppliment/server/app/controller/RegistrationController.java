package com.bookSuppliment.server.app.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.bookSuppliment.server.app.entity.User;
import com.bookSuppliment.server.app.repository.UserRepository;
import com.bookSuppliment.server.app.service.EmailRegistrationService;
import com.bookSuppliment.server.app.service.GoogleRecaptchaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class RegistrationController {
	
	// TODO Having found a better logging option, move the logger to a separate place
	private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
	
	private GoogleRecaptchaService googleRecaptchaService;
	private EmailRegistrationService emailRegistrationService;
	private ConfirmationCodeGenerator confirmationCodeGenerator;
	private UserRepository userRepository;
	
	public RegistrationController(
			EmailRegistrationService emailRegistrationService,
			GoogleRecaptchaService googleRecaptchaService,
			ConfirmationCodeGenerator confirmationCodeGenerator,
			UserRepository userRepository
	) {
		this.emailRegistrationService = emailRegistrationService;
		this.googleRecaptchaService = googleRecaptchaService;
		this.confirmationCodeGenerator = confirmationCodeGenerator;
		this.userRepository = userRepository;
	}
	
	@GetMapping("/registration")
	public String getRegisterForm(@ModelAttribute("form") RegistrationForm form) {
		return "registration-form";
	}
	
	@PostMapping("/registration")
	public String registerUser(@Valid @ModelAttribute("form") RegistrationForm form, BindingResult bindingResult, HttpServletRequest request) {
		
		if (bindingResult.hasErrors()) {
			logger.debug("Registration data is incorrect");
			logger.debug("Registration form returns");
			
			return "registration-form";
		}
		
		if (!isRecaptchaValid(request)) {
			logger.debug("The recaptcha is incorrect");
			logger.debug("Registration form returns");
			
			return "registration-form";
		}
		
		if (isSuchUserAlreadyExist(form)) {
			logger.debug("Such user is already exist");
			logger.debug("Registration form returns");
			
			bindingResult.addError(new FieldError("form", "email", "Benutzer mit dieser E-Mail-Adresse existiert bereits"));
			return "registration-form";
		}
		
		logger.debug("Registration request is correct");
		
		String confirmationCode = confirmationCodeGenerator.generate();
		
		HttpSession session = request.getSession();
		setRegistrationFormToSession(session, form);
		setConfirmationCodeToSession(session, confirmationCode);
		
		sendConfirmationCodeToUser(form.getEmail(), confirmationCode);
		
		return "redirect:/registration/confirmation";
	}
	
	private boolean isSuchUserAlreadyExist(RegistrationForm form) {
		Optional<User> foundUser = userRepository.findByEmail(form.getEmail());
		return foundUser.isPresent();
	}
	
	private void sendConfirmationCodeToUser(String email, String confirmationCode) {
		emailRegistrationService.sendRegistrationConfirmationCodeToEmail(email, confirmationCode);
		logger.debug("The message with confirmation code has sended to the user");
	}
	
	private void setRegistrationFormToSession(HttpSession session, RegistrationForm registrationForm) {
		session.setAttribute("registration_form", registrationForm);
		logger.debug(String.format("The registration form with value %s had setted to the session", registrationForm.toString()));
	}
	
	private void setConfirmationCodeToSession(HttpSession session, String confirmationCode) {
		session.setAttribute("confirmation_code", confirmationCode);
		logger.debug(String.format("The confirmation code with value \"%s\" had setted to the session", confirmationCode));
	}
	
	private boolean isRecaptchaValid(HttpServletRequest request) {
	    String recaptchaUserToken = request.getParameter("g-recaptcha-response");
	    RecaptchaResponse recaptchaResponse = googleRecaptchaService.getRecaptchaResponseForToken(recaptchaUserToken);
	    return recaptchaResponse.isSuccess();
	}
}
