package com.gervasioartur.libraryapi.model.repository;

import com.gervasioartur.libraryapi.model.entity.Book;
import com.gervasioartur.libraryapi.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static com.gervasioartur.libraryapi.model.repository.BookRepositoryTest.bookFactory;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    private LoanRepository loanRepository;

    @Test
    @DisplayName("Should verify if exists a loan that was not returned for a book")
    public void existsByBookAndNotReturnedTest() throws Exception {

        Book book = bookFactory();
        entityManager.persist(book);

        Loan loan = Loan
                .builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .returned(false)
                .build();

        entityManager.persist(loan);
        boolean exists = loanRepository.existsByBookAndNotReturned(book);
        assertThat(exists).isTrue();
    }
}
