package com.bookSuppliment.server.app.repository;

import org.springframework.data.repository.CrudRepository;

import com.bookSuppliment.server.app.entity.Product;

public interface ProductRepository extends CrudRepository<Product, Long>{

}
