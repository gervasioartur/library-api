package com.gervasioartur.libraryapi.service;

import com.gervasioartur.libraryapi.api.dto.LoanFilterDTO;
import com.gervasioartur.libraryapi.exception.BusinessException;
import com.gervasioartur.libraryapi.model.entity.Book;
import com.gervasioartur.libraryapi.model.entity.Loan;
import com.gervasioartur.libraryapi.model.repository.LoanRepository;
import com.gervasioartur.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    private LoanService loanService;

    @MockBean
    private LoanRepository loanRepository;

    public static Loan createLoan() {
        Book book = Book.builder().id(1l).build();
        String customer = "Fulano";

        return Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();
    }

    @BeforeEach
    public void setUp() throws Exception {
        this.loanService = new LoanServiceImpl(this.loanRepository);
    }

    @Test
    @DisplayName("Should save loan")
    public void saveLoanTest() throws Exception {
        Book book = Book.builder().id(1l).build();
        String customer = "Fulano";

        Loan savingLoan = Loan
                .builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan
                .builder()
                .id(1l)
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();
        when(loanRepository.existsByBookAndNotReturned(book)).thenReturn(false);
        when(loanRepository.save(savingLoan)).thenReturn(savedLoan);
        Loan loan = loanService.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }

    @Test
    @DisplayName("Should throw an business exception when trying to loan an already loaned book")
    public void loanedBookSaveTest() throws Exception {
        Book book = Book.builder().id(1l).build();
        String customer = "Fulano";

        Loan savingLoan = Loan
                .builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();
        when(loanRepository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable exception = catchThrowable(() -> loanService.save(savingLoan));
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned!");

        verify(loanRepository, never()).save(savingLoan);
    }

    @Test
    @DisplayName("Should get the loan information")
    public void getLoanInfoTest() throws Exception {
        Book book = Book.builder().id(1l).build();
        String customer = "Fulano";
        Long id = 1l;

        Loan loan = Loan
                .builder()
                .id(id)
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();
        Mockito.when(loanRepository.findById(id)).thenReturn(Optional.of(loan));
        Optional<Loan> result = loanService.getById(id);
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify(loanRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should update a loan")
    public void updateLoanTest() {
        Book book = Book.builder().id(1l).build();
        String customer = "Fulano";
        Long id = 1l;

        Loan loan = Loan
                .builder()
                .id(id)
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .returned(true)
                .build();
        when(loanRepository.save(loan)).thenReturn(loan);

        Loan updatedLoan = loanService.update(loan);

        assertThat(updatedLoan.getReturned()).isTrue();
        verify(loanRepository).save(loan);
    }

    @Test
    @DisplayName("Deve filtrar empréstimos pelas propriedades")
    public void findLoanTest() {
        //cenario
        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().customer("Fulano").isbn("321").build();

        Loan loan = createLoan();
        loan.setId(1l);
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> lista = Arrays.asList(loan);

        Page<Loan> page = new PageImpl<>(lista, pageRequest, lista.size());
        when(loanRepository.findByBookIsbnOrCustomer(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(PageRequest.class))
        )
                .thenReturn(page);

        //execucao
        Page<Loan> result = loanService.find(loanFilterDTO, pageRequest);

        //verificacoes
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }
}
