package com.example.bank.serviceImpl;

import com.example.bank.Enum.AccountStatus;
import com.example.bank.exception.ResourceNotFoundException;
import com.example.bank.model.Account;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.TransactionRepository;
import com.example.bank.service.BranchService;
import com.example.bank.service.CustomerService;
import com.example.bank.utils.AccountNumberGenerator;
import com.example.bank.utils.MaskedNumber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class is a test suite for the AccountServiceImpl class.
 * It contains tests for various methods related to account management.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private  BranchService branchService;

    @Mock
    private MaskedNumber maskedNumber;

    @Mock
    private  CustomerService customerService;


    @Mock
    private  TransactionRepository transactionRepository;


    @Mock
    private  ModelMapper modelMapper;

    @Mock
    private  AccountNumberGenerator accountNumberGenerator;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void getAccountByAccountNumber() {

        Account account = new Account();
        account.setAccountNumber("1234567890");
        account.setAccountId(UUID.randomUUID());
        account.setBalance(BigDecimal.valueOf(1000.0));
        account.setAccountStatus(AccountStatus.ACTIVE);

        // masked number mock
//        when(maskedNumber.maskNumber(account.getAccountId().toString())).thenReturn("****-****-****-1234");

//        when(accountRepository.getAccountByAccountNumber("1234567121")).thenReturn(null);

        when(accountRepository.getAccountByAccountNumber(account.getAccountNumber())).thenReturn(account);


        Account foundAccount = accountService.getAccountByAccountNumber(account.getAccountNumber());

        assertNotNull(foundAccount);

    }

    @Test
    void getAccountByAccountNumber_Not_Found() {
        Account account = new Account();
        account.setAccountNumber("1234567890");
        account.setAccountId(UUID.randomUUID());
        account.setBalance(BigDecimal.valueOf(1000.0));
        account.setAccountStatus(AccountStatus.INACTIVE);


    }

    @Test
    void createAccount() {
    }

    @Test
    void getAccountDTOById() {
    }

    @Test
    void getAccountById() {
    }

    @Test
    void closeAccount() {
    }

    @Test
    void activateAccount() {
    }

    @Test
    void deposit() {
    }

    @Test
    void withDraw() {
    }

    @Test
    void transfer() {
    }

    @Test
    void findAllCustomerAccountByCustomerId() {
    }
}