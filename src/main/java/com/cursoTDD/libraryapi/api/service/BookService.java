package com.cursoTDD.libraryapi.api.service;

import java.util.Optional;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.cursoTDD.libraryapi.api.entity.BookEntity;

public interface BookService {

	BookEntity save(BookEntity bookEntity);

	Optional<BookEntity> getById(long id);

	void delete(BookEntity bookEntity);

	BookEntity update(BookEntity bookEntity);

	Page<BookEntity> find(BookEntity filter, Pageable pageRequest);

	Optional<BookEntity> getBookByIsbn(String isbn); 
		

}
