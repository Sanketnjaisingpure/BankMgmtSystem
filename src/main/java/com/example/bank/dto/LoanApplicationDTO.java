package com.example.bank.dto;

import com.example.bank.Enum.LoanStatus;
import com.example.bank.Enum.LoanType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanApplicationDTO {

    @NotNull
    private UUID customerId;

    @NotNull
    @Size(message = "size should be up to 20 " , min = 2 , max = 20)
    private String accountNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private LoanType loanType;

    @NotNull
    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0.01")
    private BigDecimal totalAmount;


    @NotNull
    private long employeeId;
}
