package com.bookSuppliment.server.app.repository;

import org.springframework.data.repository.CrudRepository;

import com.bookSuppliment.server.app.entity.BookCode;
import com.bookSuppliment.server.app.entity.User;

public interface BookCodeRepository extends CrudRepository<BookCode, Long>{

}
