package com.example.bank.repository;

import com.example.bank.model.Account;
import com.example.bank.model.Address;
import com.example.bank.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryTest {


    @Autowired
    private AccountRepository accountRepository;

    @Test
    void findAllCustomerAccountByCustomerId() {

       /* List<Account> customerAccounts = new ArrayList<>();
        Address address = new Address(1L, "123 Main St", "Springfield", "IL", "627011", "USA");
        Customer customer1 = new Customer("c388cd37-f795-4a04-8fb3-fe5d156873ba", "John", "Doe", "law", "john@gmail.com","john@1234", "1234567890", address,
                LocalDate.parse("2000-06-24"), "Passport", "A123456789", LocalDateTime.now(), null);
*/
    }

    @Test
    void getAccountByAccountNumber() {
    }

    @Test
    void existsByAccountNumber() {
    }
}