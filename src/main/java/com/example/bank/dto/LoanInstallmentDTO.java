package com.example.bank.dto;

import com.example.bank.Enum.LoanStatus;
import com.example.bank.Enum.LoanType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanInstallmentDTO {
    private UUID loanId;

    private LoanType loanType;

    private BigDecimal totalAmount;

    private BigDecimal amountPaid;

    private double interestRate;

    private int termInMonth;

    private LoanStatus loanStatus;

}
