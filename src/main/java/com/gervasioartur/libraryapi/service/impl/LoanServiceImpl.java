package com.gervasioartur.libraryapi.service.impl;

import com.gervasioartur.libraryapi.model.entity.Loan;
import com.gervasioartur.libraryapi.model.repository.LoanRepository;
import com.gervasioartur.libraryapi.service.LoanService;

public class LoanServiceImpl implements LoanService {
    private LoanRepository loanRepository;
    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Loan save(Loan loan) {
        return this.loanRepository.save(loan);
    }
}
