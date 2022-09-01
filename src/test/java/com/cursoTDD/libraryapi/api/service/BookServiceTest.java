package com.cursoTDD.libraryapi.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.springframework.data.domain.Example;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
	@Test
	@DisplayName("deve obter um livro por id")
	public void getByIdTest() {
		long id = 11;
		
		BookEntity bookEntity = createValidBook();
		bookEntity.setId(id);
		
		Mockito.when(bookRepository.findById(id)).thenReturn(Optional.of(bookEntity));
		
		Optional<BookEntity> foundBook =  bookService.getById(id);
		
		
		assertThat(foundBook.isPresent()).isTrue();
		assertThat(foundBook.get().getId()).isEqualTo(id);
		assertThat(foundBook.get().getTitle()).isEqualTo(bookEntity.getTitle());
		assertThat(foundBook.get().getAuthor()).isEqualTo(bookEntity.getAuthor());
		assertThat(foundBook.get().getIsbn()).isEqualTo(bookEntity.getIsbn());
		
		
		
	}
	
	@Test
	@DisplayName("deve retornar vazio ao obter um livro por id quando ele nao existe na base")
	public void BookNotFoundByIdTest() {
		long id = 11;
		
		Mockito.when(bookRepository.findById(id)).thenReturn(Optional.empty());
		
		Optional<BookEntity> book =  bookService.getById(id);
		
		
		assertThat ( book.isPresent()).isFalse();
	
	
	}
	
	@Test
	@DisplayName("Deve deletar um livro pelo seu id")
	public void deleteBookTest() {
		
		BookEntity bookEntity = BookEntity.builder().id((long) 11).build();
	
		org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> bookRepository.delete(bookEntity) );
		
		Mockito.verify(bookRepository, Mockito.times(1)).delete(bookEntity);
		
	}
	
	@Test
	@DisplayName("Deve retornar um erro ao tentar deletar um livro invalido")
	public void deleteInvalidBook() {
		
		BookEntity bookEntity = new BookEntity();
		
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.delete(bookEntity));
		
		Mockito.verify(bookRepository, Mockito.never()).delete(bookEntity);
	
	}
	
	@Test
	@DisplayName("Deve atualizar um livro pelo seu id")
	public void updateBookTest() {
		
		long id = 11;
		
		BookEntity oldBook = BookEntity.builder().id(id).build();
		
		BookEntity newBook = createValidBook();
		newBook.setId(id);
		
		Mockito.when(bookRepository.save(oldBook)).thenReturn(newBook);
		
		BookEntity savedBook = bookService.update(oldBook);
		
		assertThat(savedBook.getId()).isEqualTo(newBook.getId());
		assertThat(savedBook.getTitle()).isEqualTo(newBook.getTitle());
		assertThat(savedBook.getAuthor()).isEqualTo(newBook.getAuthor());
		assertThat(savedBook.getIsbn()).isEqualTo(newBook.getIsbn());
	}
	
	@Test
	@DisplayName("Deve retornar um erro ao tentar deletar um livro invalido")
	public void updateInvalidBook() {
		
		BookEntity bookEntity = new BookEntity();
		
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.update(bookEntity));
		
		Mockito.verify(bookRepository, Mockito.never()).save(bookEntity);
	}
	
	@Test
	@DisplayName("deve filtrar livros pelas propriedades")
	public void findBookTest() {
		
		BookEntity book = createValidBook();
		
		PageRequest pageRequest = PageRequest.of(0 , 10);
		
		List<BookEntity> lista = Arrays.asList(book);
		
		Page<BookEntity> page = new PageImpl<BookEntity>(Arrays.asList(book), PageRequest.of(0, 10), 1);
		
		Mockito.when(bookRepository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);
		
		Page<BookEntity> result = bookService.find(book, pageRequest);
		
		
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).isEqualTo(lista);
		assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(result.getPageable().getPageSize()).isEqualTo(10);
		
		
		
	}
	
}
