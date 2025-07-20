package com.example.bank.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {

    @Test
    void testBankCreation() {
        Bank bank = new Bank();
        bank.setBankId(UUID.randomUUID());
        bank.setBankName("Test Bank");
        bank.setHeadOfficeAddress("123 Test St, Test City, TC 12345");


        assertEquals("Test Bank", bank.getBankName());
        assertEquals("123 Test St, Test City, TC 12345", bank.getHeadOfficeAddress());

    }

    @Test
    void testDefaultListInitialization() {
        Branch branch = new Branch();
        branch.setBranchId(1L);
        branch.setBranchName("Test Branch");
        Bank bank = new Bank();

        branch.setBank(bank);
        branch.setAddress(new Address());

        assertNotNull(bank.getBranch(), "Branch list should be initialized");
        assertTrue(bank.getBranch().isEmpty(), "Branch list should be empty initially");
    }

}