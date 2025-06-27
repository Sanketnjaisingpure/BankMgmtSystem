package com.example.bank.dto;

import com.example.bank.Enum.AccountStatus;
import com.example.bank.Enum.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountDTO {

    private String firstName;

    private String middleName;

    private String lastName;

    private String accountNumber;

    private AccountType accountType;

    private BigDecimal balance;

    private AccountStatus accountStatus;

    private LocalDate openDate;

    private String branchName;

    private String bankName;
}
