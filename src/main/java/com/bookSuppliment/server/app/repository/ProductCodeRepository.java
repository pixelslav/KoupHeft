package com.bookSuppliment.server.app.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.bookSuppliment.server.app.entity.ProductCode;

public interface ProductCodeRepository extends CrudRepository<ProductCode, Long>{
	
	public Optional<ProductCode> findByCode(String code);
}
