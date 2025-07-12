package com.example.bank.repository;

import com.example.bank.Enum.LoanStatus;
import com.example.bank.model.Loan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LoanRepositoryTest {


    private final LoanRepository loanRepository;

    @Autowired
    public LoanRepositoryTest(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Test
    void findAllLoanByLoanStatus() {

        Loan loan = new Loan();
        loan.setLoanStatus(LoanStatus.ACTIVE);
        loanRepository.save(loan);

        Loan loan3 = new Loan();
        loan3.setLoanStatus(LoanStatus.ACTIVE);
        loanRepository.save(loan3);


        List<Loan> activeLoans = loanRepository.findAllLoanByLoanStatus(LoanStatus.ACTIVE);

        assertNotNull(activeLoans, "Active loans should not be null");
        assertFalse(activeLoans.isEmpty(), "Active loans list should not be empty");
        assertEquals(2, activeLoans.size(), "There should be 2 active loans");

    }
}