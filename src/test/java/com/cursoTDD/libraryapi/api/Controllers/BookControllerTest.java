package com.cursoTDD.libraryapi.api.Controllers;


import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.cursoTDD.libraryapi.api.dto.BookDTO;
import com.cursoTDD.libraryapi.api.entity.BookEntity;
import com.cursoTDD.libraryapi.api.resource.BookController;
import com.cursoTDD.libraryapi.api.service.BookService;
import com.cursoTDD.libraryapi.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;




@ActiveProfiles("test")
@WebMvcTest(controllers = {BookController.class})
public class BookControllerTest {

	static String BOOK_API = "/api/books";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	BookService bookService;
	
	@Test
	@DisplayName("Deve criar um livro com sucesso")
	public void createBookTest() throws Exception {
		
		BookDTO bookDTO = BookDTO.builder().author("Artur").title("As aventuras").isbn("001").build();
		
		BookEntity savedBook = BookEntity.builder().id((long) 11).author("Artur").title("As aventuras").isbn("001").build();
		
		BDDMockito.given(bookService.save(Mockito.any(BookEntity.class))).willReturn(savedBook);
		
		
		String json = new ObjectMapper().writeValueAsString(bookDTO);
				
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
					.post(BOOK_API)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(json);
		
		mvc
			.perform(request)
			.andExpect(status().isCreated() )
			.andExpect(jsonPath("id").value(11) )
			.andExpect(jsonPath("title").value(bookDTO.getTitle()) )
			.andExpect(jsonPath("author").value(bookDTO.getAuthor()) )
			.andExpect(jsonPath("isbn").value(bookDTO.getIsbn()) ) 
			;
		
		
	}
	
	@Test
	@DisplayName("Deve lançar erro de validação quando não houver dados suficientes")
	public void createInvalidBookTest() throws Exception {
		
		String json = new ObjectMapper().writeValueAsString(new BookDTO());
				
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
					.post(BOOK_API)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(json);
		
		mvc.perform(request)
				 .andExpect(status().isBadRequest())
				 .andExpect(jsonPath("errors", hasSize(3)));
		
		
	}
	
	
	
	@Test
	@DisplayName("Deve lançar erro ao tentar cadastrar livro com isbn repetido")
	public void createBookWithDuplicatedIsbn() throws Exception{
		
		BookDTO bookDTO = createNewBook();
		
		
		String json = new ObjectMapper().writeValueAsString(bookDTO);
		
		String mensagemErro = "isbn ja cadastrado";
				
		BDDMockito.given(bookService.save(Mockito.any(BookEntity.class))).willThrow(new BusinessException(mensagemErro));
		
		
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
					.post(BOOK_API)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(json);
		
		
		mvc.perform(request)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("errors", hasSize(1)))
				.andExpect(jsonPath("errors[0]").value(mensagemErro));
		
		}
	
	
	@Test
	@DisplayName("Deve obter informações de um livro")
	public void getBookDetaisTest() throws Exception {
		long id = 11;
		
		BookEntity bookEntity = BookEntity.builder()
												.id(id)
												.title(createNewBook().getTitle())
												.author(createNewBook().getAuthor())
												.isbn(createNewBook().getIsbn())
												.build();
		
		BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(bookEntity));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(BOOK_API.concat("/" + id))
				.accept(MediaType.APPLICATION_JSON);
		
		
		
		mvc.perform(request)
						.andExpect(status().isOk())
						.andExpect(jsonPath("id").value(id))
						.andExpect(jsonPath("title").value(createNewBook().getTitle()) )
						.andExpect(jsonPath("author").value(createNewBook().getAuthor()) )
						.andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));
				
		
	}
	
	
	@Test
	@DisplayName("Deve retornar resource not found quando o livro procurado nao existir")
	public void bookNotFoundTest() throws Exception{
	
		BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(BOOK_API.concat("/" + 1))
				.accept(MediaType.APPLICATION_JSON);
		
		mvc.perform(request)
			.andExpect(status().isNotFound());
		
	}
	
	@Test
	@DisplayName("Deve deletar um livro")
		public void deleteBookTest()  throws Exception{
		
		BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.of(BookEntity.builder().id((long) 11).build()));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(BOOK_API.concat("/" + 1))
				.accept(MediaType.APPLICATION_JSON);
		
		
		mvc.perform(request).andExpect(status().isNoContent());
	}
	
	
	@Test
	@DisplayName("Deve retornar resource not found quando nao encontrar o livro para deletar")
		public void deleteInexistentBookTest()  throws Exception{
		
		BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(BOOK_API.concat("/" + 1))
				.accept(MediaType.APPLICATION_JSON);
		
		
		mvc.perform(request).andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("deve atualizar um livro")
	public void updateBookTest() throws Exception{
		
		long id = 11;
		
		String json = new ObjectMapper().writeValueAsString(createNewBook());
	
		BookEntity updateBook = BookEntity.builder().id((long) 11).title("title").author("author").isbn("321").build();
		
		BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(updateBook));
		
		BookEntity updatedBook =  BookEntity.builder().id(id).author("Artur").title("As aventuras").isbn("321").build();
		
		BDDMockito.given(bookService.update(updateBook)).willReturn(updatedBook);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.put(BOOK_API.concat("/" + id))
				.content(json)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		
		
		
		mvc.perform(request)
						.andExpect(status().isOk())
						.andExpect(jsonPath("id").value(id))
						.andExpect(jsonPath("title").value(createNewBook().getTitle()) )
						.andExpect(jsonPath("author").value(createNewBook().getAuthor()) )
						.andExpect(jsonPath("isbn").value("321"));
				
		
	}
	
	@Test 
	@DisplayName("deve retornar resource not found ao tentat atualizar livro inexistente")
	public void updateInexistentBookTest() throws Exception{
		
		String json = new ObjectMapper().writeValueAsString(createNewBook());
			
		BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.put(BOOK_API.concat("/" + 1))
				.content(json)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		
		
		
		mvc.perform(request)
						.andExpect(status().isNotFound());
					
				
	}
	
	@Test
	@DisplayName("Deve filtrar livros")
	public void findBooksTest() throws Exception{
		long id = 11;
		
		BookEntity book = BookEntity.builder()
							.id(id)
							.title(createNewBook().getTitle())
							.author(createNewBook().getAuthor())
							.isbn(createNewBook().getIsbn())
							.build();
	
		BDDMockito.given(bookService.find(Mockito.any(BookEntity.class), Mockito.any( Pageable.class )))
					.willReturn(new PageImpl<BookEntity>(Arrays.asList(book), PageRequest.of(0, 100), 1));

		String querryString = String.format("?title=%s&author=%s&page=0&size=100",book.getTitle(), book.getAuthor() );
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat(querryString)).accept(MediaType.APPLICATION_JSON);
		
		mvc
			.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("content" , hasSize(1)))
			.andExpect(jsonPath("totalElements").value(1))
			.andExpect(jsonPath("pageable.pageSize").value(100))
			.andExpect(jsonPath("pageable.pageNumber").value(0));
		
	}
	
	
	private BookDTO createNewBook() {
		return BookDTO.builder().author("Artur").title("As aventuras").isbn("001").build();
	}
}
