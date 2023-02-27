package com.bookSuppliment.server.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginContoller {
	
	@GetMapping("/login")
	public String getLoginTestPage() {
		return "login-form";
	}
	
}
