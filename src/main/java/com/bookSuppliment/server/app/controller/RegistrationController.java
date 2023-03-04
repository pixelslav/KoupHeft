package com.bookSuppliment.server.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bookSuppliment.server.app.component.ConfirmationCodeGenerator;
import com.bookSuppliment.server.app.dtos.RecaptchaResponse;
import com.bookSuppliment.server.app.dtos.RegistrationForm;
import com.bookSuppliment.server.app.service.EmailRegistrationService;
import com.bookSuppliment.server.app.service.GoogleRecaptchaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class RegistrationController {
	
	private GoogleRecaptchaService googleRecaptchaService;
	private EmailRegistrationService emailRegistrationService;
	private ConfirmationCodeGenerator confirmationCodeGenerator;
	
	public RegistrationController(
			EmailRegistrationService emailRegistrationService,
			GoogleRecaptchaService googleRecaptchaService,
			ConfirmationCodeGenerator confirmationCodeGenerator
	) {
		this.emailRegistrationService = emailRegistrationService;
		this.googleRecaptchaService = googleRecaptchaService;
		this.confirmationCodeGenerator = confirmationCodeGenerator;
	}
	
	@GetMapping("/registration")
	public String getRegisterForm(@ModelAttribute("form") RegistrationForm form) {
		return "registration-form";
	}
	
	@PostMapping("/registration")
	public String registerUser(@Valid @ModelAttribute("form") RegistrationForm form, BindingResult bindingResult, HttpServletRequest request) {
		String codeForConfirmation = confirmationCodeGenerator.generate();
		
		if (bindingResult.hasErrors()) {
			return "registration-form";
		}
		
		if (!isRecaptchaValid(request)) {
			return "registration-form";
		}
		
		HttpSession session = request.getSession();
		
		session.setAttribute("registration_form", form);
		session.setAttribute("confirmation_code", codeForConfirmation);
		
		emailRegistrationService.sendRegistrationConfirmationCodeToEmail(form.getEmail(), codeForConfirmation);
		
		return "redirect:/registration/confirmation";
	}
	
	private boolean isRecaptchaValid(HttpServletRequest request) {
	    String recaptchaUserToken = request.getParameter("g-recaptcha-response");
	    RecaptchaResponse recaptchaResponse = googleRecaptchaService.getRecaptchaResponseForToken(recaptchaUserToken);
	    return recaptchaResponse.isSuccess();
	}
}
