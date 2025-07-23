package com.example.bank.serviceImpl;

import com.example.bank.Enum.AccountType;
import com.example.bank.Enum.LoanStatus;
import com.example.bank.Enum.TransactionType;
import com.example.bank.dto.*;
import com.example.bank.exception.ResourceNotFoundException;
import com.example.bank.model.*;
import com.example.bank.repository.*;
import com.example.bank.service.*;
import com.example.bank.utils.MaskedNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {
    @Mock private LoanRepository loanRepository;
    @Mock private EmployeeService employeeService;
    @Mock private TransactionRepository transactionRepository;
    @Mock private CustomerService customerService;
    @Mock private AccountService accountService;
    @Mock private ModelMapper modelMapper;
    @Mock private MaskedNumber maskedNumber;
    @InjectMocks private LoanServiceImpl loanServiceImpl;


    private Loan testLoan;
    private Account testAccount;
    private Branch testBranch;
    private Customer testCustomer;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setCustomerId(UUID.randomUUID());
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("johb@gmail.com");
        testCustomer.setPhoneNumber("1234567890");


        testBranch = new Branch();
        testBranch.setBranchId(1232323L);
        testBranch.setBranchName("Test Branch");
        testBranch.setIfscCode("TEST1234");



        testAccount = new Account();
        testAccount.setAccountNumber("123456789");
        testAccount.setBalance(BigDecimal.valueOf(10000));
        testAccount.setAccountType(AccountType.SAVING);
        testAccount.setAccountId(UUID.randomUUID());

        testEmployee = new Employee();
        testEmployee.setEmployeeId(1434343L);
        testEmployee.setFirstName("Jane");
        testEmployee.setLastName("Smith");
        testEmployee.setEmail("hoh@gmail.com");
        testEmployee.setPassword("0987654321");
        testEmployee.setBranch(testBranch);

        testLoan = new Loan();
        testLoan.setLoanId(UUID.randomUUID());
        testLoan.setLoanStatus(LoanStatus.ACTIVE);
        testLoan.setTotalAmount(BigDecimal.valueOf(10000));
        testLoan.setAmountPaid(BigDecimal.ZERO);
        testLoan.setTermInMonth(12);
        testLoan.setAccount(testAccount);


    }

    @Test
    void testApplyForLoan_success() {
        LoanApplicationDTO dto = new LoanApplicationDTO();
        dto.setTotalAmount(BigDecimal.valueOf(100000));
        dto.setCustomerId(testCustomer.getCustomerId());
        dto.setEmployeeId(testEmployee.getEmployeeId());
        dto.setAccountNumber("123");
        Customer customer =testCustomer;
        customer.setLoanList(List.of(testLoan));
        Employee employee =testEmployee;
        Branch branch = testBranch;

        employee.setBranch(branch);
        Account account = testAccount;
        account.setCustomer(customer);
        account.setBranch(branch);
        Loan loan = testLoan;
        LoanDTO loanDTO = new LoanDTO();
        when(customerService.findCustomerById(dto.getCustomerId())).thenReturn(customer);
        when(employeeService.getEmployeeById(dto.getEmployeeId())).thenReturn(employee);
        when(accountService.getAccountByAccountNumber(dto.getAccountNumber())).thenReturn(account);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(maskedNumber.maskNumber(anyString())).thenReturn("***");
        when(modelMapper.map(any(Loan.class), eq(LoanDTO.class))).thenReturn(loanDTO);
        LoanDTO result = loanServiceImpl.applyForLoan(dto);
        assertNotNull(result);
    }

    @Test
    void testApplyForLoan_invalidAmount() {
        LoanApplicationDTO dto = new LoanApplicationDTO();
        dto.setTotalAmount(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> loanServiceImpl.applyForLoan(dto));
    }

    @Test
    void testApplyForLoan_maxLoansReached() {
        LoanApplicationDTO dto = new LoanApplicationDTO();
        dto.setTotalAmount(BigDecimal.valueOf(10000));
        dto.setCustomerId(testCustomer.getCustomerId());
        dto.setEmployeeId(testEmployee.getEmployeeId());
        dto.setAccountNumber(testAccount.getAccountNumber());
        Customer customer = testCustomer;
        customer.setLoanList(Arrays.asList(testLoan, testLoan, testLoan)); // 3 loans already
        when(customerService.findCustomerById(dto.getCustomerId())).thenReturn(customer);
        when(maskedNumber.maskNumber(anyString())).thenReturn("***");
        assertThrows(IllegalArgumentException.class, () -> loanServiceImpl.applyForLoan(dto));
    }

    @Test
    void testApplyForLoan_accountNotBelongToCustomer() {
        LoanApplicationDTO dto = new LoanApplicationDTO();
        dto.setTotalAmount(BigDecimal.valueOf(10000));
        dto.setCustomerId(testCustomer.getCustomerId());
        dto.setAccountNumber(testAccount.getAccountNumber());
        Customer customer = testCustomer;
        customer.setCustomerId(UUID.randomUUID());
        customer.setLoanList(List.of(testLoan));
        Employee employee =testEmployee;
        employee.setEmployeeId(1212121L);
        Branch branch = testBranch;
        employee.setBranch(branch);
        Account account =testAccount;
        account.setCustomer(customer); // different customer
        account.setBranch(branch);
        when(customerService.findCustomerById(dto.getCustomerId())).thenReturn(customer);
        when(employeeService.getEmployeeById(dto.getEmployeeId())).thenReturn(employee);
        when(accountService.getAccountByAccountNumber(dto.getAccountNumber())).thenReturn(account);
        when(maskedNumber.maskNumber(anyString())).thenReturn("***");
        assertThrows(IllegalArgumentException.class, () -> loanServiceImpl.applyForLoan(dto));
    }

    @Test
    void testApplyForLoan_employeeDifferentBranch() {
        LoanApplicationDTO dto = new LoanApplicationDTO();
        dto.setTotalAmount(BigDecimal.valueOf(10000));
        dto.setCustomerId(UUID.randomUUID());
        dto.setEmployeeId(1L);
        dto.setAccountNumber("123");
        Customer customer = testCustomer;
        customer.setLoanList(List.of(testLoan));
        Employee employee =testEmployee;
        employee.setEmployeeId(11212L);
        Branch branch1 = testBranch;
        branch1.setBranchId(1121211L);
        Branch branch2 = testBranch;
        branch2.setBranchId(1212111122L);
        employee.setBranch(branch1);
        Account account = testAccount;
        account.setAccountNumber("123");
        account.setCustomer(customer);
        account.setBranch(branch2);
        when(customerService.findCustomerById(dto.getCustomerId())).thenReturn(customer);
        when(employeeService.getEmployeeById(dto.getEmployeeId())).thenReturn(employee);
        when(accountService.getAccountByAccountNumber(dto.getAccountNumber())).thenReturn(account);
        when(maskedNumber.maskNumber(anyString())).thenReturn("***");
        assertThrows(IllegalArgumentException.class, () -> loanServiceImpl.applyForLoan(dto));
    }

    @Test
    void testGetLoanById_success() {
        Loan loan =testLoan;
        LoanDTO loanDTO = new LoanDTO();
        when(maskedNumber.maskNumber(anyString())).thenReturn("***");
        when(loanRepository.findById(loan.getLoanId())).thenReturn(Optional.of(loan));
        when(modelMapper.map(any(Loan.class), eq(LoanDTO.class))).thenReturn(loanDTO);
        LoanDTO result = loanServiceImpl.getLoanById(loan.getLoanId());
        assertNotNull(result);
    }

    @Test
    void testGetLoanById_notFound() {
        UUID loanId = UUID.randomUUID();
        when(maskedNumber.maskNumber(anyString())).thenReturn("***");
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> loanServiceImpl.getLoanById(loanId));
    }

    @Test
    void testFindLoanByLoanId_success() {
        Loan loan = testLoan;
        when(maskedNumber.maskNumber(anyString())).thenReturn("***");
        when(loanRepository.findById(loan.getLoanId())).thenReturn(Optional.of(loan));
        Loan result = loanServiceImpl.findLoanByLoanId(loan.getLoanId());
        assertNotNull(result);
        assertEquals(loan.getLoanId(), result.getLoanId());
    }

    @Test
    void testFindLoanByLoanId_notFound() {
        UUID loanId = UUID.randomUUID();
        when(maskedNumber.maskNumber(anyString())).thenReturn("***");
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> loanServiceImpl.findLoanByLoanId(loanId));
    }

    @Test
    void testGetLoanCustomerById_success() {

        Customer customer = testCustomer;
        customer.setLoanList(List.of(testLoan));
        LoanDTO loanDTO = new LoanDTO();
        when(maskedNumber.maskNumber(anyString())).thenReturn("***");
        when(customerService.findCustomerById(customer.getCustomerId())).thenReturn(customer);
        when(modelMapper.map(any(Loan.class), eq(LoanDTO.class))).thenReturn(loanDTO);
        List<LoanDTO> result = loanServiceImpl.getLoanCustomerById(customer.getCustomerId());
        assertEquals(1, result.size());
    }

    @Test
    void testMakeLoanPayment_success() {
        LoanApplyDTO dto = new LoanApplyDTO();
        dto.setLoanId(testLoan.getLoanId());
        dto.setTotalAmount(BigDecimal.valueOf(1000.00));
        Loan loan =testLoan;
        loan.setLoanId(dto.getLoanId());
        loan.setLoanStatus(LoanStatus.ACTIVE);
        loan.setTotalAmount(BigDecimal.valueOf(12000));
        loan.setAmountPaid(BigDecimal.valueOf(11000));
        loan.setTermInMonth(12);
        Account account = testAccount;
        loan.setAccount(account);
        LoanInstallmentDTO installmentDTO = new LoanInstallmentDTO();
        when(maskedNumber.maskNumber(anyString())).thenReturn("***");
        when(loanRepository.findById(dto.getLoanId())).thenReturn(Optional.of(loan));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(modelMapper.map(any(Loan.class), eq(LoanInstallmentDTO.class))).thenReturn(installmentDTO);
        LoanInstallmentDTO result = loanServiceImpl.makeLoanPayment(dto);
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(12000), loan.getTotalAmount());
    }

    @Test
    void testMakeLoanPayment_invalidAmount() {
        LoanApplyDTO dto = new LoanApplyDTO();
        dto.setLoanId(UUID.randomUUID());
        dto.setTotalAmount(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> loanServiceImpl.makeLoanPayment(dto));
    }

    @Test
    void testMakeLoanPayment_rejectedLoan() {
        LoanApplyDTO dto = new LoanApplyDTO();
        dto.setLoanId(testLoan.getLoanId());
        dto.setTotalAmount(BigDecimal.valueOf(1000));
        Loan loan = testLoan;
        loan.setLoanStatus(LoanStatus.REJECT);
        when(maskedNumber.maskNumber(anyString())).thenReturn("***");
        when(loanRepository.findById(dto.getLoanId())).thenReturn(Optional.of(loan));
        assertThrows(IllegalStateException.class, () -> loanServiceImpl.makeLoanPayment(dto));
    }

    @Test
    void testMakeLoanPayment_paidOffLoan() {
        LoanApplyDTO dto = new LoanApplyDTO();
        dto.setLoanId(testLoan.getLoanId());
        dto.setTotalAmount(BigDecimal.valueOf(1000));
        Loan loan = testLoan;
        loan.setLoanStatus(LoanStatus.PAID_OFF);
        when(maskedNumber.maskNumber(anyString())).thenReturn("***");
        when(loanRepository.findById(dto.getLoanId())).thenReturn(Optional.of(loan));
        assertThrows(IllegalStateException.class, () -> loanServiceImpl.makeLoanPayment(dto));
    }

    @Test
    void testMakeLoanPayment_overPayment() {
        LoanApplyDTO dto = new LoanApplyDTO();
        dto.setLoanId(testLoan.getLoanId());
        dto.setTotalAmount(BigDecimal.valueOf(2000));
        Loan loan = testLoan;
        loan.setLoanId(dto.getLoanId());
        loan.setLoanStatus(LoanStatus.ACTIVE);
        loan.setTotalAmount(BigDecimal.valueOf(12000));
        loan.setAmountPaid(BigDecimal.valueOf(11000));
        loan.setTermInMonth(12);
        when(maskedNumber.maskNumber(anyString())).thenReturn("***");
        when(loanRepository.findById(dto.getLoanId())).thenReturn(Optional.of(loan));
        assertThrows(IllegalArgumentException.class, () -> loanServiceImpl.makeLoanPayment(dto));
    }

    @Test
    void testMakeLoanPayment_invalidMonthlyPayment() {
        LoanApplyDTO dto = new LoanApplyDTO();
        dto.setLoanId(testLoan.getLoanId());
        dto.setTotalAmount(BigDecimal.valueOf(500));
        Loan loan = testLoan;
        loan.setLoanId(dto.getLoanId());
        loan.setLoanStatus(LoanStatus.ACTIVE);
        loan.setTotalAmount(BigDecimal.valueOf(12000));
        loan.setAmountPaid(BigDecimal.valueOf(11000));
        loan.setTermInMonth(12);
        when(maskedNumber.maskNumber(anyString())).thenReturn("***");
        when(loanRepository.findById(dto.getLoanId())).thenReturn(Optional.of(loan));
        assertThrows(IllegalArgumentException.class, () -> loanServiceImpl.makeLoanPayment(dto));
    }
} 