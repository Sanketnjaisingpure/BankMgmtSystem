package com.example.bank.dto;

import com.example.bank.Enum.AccountStatus;
import com.example.bank.Enum.AccountType;
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
public class CreateAccountDTO {

    @NotNull
    private AccountType accountType;


    @NotNull
    @DecimalMin(value = "500", message = "Amount must be greater than 500")
    private BigDecimal balance;

    @NotNull
    private UUID customerId;

    @NotNull
    private Long branchId;

}
