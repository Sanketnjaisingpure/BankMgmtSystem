package com.example.bank.serviceImpl;

import com.example.bank.Enum.AccountStatus;
import com.example.bank.Enum.TransactionType;
import com.example.bank.dto.AccountDTO;
import com.example.bank.dto.CreateAccountDTO;
import com.example.bank.dto.TransactionDTO;
import com.example.bank.dto.TransactionRequestDTO;
import com.example.bank.exception.ResourceNotFoundException;
import com.example.bank.model.Account;
import com.example.bank.model.Branch;
import com.example.bank.model.Customer;
import com.example.bank.model.Transaction;
import com.example.bank.repository.AccountRepository;

import com.example.bank.repository.TransactionRepository;
import com.example.bank.service.AccountService;
import com.example.bank.service.BranchService;
import com.example.bank.service.CustomerService;
import com.example.bank.utils.AccountNumberGenerator;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {


    private final AccountRepository accountRepository;

    private final BranchService branchService;

    private final CustomerService customerService;

    private final TransactionRepository transactionRepository;

    private static final BigDecimal MINIMUM_AMOUNT = BigDecimal.valueOf(500);

    private final ModelMapper modelMapper;

    private final AccountNumberGenerator accountNumberGenerator;

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);


    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository,
                              BranchService branchService,CustomerService customerService,
                              ModelMapper modelMapper, TransactionRepository transactionRepository,
                              AccountNumberGenerator accountNumberGenerator) {
        this.accountRepository= accountRepository;
        this.modelMapper = modelMapper;
        this.transactionRepository = transactionRepository;
        this.accountNumberGenerator = accountNumberGenerator;
        this.branchService =branchService;
        this.customerService = customerService;
    }

    @Override
    public Account getAccountByAccountNumber(String accountNumber){
        log.info("finding Account: {}",accountNumber);
        Account account =accountRepository.getAccountByAccountNumber(accountNumber);
        if (account==null){
            log.warn("Account: {} not found " ,accountNumber);
            throw new ResourceNotFoundException("Account Not found");
        }

        log.info("Checking if Account Status is Closed or not");
        if(account.getAccountStatus().equals(AccountStatus.CLOSED)){
            log.warn("Account: {} Status is Closed  " ,accountNumber);
            throw new ResourceNotFoundException("Account Not found");
        }

        log.info("Account: {} found successfully ",accountNumber);
        return account;
    }

    @Transactional
    @Override
    public AccountDTO createAccount(CreateAccountDTO createAccountDTO) {

        log.info("Account Creation Initiated ");

         BigDecimal amount = createAccountDTO.getBalance();

        Account account = new Account();
        // amount should be greater than 500
        log.info("Checking if amount is greater than 500");
        if (amount.compareTo(MINIMUM_AMOUNT) < 0) {
            log.warn("Deposit must be more than Rs.500  , your Amount : {}", amount);
            throw new IllegalArgumentException("You need to deposit more than 500 Rs in bank ");
        }

        Branch branch = branchService.findBranchByBranchId(createAccountDTO.getBranchId());

        String ifscCodePrefix = branch.getIfscCode().substring(0, 4);
        String accountNumber = accountNumberGenerator.generateUniqueAccountNumber(accountRepository);
        accountNumber = ifscCodePrefix + accountNumber;
        account.setAccountNumber(accountNumber);
        account.setAccountType(createAccountDTO.getAccountType());

        account.setBranch(branch);
        account.setBalance(amount);
        account.setAccountStatus(AccountStatus.ACTIVE);
        Customer customer = customerService.findCustomerById(createAccountDTO.getCustomerId());

        log.info("Checking if Customer already has an account of this type in this branch");
        boolean duplicateAccount = customer.getAccountList().stream().anyMatch(account1 -> account1.getBranch().getBranchId().equals(branch.getBranchId())
                && account1.getAccountType().equals(createAccountDTO.getAccountType()));

        if (duplicateAccount) {
            log.warn("Customer already has an account of type {} in this branch", createAccountDTO.getAccountType());
            throw new IllegalArgumentException("Customer already has an account of this type in this branch");
        }

        // count number of accounts customer has
        long count = customer.getAccountList().stream().map(Account::getAccountNumber).count();

        log.info("Checking if Customer already has 3 accounts");

        if (count >= 3) {
            log.warn("Customer already has 3 accounts, customer can't have more than 3 accounts");
            throw new IllegalArgumentException("Customer already has 3 accounts , customer can't have more than 3 accounts");
        }

        account.setCustomer(customer);
        account.setOpenDate(LocalDate.now());

        log.info("Transaction initiated for creating account: {}", accountNumber);
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setDescription("Transaction done while creating account");
        transaction.setDestinationAccountNumber(accountNumber);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        log.info("transaction successful for creating account: {}", accountNumber);
        transactionRepository.save(transaction);
        accountRepository.save(account);


        AccountDTO  accountDTO = modelMapper.map(account, AccountDTO.class);
        accountDTO.setFirstName(customer.getFirstName());
        accountDTO.setLastName(customer.getLastName());
        accountDTO.setMiddleName(customer.getMiddleName());
        accountDTO.setBranchName(branch.getBranchName());
        accountDTO.setBankName(branch.getBank().getBankName());

        log.info("Account Created Successfully Account: {}", accountNumber);
        return accountDTO;
    }

    @Override
    public AccountDTO getAccountDTOById(UUID accountId) {
        log.info("Get Account DTO initialized ");

        Account account = this.getAccountById(accountId);
        log.info("Account found successfully");
        AccountDTO accountDTO = modelMapper.map(account, AccountDTO.class);
        accountDTO.setFirstName(account.getCustomer().getFirstName());
        accountDTO.setLastName(account.getCustomer().getLastName());
        accountDTO.setMiddleName(account.getCustomer().getMiddleName());
        accountDTO.setBranchName(account.getBranch().getBranchName());
        accountDTO.setBankName(account.getBranch().getBank().getBankName());
        return accountDTO;
    }

    @Override
    public Account getAccountById(UUID accountId){
        log.info("Get Account By Id initiated");
        Account account = accountRepository.findById(accountId).orElseThrow(()->{
            log.warn("Account not found with Id: {}" , accountId);
            return new ResourceNotFoundException("Account not found");
        });
        log.info("Account found Successfully with Id: {}" , accountId);
        return account;
    }


    @Override
    public void closeAccount(String accountNumber) {

        log.info("Closing Account: {} initiated ",accountNumber);
        Account account = this.getAccountByAccountNumber(accountNumber);
        account.setAccountStatus(AccountStatus.CLOSED);
        log.info("Account: {} Closed Successfully ", accountNumber);
        accountRepository.save(account);
    }

    @Override
    public void activateAccount(String accountNumber) {
        log.info("Activating Account: {} initiated ",accountNumber);
        Account account = this.getAccountByAccountNumber(accountNumber);

        if (account.getAccountStatus().equals(AccountStatus.ACTIVE)) {
            log.warn("Account is already Active or Account: {} " ,accountNumber);
            throw new RuntimeException("Account is already Active");
        }

        account.setAccountStatus(AccountStatus.ACTIVE);
        log.info("Account: {} Activated Successfully ", accountNumber);
        accountRepository.save(account);
    }

    @Transactional
    @Override
    public TransactionDTO deposit(TransactionRequestDTO transactionRequestDTO) {

        String accountNumber = transactionRequestDTO.getToAccountNumber();
        BigDecimal amount = transactionRequestDTO.getAmount();

        log.info("Initiating deposit. Account: {}, Amount: ₹{}", accountNumber, amount);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid deposit amount: ₹{} for account: {}", amount, accountNumber);
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }



        Account account = this.getAccountByAccountNumber(accountNumber);

        // Update balance
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        // Save transaction
        log.info("Initiating Transaction for deposit for account: {}", accountNumber);
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setDescription("Amount deposited into account");
        transaction.setDestinationAccountNumber(accountNumber);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        log.info("Transaction successful for deposit for account: {}", accountNumber);
        transactionRepository.save(transaction);

        log.info("Deposit successful. Account: {}, Amount: ₹{}, New Balance: ₹{}",
                accountNumber, amount, account.getBalance());

        return modelMapper.map(transaction, TransactionDTO.class);
    }


    @Transactional
    @Override
    public TransactionDTO withDraw(TransactionRequestDTO transactionRequestDTO) {

        String accountNumber = transactionRequestDTO.getToAccountNumber();
        BigDecimal amount = transactionRequestDTO.getAmount();

        log.info("Initiating withdrawal. Account: {}, Amount: ₹{}", accountNumber, amount);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid withdrawal amount for account {}: ₹{}", accountNumber, amount);
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
        }

        Account account = this.getAccountByAccountNumber(accountNumber);
        BigDecimal currentBalance = account.getBalance();
        BigDecimal newBalance = currentBalance.subtract(amount);

        if (newBalance.compareTo(MINIMUM_AMOUNT) < 0) {
            log.warn("Withdrawal denied. Post-withdrawal balance ₹{} would fall below ₹500 for account {}",
                    newBalance, accountNumber);
            throw new IllegalArgumentException("Withdrawal not allowed: Balance must not fall below ₹500");
        }

        // Update balance
        account.setBalance(newBalance);
        accountRepository.save(account);

        log.info("Initiating Transaction for withdrawal for account: {}", accountNumber);
        // Save transaction
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setAmount(amount);
        transaction.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        transaction.setDescription("Amount withdrawn");
        transaction.setDestinationAccountNumber(accountNumber);
        log.info("Transaction successful for withdrawal for account: {}", accountNumber);
        transactionRepository.save(transaction);

        log.info("Withdrawal successful. Account: {}, Withdrawn: ₹{}, Remaining balance: ₹{}",
                accountNumber, amount, newBalance);

        return modelMapper.map(transaction, TransactionDTO.class);
    }


    @Transactional
    @Override
    public TransactionDTO transfer(TransactionRequestDTO transactionRequestDTO) {

        log.info("Transfer initiated. Sender: {}, Receiver: {}, Amount: ₹{}",
                transactionRequestDTO.getFromAccountNumber(),
                transactionRequestDTO.getToAccountNumber(),
                transactionRequestDTO.getAmount());

        Account senderAccount = this.getAccountByAccountNumber(transactionRequestDTO.getFromAccountNumber());
        Account receiverAccount = this.getAccountByAccountNumber(transactionRequestDTO.getToAccountNumber());

        BigDecimal transferAmount = transactionRequestDTO.getAmount();
        BigDecimal senderBalance = senderAccount.getBalance();

        // Check if sender has enough balance to keep ₹500 after transfer
        BigDecimal postTransferBalance = senderBalance.subtract(transferAmount);
        if (postTransferBalance.compareTo(MINIMUM_AMOUNT) < 0) {
            log.warn("Transfer denied: Post-transfer balance ₹{} would fall below ₹500 for account {}",
                    postTransferBalance, senderAccount.getAccountNumber());
            throw new IllegalArgumentException("Transfer denied: Sender's balance must not fall below ₹500");
        }

        // Perform transfer
        senderAccount.setBalance(postTransferBalance);
        receiverAccount.setBalance(receiverAccount.getBalance().add(transferAmount));

        accountRepository.save(senderAccount);

        accountRepository.save(receiverAccount);

        log.info("Initiating Transaction for ");
        // Save transaction
        Transaction transaction = new Transaction();
        transaction.setAccount(senderAccount);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setAmount(transferAmount);
        transaction.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        transaction.setDestinationAccountNumber(transactionRequestDTO.getToAccountNumber());
        transaction.setDescription("Transferred ₹" + transferAmount + " to account " + receiverAccount.getAccountNumber());

        transactionRepository.save(transaction);

        log.info("Transfer successful. Sender: {}, Receiver: {}, Amount: ₹{}, Remaining Balance: ₹{}",
                senderAccount.getAccountNumber(),
                receiverAccount.getAccountNumber(),
                transferAmount,
                postTransferBalance);

        return modelMapper.map(transaction, TransactionDTO.class);
    }

    @Override
    public List<Account> FindAllCustomerAccountByCustomerId(UUID customerId){
        log.info("Initiating Get All Customer's Account's ");
        List<Account> accountList = accountRepository.FindAllCustomerAccountByCustomerId(customerId);
        if (accountList.isEmpty()) {
            log.warn("Customer's Account not found");
            throw new ResourceNotFoundException("Customer's Account not found");
        }
        log.info("Found all Customer's Account list");
        return accountList;
    }

}
