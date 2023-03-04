package com.bookSuppliment.server.app.filter;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bookSuppliment.server.app.entity.User;
import com.bookSuppliment.server.app.repository.UserRepository;

public class EmailPasswordAuthenticationProvider implements AuthenticationProvider {
	
	private final UserRepository userRepository;
	
	public EmailPasswordAuthenticationProvider(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();

        if (email.isEmpty() || password.isEmpty()) {
            throw new AuthenticationServiceException("Email or password is empty");
        }

        Optional<User> loadedUser = loadUser(email);

        if (!loadedUser.isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }
        
        User user = loadedUser.get(); 

        if (!password.equals(user.getPassword())) {
            throw new AuthenticationServiceException("Incorrect password");
        }

        return new EmailPasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(EmailPasswordAuthenticationToken.class);
    }

    public Optional<User> loadUser(String email) throws UsernameNotFoundException {
    	return userRepository.findByEmail(email);
    }
}
