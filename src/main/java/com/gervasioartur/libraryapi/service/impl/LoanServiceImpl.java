package com.gervasioartur.libraryapi.service.impl;

import com.gervasioartur.libraryapi.api.dto.LoanFilterDTO;
import com.gervasioartur.libraryapi.exception.BusinessException;
import com.gervasioartur.libraryapi.model.entity.Book;
import com.gervasioartur.libraryapi.model.entity.Loan;
import com.gervasioartur.libraryapi.model.repository.LoanRepository;
import com.gervasioartur.libraryapi.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class LoanServiceImpl implements LoanService {
    private final LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Loan save(Loan loan) {
        if (loanRepository.existsByBookAndNotReturned(loan.getBook()))
            throw new BusinessException("Book already loaned!");

        return this.loanRepository.save(loan);
    }

    @Override
    public Optional<Loan> getById(long id) {
        return loanRepository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return loanRepository.save(loan);
    }

    @Override
    public Page<Loan> find(LoanFilterDTO filterDTO, Pageable pageable) {
        return loanRepository.findByBookIsbnOrCustomer(filterDTO.getIsbn(), filterDTO.getCustomer(), pageable);
    }

    @Override
    public Page<Loan> getLoanByBook(Book book, Pageable pageable) {
        return loanRepository.findByBook(book, pageable);
    }
}
