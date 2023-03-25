package com.gervasioartur.libraryapi.api.resource;

import com.gervasioartur.libraryapi.api.dto.LoanDTO;
import com.gervasioartur.libraryapi.model.entity.Book;
import com.gervasioartur.libraryapi.model.entity.Loan;
import com.gervasioartur.libraryapi.service.BookService;
import com.gervasioartur.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO loanDTO ){
        Book book = bookService.getBookByIsbn(loanDTO.getIsbn()).get();
        Loan loan = Loan
                    .builder()
                    .book(book)
                    .customer(loanDTO.getCustomer())
                    .loanDate(LocalDate.now())
                    .build();
        loan =  loanService.save(loan);
        return loan.getId();
    }
}
