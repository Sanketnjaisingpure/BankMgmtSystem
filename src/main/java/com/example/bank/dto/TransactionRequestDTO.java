package com.example.bank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class TransactionRequestDTO {

        @NotNull
        private String toAccountNumber;

        @NotNull
        private String fromAccountNumber;

        @NotNull
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        private BigDecimal amount;
}
