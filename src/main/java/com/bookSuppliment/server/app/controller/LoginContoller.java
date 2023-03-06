package com.bookSuppliment.server.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginContoller {
	
	@GetMapping("/login")
	public String getLoginPage() {
		return "login-form";
	}
	
	@GetMapping("/logout")
	public String getLogoutPage() {
		return "logout-form";
	}
	
}
