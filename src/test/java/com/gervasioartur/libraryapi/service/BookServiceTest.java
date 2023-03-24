package com.gervasioartur.libraryapi.service;


import com.gervasioartur.libraryapi.exception.BusinessException;
import com.gervasioartur.libraryapi.model.entity.Book;
import com.gervasioartur.libraryapi.model.repository.BookRepository;
import com.gervasioartur.libraryapi.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Assertions;
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

    private Book bookFactory() {
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
    @DisplayName("Should remove a Book if exists")
    public void deleteTest() {
        Book book = this.bookFactory();
        book.setId(1l);
        this.bookService.delete(book);
        Mockito.verify(bookRepository, Mockito.atLeastOnce()).delete(book);
    }

    @Test
    @DisplayName("Should update a book")
    public void updateBookTest() {
        long id = 1l;
        Book updatingBook = Book.builder().id(id).build();

        Book updatedBook = this.bookFactory();
        updatedBook.setId(id);
        Mockito.when(bookRepository.save(updatingBook)).thenReturn(updatedBook);

        Book book = bookService.update(updatingBook);

        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
    }

    @Test
    @DisplayName("Should return an error if when trying to update invalid book")
    public void updateInvalidBookTest() {
        Book book = new Book();
        Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.update(book));
        Mockito.verify(bookRepository, Mockito.never()).save(book);
    }
}
