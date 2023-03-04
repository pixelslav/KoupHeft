package com.bookSuppliment.server.app.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bookSuppliment.server.app.entity.User;
import com.bookSuppliment.server.app.repository.UserRepository;

public class UserDetailsEmailService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
//	public UserDetailsEmailService(UserRepository userRepository) {
//		this.userRepository = userRepository;
//	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }
		return user.get();
	}
	
	
}
