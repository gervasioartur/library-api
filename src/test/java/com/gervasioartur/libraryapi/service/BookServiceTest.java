package com.gervasioartur.libraryapi.service;


import com.gervasioartur.libraryapi.exception.BusinessException;
import com.gervasioartur.libraryapi.model.entity.Book;
import com.gervasioartur.libraryapi.model.repository.BookRepository;
import com.gervasioartur.libraryapi.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {
    BookService bookService;
    @MockBean
    BookRepository bookRepository;

    @BeforeEach
    public void setup() {
        this.bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    @DisplayName("Should save a book")
    public void saveBookTest() {
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Book book = Book.builder().author("Gerry").title("gerry").isbn("123").build();
        Mockito.when(bookRepository.save(book)).thenReturn(Book.builder().id(1l).author("Gerry").title("gerry").isbn("123").build());
        Book savedBook = bookService.save(book);
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getAuthor()).isEqualTo("Gerry");
        assertThat(savedBook.getTitle()).isEqualTo("gerry");
        assertThat(savedBook.getIsbn()).isEqualTo("123");
    }

    @Test
    @DisplayName("Should throw a BusinessException if the book isbn is already used")
    public void shouldNotSaveBookWithDuplicateIsbnTest() {
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);
        Book book = Book.builder().author("Gerry").title("gerry").isbn("123").build();
        Mockito.when(bookRepository.save(book)).thenReturn(Book.builder().id(1l).author("Gerry").title("gerry").isbn("123").build());
        Throwable exception = catchThrowable(() -> bookService.save(book));
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("isbn already used");

        Mockito.verify(bookRepository, Mockito.never()).save(book);
    }
}
