package com.cursoTDD.libraryapi.api.Controllers;



import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDate;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.cursoTDD.libraryapi.api.dto.LoanDTO;
import com.cursoTDD.libraryapi.api.entity.BookEntity;
import com.cursoTDD.libraryapi.api.entity.LoanEntity;
import com.cursoTDD.libraryapi.api.resource.LoanController;
import com.cursoTDD.libraryapi.api.service.BookService;
import com.cursoTDD.libraryapi.api.service.LoanService;
import com.cursoTDD.libraryapi.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = {LoanController.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoanControllerTest {

	
	public static final String LOAN_API = "/api/loans";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	BookService bookService;

	@MockBean
	private LoanService loanService;

	@Test
	@DisplayName("deve realizar um emprestimo")
	public void createLoanTest() throws Exception {
		
		LoanDTO loanDTO = LoanDTO.builder().isbn("123").costumer("fulano").build();
		String json = new ObjectMapper().writeValueAsString(loanDTO);
		
		
		BookEntity book = BookEntity.builder().id((long) 11).isbn("123").build();
		
		BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));
	
		LoanEntity loan = LoanEntity.builder().id((long) 1).costumer("fulano").book(book).loanDate(LocalDate.now()).build();
		
		
		BDDMockito.given(loanService.save(Mockito.any(LoanEntity.class))).willReturn(loan);
	
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
																	  .accept(MediaType.APPLICATION_JSON)
																	  .contentType(MediaType.APPLICATION_JSON)
																	  .content(json);
		
		mvc.perform(request)
			.andExpect(status().isCreated())
			.andExpect(content().string("1"));
		
	}
	
	
	@Test
	@DisplayName("deve retornar erro ao tentar fazer emprestimo de um livro inexistente")
	public void invalidIsbnCreateLoanTest() throws Exception {
		LoanDTO loanDTO = LoanDTO.builder().isbn("123").costumer("fulano").build();
		String json = new ObjectMapper().writeValueAsString(loanDTO);
		
		BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.empty());
		

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
																	  .accept(MediaType.APPLICATION_JSON)
																	  .contentType(MediaType.APPLICATION_JSON)
																	  .content(json);
		
		mvc.perform(request)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("errors", Matchers.hasSize(1)))
			.andExpect(jsonPath("errors[0]").value("Book not found for passed isbn"));
		
		
		
		
	}
	
	@Test
	@DisplayName("deve retornar erro ao tentar fazer emprestimo de um livro emprestado")
	public void loanedBookErrorOnCreateLoanTest() throws Exception {

		LoanDTO loanDTO = LoanDTO.builder().isbn("123").costumer("fulano").build();
		String json = new ObjectMapper().writeValueAsString(loanDTO);
		
		
		BookEntity book = BookEntity.builder().id((long) 11).isbn("123").build();
		
		BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));
	
		BDDMockito.given(loanService.save(Mockito.any(LoanEntity.class))).willThrow(new BusinessException("Book already loaned"));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
																	  .accept(MediaType.APPLICATION_JSON)
																	  .contentType(MediaType.APPLICATION_JSON)
																	  .content(json);
		mvc.perform(request)
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("errors", Matchers.hasSize(1)))
		.andExpect(jsonPath("errors[0]").value("Book already loaned"));
	
	}
	
	
}
