package com.cursoTDD.libraryapi.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cursoTDD.libraryapi.api.entity.BookEntity;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

	boolean existsByIsbn(String isbn);

}
