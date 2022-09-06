package com.cursoTDD.libraryapi.api.dto;


import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class LoanDTO {

	private String isbn;
	private String costumer;
	
}
