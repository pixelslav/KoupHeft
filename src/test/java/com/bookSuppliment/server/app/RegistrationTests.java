package com.bookSuppliment.server.app;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import com.bookSuppliment.server.app.component.ConfirmationCodeGenerator;
import com.bookSuppliment.server.app.controller.ConfirmationController;
import com.bookSuppliment.server.app.controller.RegistrationController;
import com.bookSuppliment.server.app.dtos.ConfirmationCode;
import com.bookSuppliment.server.app.dtos.RecaptchaResponse;
import com.bookSuppliment.server.app.dtos.RegistrationForm;
import com.bookSuppliment.server.app.entity.User;
import com.bookSuppliment.server.app.repository.UserRepository;
import com.bookSuppliment.server.app.service.EmailRegistrationService;
import com.bookSuppliment.server.app.service.GoogleRecaptchaService;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
    private EmailRegistrationService emailRegistrationService;

    @MockBean
    private GoogleRecaptchaService googleRecaptchaService;

    @Mock
    private RecaptchaResponse recaptchaResponse;
    
    @MockBean
    private UserRepository userRepository;
    
	@Test
	public void registrationForm_passedValidRegistrationForm_setFormToSession() throws Exception {
		// Arrange
        when(recaptchaResponse.isSuccess()).thenReturn(true);
        when(googleRecaptchaService.getRecaptchaResponseForToken("test")).thenReturn(recaptchaResponse);
           
        RegistrationForm form = new RegistrationForm();
        form.setName("Barack Obama");
        form.setEmail("test@gmail.com");
        form.setPassword("testpassword");
        
		MockHttpSession session = new MockHttpSession();
        
        MockHttpServletRequestBuilder requestToRegistration = MockMvcRequestBuilders.post("/registration")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.session(session)
				.param("name", "Barack Obama")
				.param("email", "test@gmail.com")
				.param("password", "testpassword")
				.param("g-recaptcha-response", "test");
        
        // Act
		this.mockMvc.perform(requestToRegistration.with(csrf())).andExpect(status().isFound());
        
        // Assert
        RegistrationForm formInSession = (RegistrationForm) session.getAttribute("registration_form");
        
        assertAll("registration",
                () -> assertTrue(form.equals(formInSession)),
                () -> assertNotNull(session.getAttribute("confirmation_code"))
        );
	}
	
	@Test
	public void registrationForm_passedInvalidRegistrationForm_sessionIsNullAndThereAreErrors() throws Exception {
		// Arrange
        when(recaptchaResponse.isSuccess()).thenReturn(true);
        when(googleRecaptchaService.getRecaptchaResponseForToken("test")).thenReturn(recaptchaResponse);
        
		MockHttpSession session = new MockHttpSession();
        
        MockHttpServletRequestBuilder requestToRegistration = MockMvcRequestBuilders.post("/registration")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.session(session)
				.param("name", "")
				.param("email", "123")
				.param("password", "41")
				.param("g-recaptcha-response", "test");
        
        // Act
        this.mockMvc.perform(requestToRegistration.with(csrf())).andExpect(status().isOk());
        
        // Assert
        assertTrue(session.getAttribute("registration_form") == null);
        assertTrue(session.getAttribute("registration_code") == null);
    }
	
	@Test
	public void codeConformation_passedValidCode_sessionIsClearUserIsSaved() throws Exception {
		// Arrange
        RegistrationForm form = new RegistrationForm();
        form.setName("Barack Obama");
        form.setEmail("test@test.com");
        form.setPassword("password123");
		
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("registration_form", form);
		session.setAttribute("confirmation_code", "test");
		
        MockHttpServletRequestBuilder requestToRegistration = MockMvcRequestBuilders.post("/registration/confirmation")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.session(session)
				.param("code", "test");
        
		// Act
		this.mockMvc.perform(requestToRegistration.with(csrf())).andExpect(status().isOk());
		
		// Assert
		assertThat(session.getAttribute("registration_form")).isNull();
		assertThat(session.getAttribute("confirmation_code")).isNull();
		
		verify(userRepository).save(any(User.class));
	}
}
