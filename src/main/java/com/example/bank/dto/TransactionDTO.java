package com.example.bank.dto;

import com.example.bank.Enum.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionDTO {

    private String accountNumber;

    private BigDecimal amount;

    private TransactionType transactionType;

    private Timestamp timestamp;

    private String description;

    private String destinationAccountNumber;

}
