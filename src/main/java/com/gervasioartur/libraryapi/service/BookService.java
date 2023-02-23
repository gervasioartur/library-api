package com.gervasioartur.libraryapi.service;

import com.gervasioartur.libraryapi.model.entity.Book;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface BookService {
    Book save(Book any);

    Optional<Book> getById(Long id);

    void delete(Book book);
}
