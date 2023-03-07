package com.bookSuppliment.server.app;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bookSuppliment.server.app.dtos.RecaptchaResponse;
import com.bookSuppliment.server.app.entity.User;

import com.bookSuppliment.server.app.repository.UserRepository;
import com.bookSuppliment.server.app.service.GoogleRecaptchaService;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityTests {
	
	@MockBean
	private UserRepository userRepository;
	
    @Mock
    private RecaptchaResponse recaptchaResponse;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void emailPasswordAuthenticationFilter_requestWithValidUserCredentials_userIsAuthenticated() throws Exception {		
		when(recaptchaResponse.isSuccess()).thenReturn(true);
		
		User user = new User();
		user.setUsername("anton");
	    user.setEmail("test@gmail.com");
	    user.setPassword("testpassword");
   
	    Optional<User> optinalUser = Optional.ofNullable(user);
		when(userRepository.findByEmail("test@gmail.com")).thenReturn(optinalUser);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/login")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("email", "test@gmail.com")
				.param("password", "testpassword");
		
		//	Moving from /login to / means the request was successfully authenticated
		this.mockMvc.perform(request.with(csrf())).andExpect(status().isFound());
	}
	
	@Test
	public void emailPasswordAuthenticationFilter_requestWithInvalidUserCredentials_userIsNotAuthenticated() throws Exception {
		when(recaptchaResponse.isSuccess()).thenReturn(true);
		
		User user = new User();
		user.setUsername("anton");
	    user.setEmail("test@gmail.com");
	    user.setPassword("testpassword");
   
	    Optional<User> optinalUser = Optional.ofNullable(user);
		when(userRepository.findByEmail("test@gmail.com")).thenReturn(optinalUser);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/login")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("email", "fail@gmail.com")
				.param("password", "invalidpassword");
		
		this.mockMvc.perform(request.with(csrf())).andExpect(status().isFound());
	}
}
