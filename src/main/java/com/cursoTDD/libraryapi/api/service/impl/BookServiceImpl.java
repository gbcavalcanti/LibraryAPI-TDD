package com.cursoTDD.libraryapi.api.service.impl;


import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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




	@Override
	public Optional<BookEntity> getById(long id) {
		return bookRepository.findById(id);
	}




	@Override
	public void delete(BookEntity bookEntity) {
		if (bookEntity == null || bookEntity.getId() == null) throw new IllegalArgumentException("id do livro não pode ser nulo");
		bookRepository.delete(bookEntity);
		
	}


	@Override
	public BookEntity update(BookEntity bookEntity) {
		if (bookEntity == null || bookEntity.getId() == null) throw new IllegalArgumentException("id do livro não pode ser nulo");
		BookEntity newBook =  bookRepository.save(bookEntity);
		return newBook;
	}




	@Override
	public Page<BookEntity> find(BookEntity filter, Pageable pageRequest) {
	
		Example<BookEntity> example = Example.of(filter,
													ExampleMatcher
													.matching()
													.withIgnoreCase()
													.withIgnoreNullValues()
													.withStringMatcher(StringMatcher.CONTAINING));
		// TODO Auto-generated method stub
		return bookRepository.findAll(example, pageRequest);
	}




	

}
