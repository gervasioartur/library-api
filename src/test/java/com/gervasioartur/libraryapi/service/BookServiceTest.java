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

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

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

    private Book bookFactory ( ){
        Book book = Book.builder().author("Gerry").title("gerry").isbn("123").build();
        Mockito.when(bookRepository.save(book)).thenReturn(Book.builder().id(1l).author("Gerry").title("gerry").isbn("123").build());
        return book;
    }
    @Test
    @DisplayName("Should save a book")
    public void saveBookTest() {
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Book book = this.bookFactory();
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
        Book book = this.bookFactory();
        Throwable exception = catchThrowable(() -> bookService.save(book));
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("isbn already used");

        Mockito.verify(bookRepository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Should get a book by id")
    public void getByIdTest() throws Exception {
        Long id = 1L;
        Book book = this.bookFactory();
        book.setId(id);
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        Optional<Book> foundBook = bookService.getById(id);
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());

    }
}