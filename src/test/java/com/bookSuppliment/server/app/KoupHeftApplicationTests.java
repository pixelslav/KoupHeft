package com.bookSuppliment.server.app;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.bookSuppliment.server.app.component.ProductCodeGenerator;
import com.bookSuppliment.server.app.dtos.RecaptchaResponse;
import com.bookSuppliment.server.app.entity.Notebook;
import com.bookSuppliment.server.app.entity.Product;
import com.bookSuppliment.server.app.entity.ProductCode;
import com.bookSuppliment.server.app.entity.User;
import com.bookSuppliment.server.app.repository.ProductCodeRepository;
import com.bookSuppliment.server.app.repository.ProductRepository;
import com.bookSuppliment.server.app.repository.UserRepository;
import com.bookSuppliment.server.app.testTools.WithMockUserEmailAuthentication;

@SpringBootTest
@AutoConfigureMockMvc
class KoupHeftApplicationTests {
	
	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private ProductCodeRepository productCodeRepository;
	
	@MockBean
	private ProductRepository productRepository;
	
	@Autowired
	private ProductCodeGenerator productCodeGenerator;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	@WithMockUserEmailAuthentication
	void productCodeFormRegistration_passedValidProductCode_productAddedToUser() throws Exception {
		String wellKnowsCode = productCodeGenerator.generate();
		
		User user = new User();
		user.setUsername("anton");
	    user.setEmail("test@gmail.com");
	    user.setPassword("testpassword");
	    
	    Optional<User> optUser = Optional.ofNullable(user);
	    
	    Notebook notebook = new Notebook();
	    notebook.setTitle("Test Excersice Notebook");
	    
	    ProductCode productCode = new ProductCode();
	    productCode.setCode(wellKnowsCode);
	    productCode.setProduct(notebook);
	    
	    Optional<ProductCode> optProductCode = Optional.ofNullable(productCode);
	    
	    when(userRepository.findByEmail("test@gmail.com")).thenReturn(optUser);
	    when(productCodeRepository.findByCode(wellKnowsCode)).thenReturn(optProductCode);
	    
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/products/add")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("code", wellKnowsCode);
		
		this.mockMvc.perform(request.with(csrf())).andExpect(status().isFound());
		
		assertAll("products",
				() -> assertNotNull(user.getProducts()),
                () -> assertTrue(user.getProducts().contains(notebook))
        );
	    
	}
}
