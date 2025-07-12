package com.example.bank.repository;

import com.example.bank.Enum.AccountStatus;
import com.example.bank.Enum.AccountType;
import com.example.bank.Enum.IdentityProofType;
import com.example.bank.model.Account;
import com.example.bank.model.Address;
import com.example.bank.model.Branch;
import com.example.bank.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void findAllCustomerAccountByCustomerId() {

        Address address = new Address();
        Customer customer = new Customer();
        address.setCustomer(customer);

        customerRepository.save(customer);


        Account account = new Account();
        account.setCustomer(customer);
        accountRepository.save(account);

        Account account1 = new Account();
        account1.setCustomer(customer);
        accountRepository.save(account1);

       List<Account> accounts =  accountRepository.FindAllCustomerAccountByCustomerId(customer.getCustomerId());

       assertFalse(accounts.isEmpty(),"Size should not be zero");

       assertEquals(2, accounts.size(), "Size should be 2");

       assertNotEquals(AccountType.CURRENT, accounts.get(1).getAccountType(), "Account type should be CURRENT");

    }

    @Test
    void getAccountByAccountNumber() {


        Account account = new Account();
        accountRepository.save(account);
        String accountNumber = account.getAccountNumber();

        assertNull(accountNumber, "Account number should be null for unsaved account");
        Account account1 = new Account();
        String accountNumber1 = "1234567890";
        account1.setAccountNumber(accountNumber1);
        accountRepository.save(account1);

        Account foundAccount = accountRepository.getAccountByAccountNumber(accountNumber1);
        assertNotNull(foundAccount, "Found account should not be null");
        assertEquals(accountNumber1, foundAccount.getAccountNumber(), "Account number should match");
        assertEquals(account1.getAccountId(), foundAccount.getAccountId(), "Account ID should match");
    }

    @Test
    void existsByAccountNumber() {
        Account account = new Account();
        account.setAccountNumber("1234567890");
        accountRepository.save(account);

        Account account1 = new Account();
        account1.setAccountNumber("0987654321");
        accountRepository.save(account1);

        Account account2 = new Account();
        account2.setAccountNumber("1234562320");
        accountRepository.save(account2);

        boolean exists = accountRepository.existsByAccountNumber("9933000033");
        assertFalse(exists, "Account number should not exist in the repository");

        exists = accountRepository.existsByAccountNumber("1234567890");
        assertTrue(exists, "Account number should exist in the repository");

        String accountNumber = account.getAccountNumber();

    }
}