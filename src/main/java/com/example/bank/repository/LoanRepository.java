package com.example.bank.repository;

import com.example.bank.Enum.LoanStatus;
import com.example.bank.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanRepository extends JpaRepository<Loan , UUID> {

    @Query("select l from Loan l where l.loanStatus = :loanStatus")
    List<Loan> findAllLoanByLoanStatus(@Param("loanStatus") LoanStatus loanStatus);
}
