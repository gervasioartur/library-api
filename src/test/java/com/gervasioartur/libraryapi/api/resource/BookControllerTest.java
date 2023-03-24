package com.gervasioartur.libraryapi.api.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gervasioartur.libraryapi.api.dto.BookDTO;
import com.gervasioartur.libraryapi.exception.BusinessException;
import com.gervasioartur.libraryapi.model.entity.Book;
import com.gervasioartur.libraryapi.service.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {
    static String BOOK_API = "/api/books";
    @Autowired
    MockMvc mvc;
    @MockBean
    BookService bookService;

    private BookDTO createNewBook() {
        return BookDTO.builder().author("Artur").title("As aventuras").isbn("001").build();
    }

    private MockHttpServletRequestBuilder createRequestBuilder(BookDTO bookDTO) throws JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString(bookDTO);
        return MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
    }

    @Test
    @DisplayName("Should create a book")
    public void createBookTest() throws Exception {
        BookDTO bookDTO = this.createNewBook();
        Book saveBook = Book.builder().id(10l).author("Artur").title("As aventuras").isbn("001").build();
        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(saveBook);
        MockHttpServletRequestBuilder request = this.createRequestBuilder(bookDTO);
        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(bookDTO.getTitle()))
                .andExpect(jsonPath("author").value(bookDTO.getAuthor()))
                .andExpect(jsonPath("isbn").value(bookDTO.getIsbn()));

    }

    @Test
    @DisplayName("Should throw validation error if any field is empty")
    public void createIvalidBookTest() throws Exception {
        MockHttpServletRequestBuilder request = this.createRequestBuilder(new BookDTO());
        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));

    }

    @Test
    @DisplayName("Should throw an error if the book isbn is already used")
    public void creatBookWithDuplicatedIsbnTest() throws Exception {
        String errorMessage = "isbn already used";
        BookDTO bookDTO = this.createNewBook();
        BDDMockito.given(bookService.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(errorMessage));
        MockHttpServletRequestBuilder request = this.createRequestBuilder(bookDTO);
        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(errorMessage));
    }

    @Test
    @DisplayName("Should return book details if the book exists")
    public void getBookDetailsTest() throws Exception {
        Long id = 1l;
        Book book = Book.builder().id(id).author("Artur").title("As aventuras").isbn("001").build();
        BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(book));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API + "/" + id)
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(this.createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(this.createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(this.createNewBook().getIsbn()));

    }

    @Test
    @DisplayName("Should return resource not found if the book does not exists")
    public void bookNotFoundTest() throws Exception {
        Long id = 2l;
        Book book = Book.builder().id(id).author("Artur").title("As aventuras").isbn("001").build();
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API + "/" + id)
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should remove a book")
    public void removeBookTest() throws Exception {
        Long id = 1l;
        Book book = Book.builder().id(id).build();
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.of(book));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API + "/" + id)
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return resource not found if the book does not exist")
    public void removeInsistentBookTest() throws Exception {
        Long id = 2l;
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API + "/" + id)
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update book if book exists")
    public void updateBookTest() throws Exception {
        Long id = 1l;
        String json = new ObjectMapper().writeValueAsString(createNewBook());

        Book updatingBook = Book.builder().id(1l).title("some title").author("some author").isbn("321").build();
        BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(updatingBook));
        Book updatedBook = Book.builder().id(id).author("Artur").title("As aventuras").isbn("321").build();
        BDDMockito.given(bookService.update(updatingBook)).willReturn(updatedBook);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value("321"));
    }

    @Test
    @DisplayName("Should filter books")
    public void findBookTest() throws Exception {
        Long id = 1l;
        Book book = Book.builder().id(id).author("Artur").title("As aventuras").isbn("001").build();
        BDDMockito.given(bookService.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));

        String queryString = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }
}
