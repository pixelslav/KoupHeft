package com.bookSuppliment.server.app.controller;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bookSuppliment.server.app.component.CategoryFilterComponent;
import com.bookSuppliment.server.app.entity.Notebook;
import com.bookSuppliment.server.app.entity.Product;
import com.bookSuppliment.server.app.entity.User;
import com.bookSuppliment.server.app.repository.UserRepository;

@Controller
public class PersonalProfileController {
	
	private final UserRepository userRepository;
	
	private final CategoryFilterComponent productFilter;
	
	public PersonalProfileController (UserRepository userRepository, CategoryFilterComponent productManager) {
		this.userRepository = userRepository;
		this.productFilter = productManager;
	}
	
	@GetMapping("/")
	public String getProfilePage(Authentication authentication, Model model) {
		
		// userPrincipalInAuthentication is a User object with a closed session with the database,
		// so it cannot lazy retrieve the data that is associated with this object
		User userPrincipal = (User) authentication.getPrincipal();
		String usersEmail = userPrincipal.getEmail();
		User user = userRepository.findByEmail(usersEmail).get();
		
		Set<Product> userProducts = user.getProducts();

		if (userProducts.isEmpty()) {
			
			return "main-page-without-products";
		}
		
		// productProcessor is used to check if a certain category exists and filter products.
		// It is important to include it in the context of Thymeleaf
		model.addAttribute("products", user.getProducts());
		model.addAttribute("productFilter", productFilter);

		return "main-page-with-products";
	}
	
}
