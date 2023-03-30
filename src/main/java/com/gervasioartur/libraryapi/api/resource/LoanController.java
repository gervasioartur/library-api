package com.gervasioartur.libraryapi.api.resource;

import com.gervasioartur.libraryapi.api.dto.LoanDTO;
import com.gervasioartur.libraryapi.api.dto.ReturnedLoanDTO;
import com.gervasioartur.libraryapi.model.entity.Book;
import com.gervasioartur.libraryapi.model.entity.Loan;
import com.gervasioartur.libraryapi.service.BookService;
import com.gervasioartur.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        Book book = bookService
                .getBookByIsbn(loanDTO.getIsbn())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn!"));

        Loan loan = Loan
                    .builder()
                    .book(book)
                    .customer(loanDTO.getCustomer())
                    .loanDate(LocalDate.now())
                    .build();
        loan =  loanService.save(loan);
        return loan.getId();
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void returnedBook (@PathVariable long id, @RequestBody ReturnedLoanDTO returnedLoanDTO){
        Loan loan = this.loanService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(returnedLoanDTO.getReturned());
        this.loanService.update(loan);
    }

}
