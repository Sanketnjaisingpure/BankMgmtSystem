package com.example.bank.dto;

import com.example.bank.Enum.LoanStatus;
import com.example.bank.Enum.LoanType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LoanDTO {
    private UUID customerId;

    private String accountNumber;

    private LoanType loanType;


    private BigDecimal totalAmount;

    private BigDecimal amountPaid;

    private double interestRate;

    private int termInMonth;

    private LoanStatus loanStatus;

    private LocalDate startDate;
}
