package com.example.bank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanApplyDTO {

    @NotNull
    private UUID loanId;

    @NotNull
    @DecimalMin(value = "500.00", message = "Loan amount must be at least 500.00")
    private BigDecimal totalAmount;

}
