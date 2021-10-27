package com.rcdomingos.bookservice.repository;

import com.rcdomingos.bookservice.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
