package com.cursoTDD.libraryapi.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cursoTDD.libraryapi.api.entity.LoanEntity;

public interface LoanRepository extends JpaRepository<LoanEntity, Long>{
	
	

}
