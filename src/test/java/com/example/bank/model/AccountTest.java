package com.example.bank.model;

import com.example.bank.Enum.AccountStatus;
import com.example.bank.Enum.AccountType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void testAccountCreation() {
        Account account = new Account();
        account.setAccountId(UUID.randomUUID());
        account.setAccountNumber("123456789");
        account.setAccountType(AccountType.SAVING);
        account.setBalance(BigDecimal.valueOf(1000.00));
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setOpenDate(LocalDate.now());


        assertNotNull(account.getAccountId());
        assertEquals("123456789", account.getAccountNumber());
        assertEquals(AccountType.SAVING, account.getAccountType());
        assertEquals(BigDecimal.valueOf(1000.00), account.getBalance());
        assertEquals(AccountStatus.ACTIVE, account.getAccountStatus());
        assertNotNull(account.getOpenDate());
    }

    @Test
    void testDefaultListInitialization(){

        Account account = new Account();

        assertNotNull(account.getTransactionList(), "Transaction list should be initialized");
        assertNotNull(account.getCardList(), "Card list should be initialized");
        assertNotNull(account.getLoanList(), "Loan list should be initialized");
    }


}