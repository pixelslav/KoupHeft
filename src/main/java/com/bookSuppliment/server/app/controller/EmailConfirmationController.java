package com.bookSuppliment.server.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bookSuppliment.server.app.dtos.CodeConfirmation;
import com.bookSuppliment.server.app.dtos.RegistrationForm;
import com.bookSuppliment.server.app.entity.User;
import com.bookSuppliment.server.app.repository.UserRepository;
import com.bookSuppliment.server.app.service.EmailRegistrationService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class EmailConfirmationController {
	
	private UserRepository userRepository;
	
	public EmailConfirmationController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@GetMapping("/registration/confirmation")
	public String getEmailConfirmationForm(@ModelAttribute(name="code") CodeConfirmation code, HttpServletRequest request) {
		
		if (request.getSession().getAttribute("registration_form") == null &&
			request.getSession().getAttribute("confirmation_code") == null
		) {
			return "redirect:/registration";
		}
		
		return "email-confirmation";
	}
	
	@PostMapping("/registration/confirmation")
	public String processCode(@ModelAttribute(name="code") CodeConfirmation code, HttpServletRequest request) {
		if (request.getSession().getAttribute("confirmation_code") != code.getCode()) {
			return "email-confirmation";
		}
		
		RegistrationForm form = (RegistrationForm) request.getSession().getAttribute("registration_form");
		
		User newUser = new User();
		newUser.setUsername(form.getName());
		newUser.setEmail(form.getName());
		newUser.setPassword(form.getPassword());
		
		userRepository.save(newUser);
		
		request.getSession().removeAttribute("confirmation_code");
		request.getSession().removeAttribute("registration_form");
		
		return "redirection:/registration/confirmation/success";
	}
}
