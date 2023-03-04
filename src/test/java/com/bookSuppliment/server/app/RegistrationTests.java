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
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
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
class RegistrationTests {
	
	@Mock
    private EmailRegistrationService emailRegistrationService;

    @Mock
    private GoogleRecaptchaService googleRecaptchaService;

    @Mock
    private RecaptchaResponse recaptchaResponse;
    
    @Mock
    private UserRepository userRepository;
    
    @Autowired
    private ConfirmationCodeGenerator confirmationCodeGenerator;
	
    @Autowired
    private Validator validator;
    
	@Test
	public void registrationForm_passedValidRegistrationForm_setFormToSession() {
		// Arrange
        when(recaptchaResponse.isSuccess()).thenReturn(true);
        when(googleRecaptchaService.getRecaptchaResponseForToken("code")).thenReturn(recaptchaResponse);
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("g-recaptcha-response", "code");
        
        RegistrationForm form = new RegistrationForm();
        form.setName("Barack Obama");
        form.setEmail("test@test.com");
        form.setPassword("password123");
        
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "registration_form");
        validator.validate(form, bindingResult);
        
        RegistrationController registrationController = new RegistrationController(
        		emailRegistrationService,
        		googleRecaptchaService,
        		confirmationCodeGenerator
        );
        
        // Act
        registrationController.registerUser(form, bindingResult, request);
        
        // Assert
        RegistrationForm formInSession = (RegistrationForm) request.getSession().getAttribute("registration_form");
        
        assertAll("registration",
                () -> assertSame(form, request.getSession().getAttribute("registration_form")),
                () -> assertNotNull(request.getSession().getAttribute("confirmation_code"))
        );
	}
	
	@Test
	public void registrationForm_passedInvalidRegistrationForm_sessionIsNullAndThereAreErrors() {
		// Arrange
        when(recaptchaResponse.isSuccess()).thenReturn(true);
        when(googleRecaptchaService.getRecaptchaResponseForToken("code")).thenReturn(recaptchaResponse);
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("g-recaptcha-response", "code");
        
        RegistrationForm form = new RegistrationForm();
        form.setName("");
        form.setEmail("uehejdjjs");
        form.setPassword("1233isisiej");
        
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "registration_form");
        validator.validate(form, bindingResult);
        
        RegistrationController registrationController = new RegistrationController(
        		emailRegistrationService,
        		googleRecaptchaService,
        		new ConfirmationCodeGenerator()
        );
        
        // Act
        registrationController.registerUser(form, bindingResult, request);
        
        // Assert
        assertTrue(request.getSession().getAttribute("registration_form") == null);
        assertTrue(request.getSession().getAttribute("registration_code") == null);
        assertTrue(bindingResult.hasErrors());
    }
	
	@Test
	public void codeConformation_passedValidCode_sessionIsClearUserIsSaved() {
		// Arrange
		ConfirmationController confirmationController = new ConfirmationController(userRepository);
		
        RegistrationForm form = new RegistrationForm();
        form.setName("Barack Obama");
        form.setEmail("test@test.com");
        form.setPassword("password123");
        
		ConfirmationCode confirmationCode = new ConfirmationCode();
		confirmationCode.setCode("ASDFGJKLOF");
		
		MockHttpServletRequest requestWithConfirmationCode = new MockHttpServletRequest();
		requestWithConfirmationCode.getSession().setAttribute("registration_form", form);
		requestWithConfirmationCode.getSession().setAttribute("confirmation_code", confirmationCode.getCode());
		
		// Act
		
		confirmationController.processCode(confirmationCode, requestWithConfirmationCode);
		
		// Assert
		
		assertThat(requestWithConfirmationCode.getSession().getAttribute("registration_form")).isNull();
		assertThat(requestWithConfirmationCode.getSession().getAttribute("confirmation_code")).isNull();
		
		verify(userRepository).save(any(User.class));
	}
}
