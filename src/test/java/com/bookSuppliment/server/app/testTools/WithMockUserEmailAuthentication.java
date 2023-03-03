package com.bookSuppliment.server.app.testTools;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockEmailAuthenticationSecurityContextFactory.class)
public @interface WithMockUserEmailAuthentication {
	String email() default "test@gmail.com";
}
