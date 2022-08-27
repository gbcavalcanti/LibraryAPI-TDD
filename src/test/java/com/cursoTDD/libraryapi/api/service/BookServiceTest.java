package com.cursoTDD.libraryapi.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cursoTDD.libraryapi.api.entity.BookEntity;
import com.cursoTDD.libraryapi.api.repository.BookRepository;
import com.cursoTDD.libraryapi.api.service.impl.BookServiceImpl;
import com.cursoTDD.libraryapi.exception.BusinessException;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

	
	BookService bookService;
	
	@MockBean
	BookRepository bookRepository;
	
	@BeforeEach
	public void setUp() {
		this.bookService = new BookServiceImpl( bookRepository );
		
	}
	
	@Test
	@DisplayName("Deve salvar um livro")
	public void saveBookTest() {
		//cenario
		BookEntity book = createValidBook();
		Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(false);
		Mockito.when(bookRepository.save(book))
			   .thenReturn(
					   BookEntity.builder()
					   			 .id((long) 11)
					   			 .title("As aventuras")
					   			 .author("Fulano")
					   			 .isbn("123")
					   			 .build()
					   	  );
		//execução
		BookEntity savedBook =  bookService.save(book);
		
		
		//verificação
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
		assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
		assertThat(savedBook.getIsbn()).isEqualTo("123");
		
	}

	private BookEntity createValidBook() {
		return BookEntity.builder().author("Fulano").title("As aventuras").isbn("123").build();
	}
	
	@Test
	@DisplayName("Deve Lançar um erro de negocio ao tentar salvar um livro com isbn duplicado")
	public void shoudNotSaveABookWithDuplicatedIsbn() {
		//cenario 
		BookEntity book = createValidBook();
		Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);
		//execucao
		
		Throwable exception =  Assertions.catchThrowable(() -> bookService.save(book));
		
		assertThat(exception)
				.isInstanceOf(BusinessException.class)
				.hasMessage("Isbn já cadastrado");
		
		Mockito.verify(bookRepository, Mockito.never()).save(book);
		
	}
	
	
}
