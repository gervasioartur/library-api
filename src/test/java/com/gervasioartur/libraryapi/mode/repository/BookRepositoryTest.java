package com.gervasioartur.libraryapi.mode.repository;

import com.gervasioartur.libraryapi.model.entity.Book;
import com.gervasioartur.libraryapi.model.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository bookRepository;

    private Book bookFactory ( ){
        Book book = Book.builder().author("Gerry").title("gerry").isbn("123").build();
        return book;
    }

    @Test
    @DisplayName("Should return true on save success")
    public void returnTrueIfIsbnExistTest() {
        String isbn = "123";
        entityManager.persist(this.bookFactory());
        boolean exists = bookRepository.existsByIsbn(isbn);
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false on save failure")
    public void returnFalseIfIsbnExistTest() {
        String isbn = "123";
        boolean exists = bookRepository.existsByIsbn(isbn);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should return true on getById success")
    public  void getByIdTest() {
        Book book = this.bookFactory();
        entityManager.persist(book);
        Optional<Book> foundBook = bookRepository.findById(book.getId());
        assertThat(foundBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should return false on getById failure")
    public  void NotFoundBookGetByIdTest() {
        Book book = this.bookFactory();
        book.setId(1l);
        Optional<Book> foundBook = bookRepository.findById(book.getId());
        assertThat(foundBook.isPresent()).isFalse();
    }
}
