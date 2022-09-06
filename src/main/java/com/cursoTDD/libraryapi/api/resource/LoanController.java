package com.cursoTDD.libraryapi.api.resource;

import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cursoTDD.libraryapi.api.dto.LoanDTO;
import com.cursoTDD.libraryapi.api.entity.BookEntity;
import com.cursoTDD.libraryapi.api.entity.LoanEntity;
import com.cursoTDD.libraryapi.api.service.BookService;
import com.cursoTDD.libraryapi.api.service.LoanService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/loans")
@RequiredArgsConstructor
public class LoanController {
	
	
	private final LoanService loanService;
	
	private final BookService bookService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Long create(@RequestBody LoanDTO loanDTO) {
		
		BookEntity book = bookService
							.getBookByIsbn(loanDTO.getIsbn())
							.orElseThrow(() -> 
							new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
	
		LoanEntity loanEntity = LoanEntity.builder()
											.book(book)
											.costumer(loanDTO.getCostumer())
											.loanDate(LocalDate.now())
											.build();
		
		loanEntity = loanService.save(loanEntity);		
		
		return loanEntity.getId();	
	}
	
}
