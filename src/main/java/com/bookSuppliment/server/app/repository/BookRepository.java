package com.bookSuppliment.server.app.repository;

import org.springframework.data.repository.CrudRepository;

import com.bookSuppliment.server.app.entity.Book;

public interface BookRepository extends CrudRepository<Book, Long>{

}
