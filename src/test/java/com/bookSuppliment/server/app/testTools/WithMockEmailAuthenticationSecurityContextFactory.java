package com.bookSuppliment.server.app.testTools;

import java.util.Arrays;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.bookSuppliment.server.app.entity.User;
import com.bookSuppliment.server.app.filter.EmailPasswordAuthenticationToken;

public class WithMockEmailAuthenticationSecurityContextFactory
	implements WithSecurityContextFactory<WithMockUserEmailAuthentication> {
	
	@Override
	public SecurityContext createSecurityContext(WithMockUserEmailAuthentication userData) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		
		User user = new User();
		user.setUsername(userData.username());
		user.setEmail(userData.email());
		user.setPassword("password");
		
		Authentication auth =
			new EmailPasswordAuthenticationToken(user, user.getPassword(), Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
		context.setAuthentication(auth);
		return context;
	}

}
