package com.cursoTDD.libraryapi.api.service.impl;


import org.springframework.stereotype.Service;

import com.cursoTDD.libraryapi.api.entity.BookEntity;
import com.cursoTDD.libraryapi.api.repository.BookRepository;
import com.cursoTDD.libraryapi.api.service.BookService;
import com.cursoTDD.libraryapi.exception.BusinessException;

@Service
public class BookServiceImpl implements BookService {


	BookRepository bookRepository;
	

	public BookServiceImpl(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}




	@Override
	public BookEntity save(BookEntity bookEntity) {
		if(bookRepository.existsByIsbn(bookEntity.getIsbn())) {
			throw new BusinessException("Isbn já cadastrado");
		}
		return bookRepository.save(bookEntity);
	}

}
