package com.bookSuppliment.server.app.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bookSuppliment.server.app.dtos.RegistrationForm;
import com.bookSuppliment.server.app.entity.User;
import com.bookSuppliment.server.app.repository.UserRepository;

public class DefaultUserDetailsService implements UserDetailsService {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultUserDetailsService.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }
		return user.get();
	}
	
	public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByUsername(email);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }
		return user.get();
	}
	
	public boolean isUserWithSuchEmailExist(String email) {
		Optional<User> foundUser = userRepository.findByEmail(email);
		return foundUser.isPresent();
	}	
}
