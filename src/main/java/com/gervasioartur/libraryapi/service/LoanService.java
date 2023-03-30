package com.gervasioartur.libraryapi.service;

import com.gervasioartur.libraryapi.model.entity.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanService {

    Loan save(Loan loan);

    Optional<Loan> getById(long id);

    Loan update(Loan loan);
}
