package com.gervasioartur.libraryapi.service;


import com.gervasioartur.libraryapi.model.entity.Book;
import com.gervasioartur.libraryapi.model.repository.BookRepository;
import com.gervasioartur.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {
    BookService bookService;
    @MockBean
    BookRepository bookRepository;
    @BeforeEach
    public void setup(){
        this.bookService = new BookServiceImpl(bookRepository);
    }
    @Test
    @DisplayName("Should save a book")
    public void saveBookTest() {
        Book book = Book.builder().author("Gerry").title("gerry").isbn("123").build();
        Mockito.when(bookRepository.save(book)).thenReturn(Book.builder().id(1l).author("Gerry").title("gerry").isbn("123").build());
        Book savedBook = bookService.save(book);
        Assertions.assertThat(savedBook.getId()).isNotNull();
        Assertions.assertThat(savedBook.getAuthor()).isEqualTo("Gerry");
        Assertions.assertThat(savedBook.getTitle()).isEqualTo("gerry");
        Assertions.assertThat(savedBook.getIsbn()).isEqualTo("123");
    }
}
