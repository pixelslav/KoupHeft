package com.bookSuppliment.server.app.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.bookSuppliment.server.app.entity.User;

public interface UserRepository extends CrudRepository<User, Long>{
	
	public Optional<User> findByEmail(String email);
}
