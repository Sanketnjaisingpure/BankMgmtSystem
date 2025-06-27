package com.example.bank.service;

import com.example.bank.dto.AccountDTO;
import com.example.bank.dto.CreateAccountDTO;
import com.example.bank.dto.TransactionDTO;
import com.example.bank.dto.TransactionRequestDTO;
import com.example.bank.model.Account;
import java.util.List;
import java.util.UUID;

public interface AccountService {

    AccountDTO createAccount(CreateAccountDTO createAccountDTO );

    AccountDTO getAccountDTOById(UUID accountId);

    void closeAccount(String accountNumber);

    void activateAccount(String accountNumber);

    Account getAccountById(UUID accountId);

    Account getAccountByAccountNumber(String accountNumber);

    List<Account> FindAllCustomerAccountByCustomerId( UUID customerId);


    // -- Core Banking Operations --

    TransactionDTO deposit(TransactionRequestDTO transactionRequestDTO);

    TransactionDTO withDraw(TransactionRequestDTO transactionRequestDTO);

    TransactionDTO transfer(TransactionRequestDTO transactionRequestDTO);




}
