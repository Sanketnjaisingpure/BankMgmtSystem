package com.example.bank.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void testTransactionCreation() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setTransactionType(com.example.bank.Enum.TransactionType.DEPOSIT);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setTimestamp(new Timestamp(System.currentTimeMillis()));
        transaction.setDescription("Test deposit");
        transaction.setDestinationAccountNumber("1234567890");
        transaction.setAccount(new Account());

        assertNotNull(transaction.getTransactionId(), "Transaction ID should not be null");
        assertEquals(com.example.bank.Enum.TransactionType.DEPOSIT, transaction.getTransactionType(), "Transaction type should match");
        assertEquals(new BigDecimal("100.00"), transaction.getAmount(), "Amount should match");
        assertNotNull(transaction.getTimestamp(), "Timestamp should not be null");
        assertEquals("Test deposit", transaction.getDescription(), "Description should match");
        assertEquals("1234567890", transaction.getDestinationAccountNumber(), "Destination account number should match");
        assertNotNull(transaction.getAccount(), "Account should not be null");
    }

}