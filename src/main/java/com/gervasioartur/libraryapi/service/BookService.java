package com.gervasioartur.libraryapi.service;

import com.gervasioartur.libraryapi.model.entity.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book any);

    Optional<Book> getById(Long id);

    void delete(Book book);

    Book update(Book book);
}
