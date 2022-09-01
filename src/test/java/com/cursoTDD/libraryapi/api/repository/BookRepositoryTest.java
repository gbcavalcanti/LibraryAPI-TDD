package com.cursoTDD.libraryapi.api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import com.cursoTDD.libraryapi.api.entity.BookEntity;



@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

	
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	BookRepository bookRepository;
	
	@Test
	@DisplayName("Deve retornar verdadeiro quando existir um livro com o isbn informado")
	public void returnTrueWhenIsbnExists() {
		//cenario
		
		String isbn = "123";
		BookEntity bookEntity = createNewBook(isbn);
		entityManager.persist(bookEntity);
		//execucao
		
		boolean exists = bookRepository.existsByIsbn(isbn);
		
		//verificacao
		
		assertThat(exists).isTrue();
	}
	
	
	private BookEntity createNewBook(String isbn) {
		return BookEntity.builder().author("Artur").title("As aventuras").isbn(isbn).build();
	}
	
	@Test
	@DisplayName("Deve retornar falso quando não existir um livro com o isbn informado")
	public void returnFalseWhenIsbnDoesntExists() {
		//cenario
		
		String isbn = "123";

		//execucao
		
		boolean exists = bookRepository.existsByIsbn(isbn);
		
		//verificacao
		
		assertThat(exists).isFalse();
	}
	
	@Test
	@DisplayName("deve obter um livro por id")
	public void findByIdTest() {
		
		BookEntity bookEntity =  createNewBook("123");
		
		entityManager.persist(bookEntity);
		
		
		Optional<BookEntity>  foundBook =  bookRepository.findById(bookEntity.getId());
		
		assertThat(foundBook.isPresent()).isTrue();
	}
}
