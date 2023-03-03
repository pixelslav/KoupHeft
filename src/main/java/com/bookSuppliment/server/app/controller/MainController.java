package com.bookSuppliment.server.app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bookSuppliment.server.app.entity.Notebook;
import com.bookSuppliment.server.app.entity.Product;
import com.bookSuppliment.server.app.entity.User;
import com.bookSuppliment.server.app.repository.UserRepository;
import com.bookSuppliment.server.app.service.ProductManager;

@Controller
public class MainController {
	
	private final UserRepository userRepository;
	
	private final ProductManager productManager;
	
	public MainController (UserRepository userRepository, ProductManager productManager) {
		this.userRepository = userRepository;
		this.productManager = productManager;
	}
	
	@GetMapping("/")
	public String getMainPage(Authentication authentication, Model model) {
		
		String usersEmailFromAuthentication = authentication.getPrincipal().toString();
		User user = userRepository.findByEmail(usersEmailFromAuthentication).get();
		
		Set<Product> userProducts = user.getProducts();

		if (userProducts.isEmpty()) {
			
			return "main-page-without-products";
		}
		
		model.addAttribute("products", user.getProducts());
		model.addAttribute("productManager", productManager);

		return "main-page-with-products";
	}
	
}
