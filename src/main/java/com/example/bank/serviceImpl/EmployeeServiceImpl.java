package com.example.bank.serviceImpl;

import com.example.bank.Enum.LoanStatus;
import com.example.bank.dto.*;
import com.example.bank.exception.ResourceNotFoundException;
import com.example.bank.model.*;
import com.example.bank.repository.*;
import com.example.bank.service.BranchService;
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

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {


    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper ;
    private final LoanService loanService;
    private final UserDetailsRepo userDetailsRepo;
    private final BranchService branchService;
    private final LoanRepository loanRepository;
    private final MaskedNumber maskedNumber;
    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    public EmployeeServiceImpl(LoanRepository loanRepository , EmployeeRepository employeeRepository,
                                @Lazy BranchService branchService,
                                UserDetailsRepo userDetailsRepo,
                                MaskedNumber maskedNumber,
                                @Lazy LoanService loanService, ModelMapper modelMapper){
        this.employeeRepository =employeeRepository;
        this.modelMapper = modelMapper;
        this.maskedNumber = maskedNumber;
        this.userDetailsRepo = userDetailsRepo;
        this.loanRepository = loanRepository;
        this.branchService = branchService;
        this.loanService = loanService;
    }

    @Override
    public Employee getEmployeeById(Long employeeId) {
        log.info("Fetching employee with id: {}", maskedNumber.maskNumber(employeeId.toString()));
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        log.info("Employee found: {} successfully", maskedNumber.maskNumber(employeeId.toString()));
        return employee;
    }


    @Override
    public EmployeeDTO addEmployee(CreateEmployeeDTO createEmployeeDTO) {
        log.info("Adding employee: {}", createEmployeeDTO);
        Employee employee = new Employee();
        employee.setFirstName(createEmployeeDTO.getFirstName());
        employee.setLastName(createEmployeeDTO.getLastName());
        employee.setEmail(createEmployeeDTO.getEmail());
        Branch branch = branchService.findBranchByBranchId(createEmployeeDTO.getBranchId());
        employee.setBranch(branch);
        employee.setAddress(modelMapper.map(createEmployeeDTO.getAddressDTO(), Address.class));
        employeeRepository.save(employee);

        Users users = new Users();

        if (userDetailsRepo.findByUsername(createEmployeeDTO.getEmail()) != null) {
            // If user with the same email already exists, log a warning and throw an exception
            log.warn("User with email {} already exists", createEmployeeDTO.getEmail());
            throw new IllegalArgumentException("User with this email already exists");
        }

        users.setUsername(createEmployeeDTO.getEmail());
        users.setPassword(createEmployeeDTO.getPassword());
        users.setRole("ROLE_EMPLOYEE");
        users.setLinkedEntityId(employee.getEmployeeId().toString());
        users.setEntityType("EMPLOYEE");
        userDetailsRepo.save(users);

        EmployeeDTO employeeDTO = modelMapper.map(employee, EmployeeDTO.class);
        employeeDTO.setBranchName(branch.getBranchName());
        employeeDTO.setBankName(branch.getBank().getBankName());
        AddressDTO addressDTO = modelMapper.map(employee.getAddress(), AddressDTO.class);
        employeeDTO.setAddressDTO(addressDTO);
        log.info("Employee added successfully with id: {}", maskedNumber.maskNumber(employee.getEmployeeId().toString()));
        return employeeDTO;

    }

    @Override
    public List<LoanDTO> getAllLoansByStatus(LoanStatus loanStatus) {
        log.info("Fetching all loans with status: {}", loanStatus);
        List<Loan> loans = loanRepository.findAllLoanByLoanStatus(loanStatus);
        if (loans.isEmpty()) {
            log.warn("No loans found with status: {}", loanStatus);
            throw new ResourceNotFoundException("No loans found with status: " + loanStatus);
        }
        return loans.stream()
                .map(loan -> modelMapper.map(loan, LoanDTO.class))
                .toList();
    }

    @Override
    public List<EmployeeDTO> getEmployeeByBranch(long branchId) {
        log.info("Getting ALL Employee by Branch Id: {} ", branchId);

        Branch branch = branchService.findBranchByBranchId(branchId);
        List<Employee> employeeList = branch.getEmployeeList();

        if (employeeList.isEmpty()){
            log.warn("No employees found for branch with id: {}", branchId);
            throw new ResourceNotFoundException("No employees found for branch with id: " + branchId);
        }

        return employeeList.stream()
                .map(employee -> modelMapper.map(employee, EmployeeDTO.class))
                .toList();
    }


    @Transactional
    @Override
    public LoanDTO approveLoan(LoanRequestDTO loanRequestDTO) {

        log.info("Approving loan with id: {}",  maskedNumber.maskNumber(loanRequestDTO.getLoanId().toString()));
        Loan loan = loanService.findLoanByLoanId(loanRequestDTO.getLoanId());

        if(loan.getLoanStatus().equals(LoanStatus.REJECT)){
            log.warn("Loan with id {} has already been rejected", maskedNumber.maskNumber(loan.getLoanId().toString()));
            throw new IllegalArgumentException("Loan has already been rejected");
        }

        Employee employee = this.getEmployeeById(loanRequestDTO.getEmployeeId());

        boolean isSameBranch =  loan.getAccount().getBranch().getBranchId().equals(employee.getBranch().getBranchId());

      if (!isSameBranch){
            log.warn("Employee and customer are not in same branch");
            throw new IllegalArgumentException("Employee and loan customer are not in same branch");
        }

        log.info("Loan status: {}", loan.getLoanStatus());
        loan.setLoanStatus(LoanStatus.APPROVED);
        loan.setEmployee(employee);
        employee.getLoanList().add(loan);
        employeeRepository.save(employee);
        loanRepository.save(loan);
        log.info("Loan approved successfully");
        return modelMapper.map(loan,LoanDTO.class);
    }

    @Transactional
    @Override
    public LoanDTO rejectLoan(LoanRequestDTO loanRequestDTO) {

        log.info("Rejecting loan with id: {}", maskedNumber.maskNumber(loanRequestDTO.getLoanId().toString()));
        Loan loan = loanService.findLoanByLoanId(loanRequestDTO.getLoanId());

        Employee employee = this.getEmployeeById(loanRequestDTO.getEmployeeId());
        boolean isSameBranch =  loan.getAccount().getBranch().getBranchId().equals(employee.getBranch().getBranchId());
        if (!isSameBranch){
            log.warn("Employee and customer are not in same branch ");
            throw new IllegalArgumentException("Employee and loan customer are not in same branch");
        }
        loan.setLoanStatus(LoanStatus.REJECT);
        loan.setEmployee(employee);
        employee.getLoanList().add(loan);
        employeeRepository.save(employee);
        loanRepository.save(loan);
        log.info("Loan rejected successfully");
        return modelMapper.map(loan,LoanDTO.class);
    }
}
