package com.example.bank.serviceImpl;

import com.example.bank.Enum.AccountStatus;
import com.example.bank.Enum.AccountType;
import com.example.bank.Enum.TransactionType;
import com.example.bank.dto.AccountDTO;
import com.example.bank.dto.CreateAccountDTO;
import com.example.bank.dto.TransactionDTO;
import com.example.bank.dto.TransactionRequestDTO;
import com.example.bank.exception.ResourceNotFoundException;
import com.example.bank.model.Account;
import com.example.bank.model.Bank;
import com.example.bank.model.Branch;
import com.example.bank.model.Customer;
import com.example.bank.model.Transaction;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.TransactionRepository;
import com.example.bank.service.BranchService;
import com.example.bank.service.CustomerService;
import com.example.bank.utils.AccountNumberGenerator;
import com.example.bank.utils.MaskedNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    private BranchService branchService;

    @Mock
    private MaskedNumber maskedNumber;

    @Mock
    private CustomerService customerService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account testAccount;
    private Customer testCustomer;
    private Branch testBranch;
    private CreateAccountDTO createAccountDTO;
    private TransactionRequestDTO transactionRequestDTO;
    private Transaction testTransaction;
    private AccountDTO testAccountDTO;
    private TransactionDTO testTransactionDTO;

    @BeforeEach
    void setUp() {
        // Setup test data


        testCustomer = new Customer();
        testCustomer.setCustomerId(UUID.randomUUID());
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setMiddleName("M");


        Bank testBank = new Bank();
        testBank.setBankId(UUID.randomUUID());
        testBank.setBankName("Test Bank");

        testBranch = new Branch();
        testBranch.setBranchId(1L);
        testBranch.setBranchName("Test Branch");
        testBranch.setIfscCode("TEST1234567");
        testBranch.setBank(testBank);

        testAccount = new Account();
        testAccount.setAccountId(UUID.randomUUID());
        testAccount.setAccountNumber("1234567890");
        testAccount.setBalance(BigDecimal.valueOf(1000.0));
        testAccount.setAccountStatus(AccountStatus.ACTIVE);
        testAccount.setAccountType(AccountType.SAVING);



        testAccount.setCustomer(testCustomer);
        testAccount.setBranch(testBranch);


        createAccountDTO = new CreateAccountDTO();
        createAccountDTO.setAccountType(AccountType.SAVING);
        createAccountDTO.setBalance(BigDecimal.valueOf(1000.0));
        createAccountDTO.setCustomerId(testCustomer.getCustomerId());
        createAccountDTO.setBranchId(testBranch.getBranchId());

        transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setToAccountNumber("0987654321");
        transactionRequestDTO.setFromAccountNumber("1234567890");
        transactionRequestDTO.setAmount(BigDecimal.valueOf(100.0));

        testTransaction = new Transaction();
        testTransaction.setTransactionId(UUID.randomUUID());
        testTransaction.setAmount(BigDecimal.valueOf(100.0));
        testTransaction.setTransactionType(TransactionType.DEPOSIT);
        testTransaction.setAccount(testAccount);

        testAccountDTO = new AccountDTO();

        testAccountDTO.setAccountNumber(testAccount.getAccountNumber());
        testAccountDTO.setBalance(testAccount.getBalance());
        testAccountDTO.setFirstName(testCustomer.getFirstName());
        testAccountDTO.setLastName(testCustomer.getLastName());
        testAccountDTO.setBranchName(testBranch.getBranchName());
        testAccountDTO.setBankName(testBank.getBankName());

        testTransactionDTO = new TransactionDTO();
        testTransactionDTO.setAmount(testTransaction.getAmount());
        testTransactionDTO.setTransactionType(testTransaction.getTransactionType());
    }

    @Test
    void getAccountByAccountNumber_Success() {
        // Arrange
        when(maskedNumber.maskNumber(anyString())).thenReturn("****67890");
        when(accountRepository.getAccountByAccountNumber(testAccount.getAccountNumber())).thenReturn(testAccount);

        // Act
        Account foundAccount = accountService.getAccountByAccountNumber(testAccount.getAccountNumber());

        // Assert
        assertNotNull(foundAccount);
        assertEquals(testAccount.getAccountNumber(), foundAccount.getAccountNumber());
        verify(accountRepository).getAccountByAccountNumber(testAccount.getAccountNumber());
    }

    @Test
    void getAccountByAccountNumber_Not_Found() {
        // Arrange
        String accountNumber = "1234567890";
        when(maskedNumber.maskNumber(anyString())).thenReturn("****67890");
        when(accountRepository.getAccountByAccountNumber(accountNumber)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountByAccountNumber(accountNumber));

        assertEquals("Account Not found", exception.getMessage());
        verify(accountRepository).getAccountByAccountNumber(accountNumber);
    }

    @Test
    void getAccountByAccountNumber_ClosedStatus_ShouldThrowException() {
        // Arrange
        Account closedAccount = new Account();
        closedAccount.setAccountNumber("1234567890");
        closedAccount.setAccountId(UUID.randomUUID());
        closedAccount.setBalance(BigDecimal.valueOf(1000.0));
        closedAccount.setAccountStatus(AccountStatus.CLOSED);

        when(maskedNumber.maskNumber(anyString())).thenReturn("****67890");
        when(accountRepository.getAccountByAccountNumber(closedAccount.getAccountNumber())).thenReturn(closedAccount);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountByAccountNumber(closedAccount.getAccountNumber()));

        assertEquals("Account is Closed", exception.getMessage());
        verify(accountRepository).getAccountByAccountNumber(closedAccount.getAccountNumber());
    }

    @Test
    void createAccount_Success() {
        // Arrange
        when(branchService.findBranchByBranchId(createAccountDTO.getBranchId())).thenReturn(testBranch);
        when(customerService.findCustomerById(createAccountDTO.getCustomerId())).thenReturn(testCustomer);
        when(accountNumberGenerator.generateUniqueAccountNumber(any())).thenReturn("123456");
        when(maskedNumber.maskNumber(anyString())).thenReturn("****67890");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(modelMapper.map(any(Account.class), eq(AccountDTO.class))).thenReturn(testAccountDTO);

        // Act
        AccountDTO result = accountService.createAccount(createAccountDTO);

        // Assert
        assertNotNull(result);
        verify(branchService).findBranchByBranchId(createAccountDTO.getBranchId());
        verify(customerService).findCustomerById(createAccountDTO.getCustomerId());
        verify(accountNumberGenerator).generateUniqueAccountNumber(accountRepository);
        verify(transactionRepository).save(any(Transaction.class));
        verify(accountRepository).save(any(Account.class));
        verify(modelMapper).map(any(Account.class), eq(AccountDTO.class));
    }

    @Test
    void createAccount_InsufficientAmount_ShouldThrowException() {
        // Arrange
        createAccountDTO.setBalance(BigDecimal.valueOf(300.0));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(createAccountDTO));

        assertEquals("You need to deposit more than 500 Rs in bank ", exception.getMessage());
    }

    @Test
    void createAccount_DuplicateAccount_ShouldThrowException() {
        // Arrange
        testCustomer.getAccountList().add(testAccount);
        when(branchService.findBranchByBranchId(createAccountDTO.getBranchId())).thenReturn(testBranch);
        when(customerService.findCustomerById(createAccountDTO.getCustomerId())).thenReturn(testCustomer);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(createAccountDTO));

        assertEquals("Customer already has an account of this type in this branch", exception.getMessage());
    }

    @Test
    void createAccount_MaxAccountsReached_ShouldThrowException() {
        // Arrange: Add 3 existing accounts to customer


        Account existingAccount = new Account();
        existingAccount.setAccountId(UUID.randomUUID());
        Branch testBranch = new Branch();
        testBranch.setBranchId(10L);
        testBranch.setIfscCode("TEST1234567");
        existingAccount.setBranch(testBranch);
        existingAccount.setAccountType(AccountType.CURRENT); // same as createAccountDTO
        testCustomer.getAccountList().add(existingAccount);

        Account existingAccount1 = new Account();
        existingAccount1.setAccountId(UUID.randomUUID());
        Branch testBranch1 = new Branch();
        testBranch1.setIfscCode("TEST1234567");
        testBranch1.setBranchId(2L);
        existingAccount1.setBranch(testBranch1);
        existingAccount1.setAccountType(AccountType.CURRENT);
        testCustomer.getAccountList().add(existingAccount1); // Add the test account to simulate existing accounts

        Account existingAccount2 = new Account();
        existingAccount2.setAccountId(UUID.randomUUID());
        Branch testBranch2 = new Branch();
        testBranch2.setIfscCode("TEST1234567");
        testBranch2.setBranchId(3L);
        existingAccount2.setBranch(testBranch2);
        existingAccount2.setAccountType(AccountType.SAVING);
        testCustomer.getAccountList().add(existingAccount2); // Add the test account to simulate existing accounts


        Account existingAccount3 = new Account();
        existingAccount3.setAccountId(UUID.randomUUID());
        Branch testBranch4 = new Branch();
        testBranch4.setBranchId(4L);
        testBranch4.setIfscCode("TEST1234567");
        existingAccount3.setAccountType(AccountType.SAVING);
        existingAccount3.setBranch(testBranch4);
        testCustomer.getAccountList().add(existingAccount3); // Add the test account to simulate existing accounts




        when(branchService.findBranchByBranchId(createAccountDTO.getBranchId())).thenReturn(testBranch);
        when(customerService.findCustomerById(createAccountDTO.getCustomerId())).thenReturn(testCustomer);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(createAccountDTO));

        assertEquals("Customer already has 3 accounts , customer can't have more than 3 accounts", exception.getMessage());
    }


    @Test
    void getAccountDTOById_Success() {
        // Arrange
        when(accountRepository.findById(testAccount.getAccountId())).thenReturn(Optional.of(testAccount));
        when(maskedNumber.maskNumber(anyString())).thenReturn("****67890");
        when(modelMapper.map(testAccount, AccountDTO.class)).thenReturn(testAccountDTO);

        // Act
        AccountDTO result = accountService.getAccountDTOById(testAccount.getAccountId());

        // Assert
        assertNotNull(result);
        verify(accountRepository).findById(testAccount.getAccountId());
        verify(modelMapper).map(testAccount, AccountDTO.class);
    }

    @Test
    void getAccountDTOById_AccountNotFound_ShouldThrowException() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        when(maskedNumber.maskNumber(anyString())).thenReturn("****67890");

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountDTOById(accountId));

        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository).findById(accountId);
    }

    @Test
    void getAccountById_Success() {
        // Arrange
        when(accountRepository.findById(testAccount.getAccountId())).thenReturn(Optional.of(testAccount));
        when(maskedNumber.maskNumber(anyString())).thenReturn("****67890");

        // Act
        Account result = accountService.getAccountById(testAccount.getAccountId());

        // Assert
        assertNotNull(result);
        assertEquals(testAccount.getAccountId(), result.getAccountId());
        verify(accountRepository).findById(testAccount.getAccountId());
        verify(maskedNumber).maskNumber(testAccount.getAccountId().toString());
    }

    @Test
    void getAccountById_AccountNotFound_ShouldThrowException() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        when(maskedNumber.maskNumber(anyString())).thenReturn("****67890");

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountById(accountId));

        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository).findById(accountId);
        verify(maskedNumber).maskNumber(accountId.toString());
    }

    @Test
    void closeAccount_Success() {
        // Arrange
        when(maskedNumber.maskNumber(anyString())).thenReturn("****67890");
        when(accountRepository.getAccountByAccountNumber(testAccount.getAccountNumber())).thenReturn(testAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        accountService.closeAccount(testAccount.getAccountNumber());

        // Assert
        verify(accountRepository).getAccountByAccountNumber(testAccount.getAccountNumber());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void activateAccount_Success() {
        // Arrange

        when(maskedNumber.maskNumber(anyString())).thenReturn("****67890");
        when(accountRepository.getAccountByAccountNumber(testAccount.getAccountNumber())).thenReturn(testAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        testAccount.setAccountStatus(AccountStatus.CLOSED);
        // Act
        accountService.activateAccount(testAccount.getAccountNumber());

        // Assert
        verify(accountRepository).getAccountByAccountNumber(testAccount.getAccountNumber());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void activateAccount_AlreadyActive_ShouldThrowException() {
        // Arrange
        testAccount.setAccountStatus(AccountStatus.ACTIVE);
        when(maskedNumber.maskNumber(anyString())).thenReturn("****67890");
        when(accountRepository.getAccountByAccountNumber(testAccount.getAccountNumber())).thenReturn(testAccount);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.activateAccount(testAccount.getAccountNumber()));

        assertEquals("Account is already Active", exception.getMessage());
        verify(accountRepository).getAccountByAccountNumber(testAccount.getAccountNumber());
    }

    @Test
    void deposit_Success() {
        // Arrange
        when(maskedNumber.maskNumber(anyString())).thenReturn("****67890");
        when(accountRepository.getAccountByAccountNumber(transactionRequestDTO.getToAccountNumber())).thenReturn(testAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(modelMapper.map(any(Transaction.class), eq(TransactionDTO.class))).thenReturn(testTransactionDTO);

        // Act
        TransactionDTO result = accountService.deposit(transactionRequestDTO);

        // Assert
        assertNotNull(result);
        verify(accountRepository).getAccountByAccountNumber(transactionRequestDTO.getToAccountNumber());
        verify(accountRepository).save(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
        verify(modelMapper).map(any(Transaction.class), eq(TransactionDTO.class));
    }

    @Test
    void deposit_InvalidAmount_ShouldThrowException() {
        // Arrange
        transactionRequestDTO.setAmount(BigDecimal.ZERO);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> accountService.deposit(transactionRequestDTO));

        assertEquals("Deposit amount must be greater than zero", exception.getMessage());
    }

    @Test
    void deposit_NullAmount_ShouldThrowException() {
        // Arrange
        transactionRequestDTO.setAmount(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> accountService.deposit(transactionRequestDTO));

        assertEquals("Deposit amount must be greater than zero", exception.getMessage());
    }

    @Test
    void withDraw_Success() {
        // Arrange
        when(maskedNumber.maskNumber(anyString())).thenReturn("****67890");
        when(accountRepository.getAccountByAccountNumber(transactionRequestDTO.getToAccountNumber())).thenReturn(testAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(modelMapper.map(any(Transaction.class), eq(TransactionDTO.class))).thenReturn(testTransactionDTO);

        // Act
        TransactionDTO result = accountService.withDraw(transactionRequestDTO);

        // Assert
        assertNotNull(result);
        verify(accountRepository).getAccountByAccountNumber(transactionRequestDTO.getToAccountNumber());
        verify(accountRepository).save(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
        verify(modelMapper).map(any(Transaction.class), eq(TransactionDTO.class));
    }

    @Test
    void withDraw_InvalidAmount_ShouldThrowException() {
        // Arrange
        transactionRequestDTO.setAmount(BigDecimal.ZERO);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> accountService.withDraw(transactionRequestDTO));

        assertEquals("Withdrawal amount must be greater than zero", exception.getMessage());
    }

    @Test
    void withDraw_NullAmount_ShouldThrowException() {
        // Arrange
        transactionRequestDTO.setAmount(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> accountService.withDraw(transactionRequestDTO));

        assertEquals("Withdrawal amount must be greater than zero", exception.getMessage());
    }

    @Test
    void withDraw_InsufficientBalance_ShouldThrowException() {
        // Arrange
        testAccount.setBalance(BigDecimal.valueOf(600.0));
        transactionRequestDTO.setAmount(BigDecimal.valueOf(200.0));
        when(maskedNumber.maskNumber(anyString())).thenReturn("****67890");
        when(accountRepository.getAccountByAccountNumber(transactionRequestDTO.getToAccountNumber())).thenReturn(testAccount);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> accountService.withDraw(transactionRequestDTO));

        assertEquals("Withdrawal not allowed: Balance must not fall below ₹500", exception.getMessage());
        verify(accountRepository).getAccountByAccountNumber(transactionRequestDTO.getToAccountNumber());
    }

    @Test
    void transfer_Success() {
        // Arrange
        Account senderAccount = new Account();
        senderAccount.setAccountId(UUID.randomUUID());
        senderAccount.setAccountNumber("1234567890");
        senderAccount.setBalance(BigDecimal.valueOf(1001.0));
        senderAccount.setAccountStatus(AccountStatus.ACTIVE);

        Account receiverAccount = new Account();
        receiverAccount.setAccountId(UUID.randomUUID());
        receiverAccount.setAccountNumber("0987654321");
        receiverAccount.setBalance(BigDecimal.valueOf(601.0));
        receiverAccount.setAccountStatus(AccountStatus.ACTIVE);

        when(maskedNumber.maskNumber(anyString())).thenReturn("****67890");
        when(accountRepository.getAccountByAccountNumber("1234567890")).thenReturn(senderAccount);
        when(accountRepository.getAccountByAccountNumber("0987654321")).thenReturn(receiverAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(senderAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(modelMapper.map(any(Transaction.class), eq(TransactionDTO.class))).thenReturn(testTransactionDTO);



        // Act
        TransactionDTO result = accountService.transfer(transactionRequestDTO);

        // Assert
        assertNotNull(result);
        verify(accountRepository).getAccountByAccountNumber("1234567890");
        verify(accountRepository).getAccountByAccountNumber("0987654321");
        verify(accountRepository, times(2)).save(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
        verify(modelMapper).map(any(Transaction.class), eq(TransactionDTO.class));
    }

    @Test
    void transfer_InsufficientBalance_ShouldThrowException() {
        // Arrange
        Account senderAccount = new Account();
        senderAccount.setAccountId(UUID.randomUUID());
        senderAccount.setAccountNumber("1234567890");
        senderAccount.setBalance(BigDecimal.valueOf(600.0));
        senderAccount.setAccountStatus(AccountStatus.ACTIVE);

        Account receiverAccount = new Account();
        receiverAccount.setAccountId(UUID.randomUUID());
        receiverAccount.setAccountNumber("0987654321");
        receiverAccount.setBalance(BigDecimal.valueOf(500.0));
        receiverAccount.setAccountStatus(AccountStatus.ACTIVE);

        transactionRequestDTO.setAmount(BigDecimal.valueOf(200.0));

        when(maskedNumber.maskNumber(anyString())).thenReturn("****67890");
        when(accountRepository.getAccountByAccountNumber("1234567890")).thenReturn(senderAccount);
        when(accountRepository.getAccountByAccountNumber("0987654321")).thenReturn(receiverAccount);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> accountService.transfer(transactionRequestDTO));

        assertEquals("Transfer denied: Sender's balance must not fall below ₹500", exception.getMessage());
        verify(accountRepository).getAccountByAccountNumber("1234567890");
        verify(accountRepository).getAccountByAccountNumber("0987654321");
    }

    @Test
    void FindAllCustomerAccountByCustomerId_Success() {
        // Arrange
        List<Account> accountList = List.of(testAccount);
        UUID customerId = UUID.randomUUID();
        when(accountRepository.FindAllCustomerAccountByCustomerId(customerId)).thenReturn(accountList);
        when(maskedNumber.maskNumber(anyString())).thenReturn("****67890");

        // Act
        List<Account> result = accountService.FindAllCustomerAccountByCustomerId(customerId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAccount, result.get(0));
        verify(accountRepository).FindAllCustomerAccountByCustomerId(customerId);
        verify(maskedNumber).maskNumber(customerId.toString());
    }

    @Test
    void FindAllCustomerAccountByCustomerId_EmptyList_ShouldThrowException() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        when(accountRepository.FindAllCustomerAccountByCustomerId(customerId)).thenReturn(new ArrayList<>());
        when(maskedNumber.maskNumber(anyString())).thenReturn("****67890");

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> accountService.FindAllCustomerAccountByCustomerId(customerId));

        assertEquals("Customer's Account not found", exception.getMessage());
        verify(accountRepository).FindAllCustomerAccountByCustomerId(customerId);
    }
}