package com.gervasioartur.libraryapi.service;

import com.gervasioartur.libraryapi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LoanService {

    Loan save(Loan loan);

    Optional<Loan> getById(long id);

    Loan update(Loan loan);

    Page<Loan> find(Loan loan, Pageable pageRequest);
}
