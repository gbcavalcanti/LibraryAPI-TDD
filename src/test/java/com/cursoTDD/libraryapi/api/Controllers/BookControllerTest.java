package com.cursoTDD.libraryapi.api.Controllers;


import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.cursoTDD.libraryapi.api.dto.BookDTO;
import com.cursoTDD.libraryapi.api.entity.BookEntity;
import com.cursoTDD.libraryapi.api.service.BookService;
import com.cursoTDD.libraryapi.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;




@ActiveProfiles("test")
@WebMvcTest
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
	
	
	
	
	
	private BookDTO createNewBook() {
		return BookDTO.builder().author("Artur").title("As aventuras").isbn("001").build();
	}
}
