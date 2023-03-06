package com.bookSuppliment.server.app;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.bookSuppliment.server.app.component.ConfirmationCodeGenerator;
import com.bookSuppliment.server.app.dtos.RecaptchaResponse;
import com.bookSuppliment.server.app.entity.User;
import com.bookSuppliment.server.app.repository.UserRepository;
import com.bookSuppliment.server.app.service.EmailRegistrationService;
import com.bookSuppliment.server.app.service.GoogleRecaptchaService;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class IntegrationRegistrationTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private EmailRegistrationService emailRegistrationService;
	
	@MockBean
	private GoogleRecaptchaService googleRecaptchaService;
	
	@MockBean
	private ConfirmationCodeGenerator confirmationCodeGenerator;
	
    @Mock
    private RecaptchaResponse recaptchaResponse;
    
    @Autowired
    private UserRepository userRepository;
	
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:11")
      .withDatabaseName("testbase")
      .withUsername("postgres")
      .withPassword("pass")
      .withExposedPorts(5432);

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> String.format("jdbc:postgresql://localhost:%d/testbase", postgres.getFirstMappedPort()));
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "pass");
    }
    
	@Test
	public void registrationController_passedValidRegistrationForm_userAddedToDatabase() throws Exception{
		when(recaptchaResponse.isSuccess()).thenReturn(true);
		when(googleRecaptchaService.getRecaptchaResponseForToken("test")).thenReturn(recaptchaResponse);
		
		when(confirmationCodeGenerator.generate()).thenReturn("test");
		
		MockHttpSession session = new MockHttpSession();
		
		MockHttpServletRequestBuilder requestToRegistration = MockMvcRequestBuilders.post("/registration")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.session(session)
				.param("name", "Barack Obama")
				.param("email", "test@gmail.com")
				.param("password", "testpassword")
				.param("g-recaptcha-response", "test");
		
		MockHttpServletRequestBuilder requestToConfirmation = MockMvcRequestBuilders.post("/registration/confirmation")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.session(session)
				.param("code", "test");
		
		this.mockMvc.perform(requestToRegistration.with(csrf())).andExpect(status().isFound());
		
		this.mockMvc.perform(requestToConfirmation.with(csrf())).andExpect(status().isOk());
		
		Optional<User> userThatMustToBeSaved = userRepository.findByEmail("test@gmail.com");
		
		assertTrue(userThatMustToBeSaved.isPresent());
	}
}
