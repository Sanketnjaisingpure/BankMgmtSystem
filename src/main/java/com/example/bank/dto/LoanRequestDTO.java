package com.example.bank.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class LoanRequestDTO {

    @NotNull
    private UUID loanId;

    @NotNull
    private Long employeeId;
}
