package com.gervasioartur.libraryapi.model.repository;

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

    public static Book bookFactory ( ){
        Book book = Book.builder().author("Gerry").title("gerry").isbn("123").build();
        return book;
    }

    @Test
    @DisplayName("Should return true on save success")
    public void returnTrueIfIsbnExistTest() {
        String isbn = "123";
        entityManager.persist(Book.builder().author("Gerry").title("gerry").isbn(isbn).build());
        boolean exists = bookRepository.existsByIsbn(isbn);
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return true on save failure")
    public void returnFalseIfIsbnExistTest() {
        String isbn = "123";
        boolean exists = bookRepository.existsByIsbn(isbn);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should remove a Book if exists")
    public void removeBookTest() {
        Book book = this.bookFactory();
        entityManager.persist(book);
        bookRepository.delete(book);
        Optional<Book> result = bookRepository.findById(book.getId());
        assertThat(result.isPresent()).isNotNull();
    }

    @Test
    @DisplayName("Should get a book by id")
    public void findByIdTest(){
        Book book = this.bookFactory();
        entityManager.persist(book);
        Optional<Book> foundBook = bookRepository.findById(book.getId());
        assertThat(foundBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should save a book")
    public void saveBookTest(){
        Book book = this.bookFactory();
        Book savedBook = bookRepository.save(book);
        assertThat(savedBook.getId()).isNotNull();

    }
}
