package com.example.bank.serviceImpl;

import com.example.bank.Enum.LoanStatus;
import com.example.bank.dto.*;
import com.example.bank.exception.ResourceNotFoundException;
import com.example.bank.model.*;
import com.example.bank.repository.*;
import com.example.bank.service.BranchService;
import com.example.bank.service.LoanService;
import com.example.bank.utils.MaskedNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {
    @Mock private EmployeeRepository employeeRepository;
    @Mock private ModelMapper modelMapper;
    @Mock private LoanService loanService;
    @Mock private UserDetailsRepo userDetailsRepo;
    @Mock private BranchService branchService;
    @Mock private LoanRepository loanRepository;
    @Mock private MaskedNumber maskedNumber;
    @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;
    @InjectMocks private EmployeeServiceImpl employeeServiceImpl;

    private  Employee testEmployee;
    private Address testAddress;
    private  Branch testBranch;
    private Bank testBank;

    @BeforeEach
    void setUp() {

        testAddress = new Address();
        testAddress.setAddressId(112121212L);
        testAddress.setStreet("123 Main St");
        testAddress.setCity("Springfield");
        testAddress.setState("IL");
        testAddress.setZipCode("627013");
        testAddress.setCountry("USA");

        testBank = new Bank();
        testBank.setBankId(UUID.randomUUID());
        testBank.setBankName("Test Bank");
        testBank.setHeadOfficeAddress("123 Bank St, Springfield, IL, 62701, USA");

        testBranch = new Branch();
        testBranch.setBranchId(1232323L);
        testBranch.setBranchName("Main Branch");
        testBranch.setIfscCode("IFSC1234");
        testBranch.setAddress(testAddress);
        testBranch.setBank(testBank);



        testEmployee = new Employee();
        testEmployee.setEmployeeId(12121211L);
        testEmployee.setAddress(testAddress);
        testEmployee.setPassword("testPassword");
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setEmail("john@gmail.com");
        testEmployee.setBranch(testBranch);

    }

    @Test
    void testGetEmployeeById_success() {
        Employee employee = testEmployee;
        employee.setEmployeeId(1232323L);
        when(maskedNumber.maskNumber(employee.getEmployeeId().toString())).thenReturn("*********1234");
        when(employeeRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        Employee result = employeeServiceImpl.getEmployeeById(employee.getEmployeeId());
        assertNotNull(result);
        assertEquals(1232323L, result.getEmployeeId());
    }

    @Test
    void testGetEmployeeById_notFound() {
        ResourceNotFoundException ex =  assertThrows(ResourceNotFoundException.class, () -> employeeServiceImpl.getEmployeeById(2121212L));
        assertEquals("Employee not found with id: 2121212", ex.getMessage());
    }

    @Test
    void testAddEmployee_success() {
        CreateEmployeeDTO dto = new CreateEmployeeDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john@doe.com");
        dto.setPassword(testEmployee.getPassword());
        dto.setBranchId(testBranch.getBranchId());
        AddressDTO addressDTO = new AddressDTO();
        dto.setAddressDTO(addressDTO);
        Branch branch =testBranch;
        branch.setBranchName("Main");
        Bank bank = testBank;
        bank.setBankName("Bank");
        branch.setBank(bank);
        Employee employee = testEmployee;
        employee.setEmployeeId(123423L);
        employee.setBranch(branch);
        employee.setAddress(testAddress);
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setBranchName("Main");
        employeeDTO.setBankName("Bank");
        employeeDTO.setAddressDTO(new AddressDTO());
        when(modelMapper.map(any(AddressDTO.class), eq(Address.class))).thenReturn(testAddress);
        when(branchService.findBranchByBranchId(branch.getBranchId())).thenReturn(branch);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> {
            Employee e = i.getArgument(0);
            e.setEmployeeId(2323231L);
            return e;
        });
        when(userDetailsRepo.findByUsername("john@doe.com")).thenReturn(null);
        when(bCryptPasswordEncoder.encode(employee.getPassword())).thenReturn("hashed");
        when(modelMapper.map(any(Employee.class), eq(EmployeeDTO.class))).thenReturn(employeeDTO);
        when(modelMapper.map(any(Address.class), eq(AddressDTO.class))).thenReturn(new AddressDTO());
        when(userDetailsRepo.save(any(Users.class))).thenReturn(new Users());
        EmployeeDTO result = employeeServiceImpl.addEmployee(dto);
        assertNotNull(result);
        assertEquals("Main", result.getBranchName());
        assertEquals("Bank", result.getBankName());
    }

    @Test
    void testAddEmployee_duplicateEmail() {
        CreateEmployeeDTO dto = new CreateEmployeeDTO();
        dto.setFirstName("John");
        dto.setEmail("john@doe.com");
        dto.setPassword(testEmployee.getPassword());
        dto.setBranchId(testBranch.getBranchId());
        dto.setLastName("Doe");
        AddressDTO addressDTO = new AddressDTO();
        dto.setAddressDTO(addressDTO);
        Employee employee = testEmployee;
        employee.setEmployeeId(12312342L);

        Users users = new Users();
        users.setUsername(dto.getEmail());
        users.setPassword(users.getPassword());
        users.setRole("ROLE_EMPLOYEE");
        users.setLinkedEntityId(employee.getEmployeeId().toString());

        when(branchService.findBranchByBranchId(dto.getBranchId())).thenReturn(testBranch);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee emp = invocation.getArgument(0);
            emp.setEmployeeId(1002324L); // simulate DB-generated ID
            return emp;
        });

        when(userDetailsRepo.findByUsername("john@doe.com")).thenReturn(users);
        System.out.println("employee: " + employee.getEmployeeId());
       IllegalArgumentException ex =  assertThrows(IllegalArgumentException.class, () -> employeeServiceImpl.addEmployee(dto));
        assertEquals("User with this email already exists", ex.getMessage());
    }

    @Test
    void testGetAllLoansByStatus_success() {
        Loan loan = new Loan();
        loan.setLoanId(UUID.randomUUID());
        List<Loan> loans = List.of(loan);
        LoanDTO loanDTO = new LoanDTO();
        when(loanRepository.findAllLoanByLoanStatus(LoanStatus.PENDING)).thenReturn(loans);
        when(modelMapper.map(any(Loan.class), eq(LoanDTO.class))).thenReturn(loanDTO);
        List<LoanDTO> result = employeeServiceImpl.getAllLoansByStatus(LoanStatus.PENDING);
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllLoansByStatus_notFound() {
        when(loanRepository.findAllLoanByLoanStatus(LoanStatus.PAID_OFF)).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> employeeServiceImpl.getAllLoansByStatus(LoanStatus.PAID_OFF));
    }

    @Test
    void testGetEmployeeByBranch_success() {
        Branch branch = testBranch;
        Employee emp = testEmployee;

        branch.setEmployeeList(List.of(emp));
        EmployeeDTO employeeDTO = new EmployeeDTO();
        when(branchService.findBranchByBranchId(branch.getBranchId())).thenReturn(branch);
        when(modelMapper.map(any(Employee.class), eq(EmployeeDTO.class))).thenReturn(employeeDTO);
        List<EmployeeDTO> result = employeeServiceImpl.getEmployeeByBranch(branch.getBranchId());
        assertEquals(1, result.size());
    }

    @Test
    void testGetEmployeeByBranch_notFound() {
        Branch branch = testBranch;

        branch.setEmployeeList(Collections.emptyList());
        when(branchService.findBranchByBranchId(branch.getBranchId())).thenReturn(branch);
        assertThrows(ResourceNotFoundException.class, () -> employeeServiceImpl.getEmployeeByBranch(branch.getBranchId()));
    }

    @Test
    void testApproveLoan_success() {
        Loan loan = new Loan();
        loan.setLoanId(UUID.randomUUID());
        loan.setLoanStatus(LoanStatus.PENDING);
        Account account = new Account();
        Branch branch = testBranch;
        account.setBranch(branch);
        loan.setAccount(account);
        Employee employee = testEmployee;
        employee.setBranch(branch);
        employee.setLoanList(new ArrayList<>());
        LoanRequestDTO req = new LoanRequestDTO();
        req.setLoanId(loan.getLoanId());
        req.setEmployeeId(testEmployee.getEmployeeId());
        LoanDTO loanDTO = new LoanDTO();
        when(maskedNumber.maskNumber(anyString())).thenReturn("***");
        when(loanService.findLoanByLoanId(loan.getLoanId())).thenReturn(loan);
        when(employeeRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        when(modelMapper.map(any(Loan.class), eq(LoanDTO.class))).thenReturn(loanDTO);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        LoanDTO result = employeeServiceImpl.approveLoan(req);
        assertNotNull(result);
        assertEquals(LoanStatus.APPROVED, loan.getLoanStatus());
    }

    @Test
    void testApproveLoan_alreadyRejected() {
        Loan loan = new Loan();
        loan.setLoanId(UUID.randomUUID());
        loan.setLoanStatus(LoanStatus.REJECT);
        LoanRequestDTO req = new LoanRequestDTO();
        req.setLoanId(loan.getLoanId());
        when(loanService.findLoanByLoanId(loan.getLoanId())).thenReturn(loan);
        assertThrows(IllegalArgumentException.class, () -> employeeServiceImpl.approveLoan(req));
    }

    @Test
    void testApproveLoan_differentBranch() {
        Loan loan = new Loan();
        loan.setLoanId(UUID.randomUUID());
        loan.setLoanStatus(LoanStatus.PENDING);
        Account account = new Account();
        Branch branch1 =  new Branch();
        branch1.setBranchId(9999999L);
        Branch branch2 = testBranch;
        account.setBranch(branch2);
        loan.setAccount(account);
        Employee employee = testEmployee;
        employee.setBranch(branch1);
        LoanRequestDTO req = new LoanRequestDTO();
        req.setLoanId(loan.getLoanId());
        req.setEmployeeId(employee.getEmployeeId());
        when(loanService.findLoanByLoanId(loan.getLoanId())).thenReturn(loan);
        when(employeeRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        assertThrows(IllegalArgumentException.class, () -> employeeServiceImpl.approveLoan(req));
    }

    @Test
    void testRejectLoan_success() {
        Loan loan = new Loan();
        loan.setLoanId(UUID.randomUUID());
        loan.setLoanStatus(LoanStatus.PENDING);
        Account account = new Account();
        Branch branch = testBranch;
        account.setBranch(branch);
        loan.setAccount(account);
        Employee employee = testEmployee;
        employee.setBranch(branch);
        employee.setLoanList(new ArrayList<>());
        LoanRequestDTO req = new LoanRequestDTO();
        req.setLoanId(loan.getLoanId());
        req.setEmployeeId(employee.getEmployeeId());
        LoanDTO loanDTO = new LoanDTO();
        when(loanService.findLoanByLoanId(loan.getLoanId())).thenReturn(loan);
        when(employeeRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        when(modelMapper.map(any(Loan.class), eq(LoanDTO.class))).thenReturn(loanDTO);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        LoanDTO result = employeeServiceImpl.rejectLoan(req);
        assertNotNull(result);
        assertEquals(LoanStatus.REJECT, loan.getLoanStatus());
    }

    @Test
    void testRejectLoan_differentBranch() {
        Loan loan = new Loan();
        loan.setLoanId(UUID.randomUUID());
        loan.setLoanStatus(LoanStatus.PENDING);
        Account account = new Account();
        Branch branch1 = new Branch();
        branch1.setBranchId(134234L);
        Branch branch2 = testBranch;
        account.setBranch(branch1);
        loan.setAccount(account);
        Employee employee = testEmployee;
        employee.setBranch(branch2);
        LoanRequestDTO req = new LoanRequestDTO();
        req.setLoanId(loan.getLoanId());
        req.setEmployeeId(employee.getEmployeeId());
        when(loanService.findLoanByLoanId(loan.getLoanId())).thenReturn(loan);
        when(employeeRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        assertThrows(IllegalArgumentException.class, () -> employeeServiceImpl.rejectLoan(req));
    }
} 