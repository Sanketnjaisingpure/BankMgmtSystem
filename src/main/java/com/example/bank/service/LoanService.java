package com.example.bank.service;

import com.example.bank.dto.LoanApplicationDTO;
import com.example.bank.dto.LoanApplyDTO;
import com.example.bank.dto.LoanDTO;
import com.example.bank.dto.LoanInstallmentDTO;
import com.example.bank.model.Loan;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface LoanService {

    LoanDTO applyForLoan(LoanApplicationDTO loanApplicationDTO);

    LoanDTO getLoanById(UUID loanId);

    List<LoanDTO> getLoanCustomerById(UUID customerId);

    LoanInstallmentDTO makeLoanPayment(LoanApplyDTO loanApplyDTO);

    Loan findLoanByLoanId(UUID loanId);

}
