package com.cursoTDD.libraryapi.api.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Pageable;

import com.cursoTDD.libraryapi.api.dto.BookDTO;
import com.cursoTDD.libraryapi.api.entity.BookEntity;
import com.cursoTDD.libraryapi.api.exception.ApiErrors;
import com.cursoTDD.libraryapi.api.service.BookService;
import com.cursoTDD.libraryapi.exception.BusinessException;

@RestController
@RequestMapping("/api/books")
public class BookController {
	
	@Autowired
	BookService bookService;
	
	@Autowired
	ModelMapper modelMapper;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookDTO create(@RequestBody @Valid BookDTO bookDTO) {
		
		BookEntity book = modelMapper.map(bookDTO, BookEntity.class);
									
		book = bookService.save(book);

		return modelMapper.map(book, BookDTO.class);
		
	}
	
	@GetMapping("/{id}")
	public BookDTO get(@PathVariable long id) {
		
		return bookService.getById(id)
								.map(bookEntity ->  modelMapper.map(bookEntity, BookDTO.class))
								.orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));	
		
	}
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable long id) {
		
		BookEntity bookEntity = bookService.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));	
		
		bookService.delete(bookEntity);
	}
	
	@PutMapping("/{id}")
	public BookDTO put(@PathVariable long id , BookDTO bookDTO) {
		
		BookEntity bookEntity = bookService.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));	
		
		bookEntity.setAuthor(bookDTO.getAuthor());
		bookEntity.setTitle(bookDTO.getTitle());
		bookEntity = bookService.update(bookEntity);
		return modelMapper.map(bookEntity, BookDTO.class);
		
	}
	
	@GetMapping
	public Page<BookDTO> find(BookDTO bookDTO, Pageable pageRequest) {
		
		BookEntity filter = modelMapper.map(bookDTO, BookEntity.class);
		Page<BookEntity> result = bookService.find(filter, pageRequest);
		List<BookDTO> bookList = result.getContent().stream().map(entity -> modelMapper.map(entity , BookDTO.class)).collect(Collectors.toList());
		
		return new PageImpl<BookDTO>(bookList,  pageRequest, result.getTotalElements());
		
	}
	
}
