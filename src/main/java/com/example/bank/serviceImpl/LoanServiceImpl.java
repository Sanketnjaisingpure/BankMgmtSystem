package com.example.bank.serviceImpl;

import com.example.bank.Enum.LoanStatus;
import com.example.bank.Enum.TransactionType;
import com.example.bank.dto.LoanApplicationDTO;
import com.example.bank.dto.LoanApplyDTO;
import com.example.bank.dto.LoanDTO;
import com.example.bank.dto.LoanInstallmentDTO;
import com.example.bank.exception.ResourceNotFoundException;
import com.example.bank.model.*;
import com.example.bank.repository.*;
import com.example.bank.service.AccountService;
import com.example.bank.service.CustomerService;
import com.example.bank.service.EmployeeService;
import com.example.bank.service.LoanService;
import com.example.bank.utils.MaskedNumber;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final EmployeeService employeeService;
    private final TransactionRepository transactionRepository;
    private final CustomerService customerService;
    private final AccountService accountService;
    private final ModelMapper modelMapper ;
    private final MaskedNumber maskedNumber;
    private static final Logger log = LoggerFactory.getLogger(LoanServiceImpl.class);

    @Autowired
    public LoanServiceImpl(LoanRepository loanRepository , AccountService accountService, TransactionRepository transactionRepository
            , @Lazy EmployeeService employeeService,MaskedNumber maskedNumber , ModelMapper modelMapper, CustomerService customerService){
        this.transactionRepository = transactionRepository;
        this.loanRepository = loanRepository;
        this.maskedNumber = maskedNumber;
        this.employeeService =employeeService;
        this.accountService = accountService;
        this.modelMapper = modelMapper;
        this.customerService = customerService;
    }

    @Transactional
    @Override
    public LoanDTO applyForLoan(LoanApplicationDTO loanApplicationDTO) {
        log.info("Apply for loan initiated");

        if (loanApplicationDTO.getTotalAmount() == null || loanApplicationDTO.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Loan amount must be greater than 0");
        }

        Loan loan = new Loan();

        double interestRate = 0;
        int termInMonth = 0;

        if (loanApplicationDTO.getTotalAmount().compareTo(BigDecimal.valueOf(50000)) < 0) {
            interestRate = 8;
            termInMonth = 12;
        }
        // if amount is between 50,000 and 1,00,000
        else if (loanApplicationDTO.getTotalAmount().compareTo(BigDecimal.valueOf(50000)) >= 0 &&
                 loanApplicationDTO.getTotalAmount().compareTo(BigDecimal.valueOf(100000)) < 0) {
            interestRate = 10;
            termInMonth = 24;
        }
        // if amount is between 1,00,000 and 2,00,000
        else if (loanApplicationDTO.getTotalAmount().compareTo(BigDecimal.valueOf(100000)) >= 0 &&
                 loanApplicationDTO.getTotalAmount().compareTo(BigDecimal.valueOf(200000)) < 0) {
           interestRate = 12;
            termInMonth = 36;
        }
        // if amount is between 2,00,000 and 5,00,000
        else if (loanApplicationDTO.getTotalAmount().compareTo(BigDecimal.valueOf(200000)) >= 0 &&
                 loanApplicationDTO.getTotalAmount().compareTo(BigDecimal.valueOf(500000)) < 0) {
           interestRate = 14;
            termInMonth = 48;
        }
        // if amount is greater than 5,00,000
        else if (loanApplicationDTO.getTotalAmount().compareTo(BigDecimal.valueOf(500000)) >= 0) {
            interestRate = 16;
            termInMonth = 60;
        }

        loan.setLoanStatus(LoanStatus.PENDING);
        loan.setLoanType(loanApplicationDTO.getLoanType());

        loan.setTermInMonth(termInMonth);
        loan.setInterestRate(interestRate);

        Customer customer = customerService.findCustomerById(loanApplicationDTO.getCustomerId());
        loan.setCustomer(customer);

        int loanCount = customer.getLoanList().size();
        if (loanCount >= 3){
            log.warn("Customer has reached the maximum number of loans. Customer ID: {}", maskedNumber.maskNumber(loanApplicationDTO.getCustomerId().toString()));
            throw new IllegalArgumentException("Customer has reached the maximum number of loans");
        }

        Employee employee = employeeService.getEmployeeById(loanApplicationDTO.getEmployeeId());

        loan.setEmployee(employee);

        Account account = accountService.getAccountByAccountNumber(loanApplicationDTO.getAccountNumber());

        if(!account.getCustomer().getCustomerId().equals(loanApplicationDTO.getCustomerId())){

            log.warn("Account does not belong to the customer. Customer ID: {}, Account Number: {}",
                    maskedNumber.maskNumber(loanApplicationDTO.getCustomerId().toString()), maskedNumber.maskNumber(loanApplicationDTO.getAccountNumber()));

            throw new IllegalArgumentException("Account does not belong to the customer");
        }

        if (!employee.getBranch().getBranchId().equals(account.getBranch().getBranchId())){
            log.warn("Employee does not belong to the same branch as the account. Employee ID: {}, Account Number: {}",
                  loanApplicationDTO.getEmployeeId(),maskedNumber.maskNumber(loanApplicationDTO.getAccountNumber()));

            throw new IllegalArgumentException("Employee does not belong to the same branch as the account");
        }

        loan.setAccount(account);

        loan.setAmountPaid(BigDecimal.ZERO);
        // Fix interest calculation
        BigDecimal interestRateN = BigDecimal.valueOf(interestRate).divide(BigDecimal.valueOf(100));
        BigDecimal totalAmountWithInterest = loanApplicationDTO.getTotalAmount().add(
                loanApplicationDTO.getTotalAmount().multiply(interestRateN));
        loan.setTotalAmount(totalAmountWithInterest);

        loanRepository.save(loan);
        log.info("Loan application recorded. Customer ID: {}, Account: {}, Amount: {}",
               maskedNumber.maskNumber( loanApplicationDTO.getCustomerId().toString()), maskedNumber.maskNumber( loanApplicationDTO.getAccountNumber())
                , totalAmountWithInterest);

        return modelMapper.map(loan, LoanDTO.class);
    }


    @Override
    public LoanDTO getLoanById(UUID loanId) {
        log.info("Initializing get loan by ID: {}", maskedNumber.maskNumber(loanId.toString()));
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        log.info("Loan found successfully with ID: {}", maskedNumber.maskNumber(loanId.toString()));
        return modelMapper.map(loan,LoanDTO.class);
    }

    @Override
    public Loan findLoanByLoanId(UUID loanId) {
        log.info("Initializing get by loan  ID: {}", maskedNumber.maskNumber(loanId.toString()));
        return loanRepository.findById(loanId).orElseThrow(() -> new ResourceNotFoundException("loan not found "));
    }


    @Override
    public List<LoanDTO> getLoanCustomerById(UUID customerId) {
        log.info("Initializing get loan by customer ID: {}", maskedNumber.maskNumber(customerId.toString()));
        Customer customer = customerService.findCustomerById(customerId);
        log.info("Loan found successfully ");
        return customer.getLoanList().stream().map(loan -> modelMapper.map(loan,LoanDTO.class)).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public LoanInstallmentDTO makeLoanPayment(LoanApplyDTO loanApplyDTO) {
        log.info("Loan payment initiated. Loan ID: {}, Amount: {}", maskedNumber.maskNumber( loanApplyDTO.getLoanId().toString()), loanApplyDTO.getTotalAmount());

        if (loanApplyDTO.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }

        Loan loan = this.findLoanByLoanId(loanApplyDTO.getLoanId());
        if (loan.getLoanStatus() == LoanStatus.REJECT) {
            throw new IllegalStateException("Cannot pay a rejected loan");
        }
        if (loan.getLoanStatus() == LoanStatus.PAID_OFF) {
            throw new IllegalStateException("Loan already paid off");
        }

        BigDecimal needToPayPerMonth = loan.getTotalAmount()
                .divide(BigDecimal.valueOf(loan.getTermInMonth()), 2, RoundingMode.HALF_UP);

        if (loanApplyDTO.getTotalAmount().compareTo(needToPayPerMonth) != 0) {
            log.info("Invalid monthly payment. Expected: {}, Provided: {}",
                    needToPayPerMonth, loanApplyDTO.getTotalAmount());
            throw new IllegalArgumentException("Monthly payment must be exactly ₹" + needToPayPerMonth);
        }




        BigDecimal totalAmount = loan.getTotalAmount();
        BigDecimal newPaidAmount = loan.getAmountPaid().add(loanApplyDTO.getTotalAmount());

        if (newPaidAmount.compareTo(totalAmount) > 0) {
            throw new IllegalArgumentException("Payment exceeds the remaining loan amount");
        }


        // Save transaction
        log.info("Saving loan payment transaction. Loan ID: {}, Amount: {}", maskedNumber.maskNumber( loanApplyDTO.getLoanId().toString()), loanApplyDTO.getTotalAmount());
        Transaction transaction = new Transaction();
        transaction.setAmount(loanApplyDTO.getTotalAmount());
        transaction.setDescription("Loan payment");
        transaction.setTransactionType(TransactionType.LOAN_PAYMENT);
        transaction.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        transaction.setAccount(loan.getAccount());
        transaction.setDestinationAccountNumber(null);
        transactionRepository.save(transaction);

        // Update loan
        loan.setAmountPaid(newPaidAmount);
        loan.setLoanStatus(newPaidAmount.compareTo(totalAmount) == 0 ? LoanStatus.PAID_OFF : LoanStatus.ACTIVE);
        loanRepository.save(loan);

        log.info("Loan payment recorded. Loan ID: {}, Remaining Amount: {}", maskedNumber.maskNumber( loanApplyDTO.getLoanId().toString()), totalAmount.subtract(newPaidAmount));
        return modelMapper.map(loan, LoanInstallmentDTO.class);
    }
}
