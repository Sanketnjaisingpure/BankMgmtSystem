package com.example.bank.model;

import com.example.bank.Enum.LoanType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LoanTest {

    @Test
    void testLoanCreation() {
        Loan loan = new Loan();
        loan.setLoanId(UUID.randomUUID());
        loan.setLoanType(LoanType.PERSONAL);
        loan.setTotalAmount(new BigDecimal("10000.00"));
        loan.setAmountPaid(new BigDecimal("5000.00"));
        loan.setInterestRate(5.0);
        loan.setTermInMonth(12);
        loan.setLoanStatus(com.example.bank.Enum.LoanStatus.APPROVED);
        loan.setStartDate(java.time.LocalDate.now());
        loan.setCustomer(new Customer());
        loan.setAccount(new Account());
        loan.setEmployee(new Employee());
        assertNotNull(loan.getLoanId(), "Loan ID should not be null");
        assertEquals(LoanType.PERSONAL, loan.getLoanType(), "Loan type should match");
        assertEquals(new BigDecimal("10000.00"), loan.getTotalAmount(), "Total amount should match");
        assertEquals(new BigDecimal("5000.00"), loan.getAmountPaid(), "Amount paid should match");
        assertEquals(5.0, loan.getInterestRate(), "Interest rate should match");
        assertEquals(12, loan.getTermInMonth(), "Term in months should match");
        assertEquals(com.example.bank.Enum.LoanStatus.APPROVED, loan.getLoanStatus(), "Loan status should match");
        assertNotNull(loan.getStartDate(), "Start date should not be null");
        assertNotNull(loan.getCustomer(), "Customer should not be null");
        assertNotNull(loan.getAccount(), "Account should not be null");
        assertNotNull(loan.getEmployee(), "Employee should not be null");




    }

}