package com.example.bank.service;

import com.example.bank.Enum.LoanStatus;
import com.example.bank.dto.*;
import com.example.bank.model.Employee;
import java.util.List;


public interface EmployeeService {
    // operation for bank employee
    LoanDTO approveLoan(LoanRequestDTO loanRequestDTO);

    LoanDTO rejectLoan(LoanRequestDTO loanRequestDTO);


    Employee getEmployeeById(Long employeeId);

    List<LoanDTO> getAllLoansByStatus(LoanStatus loanStatus);

    EmployeeDTO addEmployee(CreateEmployeeDTO createEmployeeDTO);
    // for customer
    List<EmployeeDTO> getEmployeeByBranch(long branchId);

}