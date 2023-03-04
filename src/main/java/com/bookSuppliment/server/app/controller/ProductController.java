package com.bookSuppliment.server.app.controller;

import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.bookSuppliment.server.app.dtos.ProductCodeForm;
import com.bookSuppliment.server.app.entity.Product;
import com.bookSuppliment.server.app.entity.ProductCode;
import com.bookSuppliment.server.app.entity.User;
import com.bookSuppliment.server.app.repository.ProductCodeRepository;
import com.bookSuppliment.server.app.repository.ProductRepository;
import com.bookSuppliment.server.app.repository.UserRepository;

import jakarta.validation.Valid;

@Controller
public class ProductController {
	
	private final UserRepository userRepository;
	private final ProductCodeRepository productCodeRepository;
	private final ProductRepository productRepository; 
	
	public ProductController (
			UserRepository userRepository,
			ProductCodeRepository productCodeRepository,
			ProductRepository productRepository
		) {
		this.userRepository = userRepository;
		this.productCodeRepository = productCodeRepository;
		this.productRepository = productRepository;
	}
	
	@GetMapping("products/add")
	public String getProductPage(@ModelAttribute("product_code") ProductCodeForm productCodeForm) {
		return "product-add";
	}
	
	@PostMapping("/products/add")
	public String addProductToUser(@Valid @ModelAttribute("product_code") ProductCodeForm productCodeForm, BindingResult bindingResult, Authentication authentication) {
		Optional<ProductCode> productCode = productCodeRepository.findByCode(productCodeForm.getCode());
		
		if (!productCode.isPresent()) {
	        bindingResult.addError(new FieldError("product_code", "code", "Der eingegebene Produktcode ist ungültig"));
	        return "product-add";
	    }
		
		// userPrincipalInAuthentication is a User object with a closed session with the database,
		// so it cannot lazy retrieve the data that is associated with this object
		User userPrincipal = (User) authentication.getPrincipal();
		String usersEmail = userPrincipal.getEmail();
		User user = userRepository.findByEmail(usersEmail).get();
		
		Product product = productCode.get().getProduct();
		
		if (user.getProducts().contains(product)) {
			bindingResult.addError(new FieldError("product_code", "code", "Sie haben dieses Produkt bereits"));
			return "product-add";
		}
		
//		user.getProducts().add(product);
		product.addUser(user);
//		userRepository.save(user);
		productRepository.save(product);
		
		return "redirect:/products/add/successfully";
	}
	
	@GetMapping("/products/add/successfully")
	public String getSuccessfullyPage() {
		return "product-successfully";
	}
}
