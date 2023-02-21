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

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository bookRepository;

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
}
