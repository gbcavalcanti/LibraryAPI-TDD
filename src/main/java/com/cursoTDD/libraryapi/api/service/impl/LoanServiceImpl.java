package com.cursoTDD.libraryapi.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cursoTDD.libraryapi.api.entity.LoanEntity;
import com.cursoTDD.libraryapi.api.repository.LoanRepository;
import com.cursoTDD.libraryapi.api.service.LoanService;

@Service
public class LoanServiceImpl implements LoanService {

	@Autowired
	LoanRepository loanRepository;
	
	
	@Override
	public LoanEntity save(LoanEntity loanEntity) {
		
		return loanRepository.save(loanEntity);
	}

}
