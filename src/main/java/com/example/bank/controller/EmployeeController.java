package com.example.bank.controller;

import com.example.bank.Enum.LoanStatus;
import com.example.bank.dto.CreateEmployeeDTO;
import com.example.bank.dto.EmployeeDTO;
import com.example.bank.dto.LoanDTO;
import com.example.bank.dto.LoanRequestDTO;
import com.example.bank.model.Loan;
import com.example.bank.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bank-management-system/employee")
@PreAuthorize("hasRole('ROLE_EMPLOYEE')")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @PutMapping("/approve-loan")
    public ResponseEntity<LoanDTO> approveLoan(@RequestBody LoanRequestDTO loanRequestDTO) {
        return ResponseEntity.ok(employeeService.approveLoan(loanRequestDTO));
    }

    @GetMapping("/get-loans-by-status/{loanStatus}")
    public ResponseEntity<List<LoanDTO>> getAllLoansByStatus(@PathVariable("loanStatus") LoanStatus loanStatus) {
        return ResponseEntity.ok(employeeService.getAllLoansByStatus(loanStatus));
    }

    @PutMapping("/reject-loan")
    public ResponseEntity<LoanDTO> rejectLoan(@RequestBody LoanRequestDTO loanRequestDTO) {
        return ResponseEntity.ok(employeeService.rejectLoan(loanRequestDTO));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add-employee")
    public ResponseEntity<EmployeeDTO> addEmployee(@Valid @RequestBody CreateEmployeeDTO createEmployeeDTO) {
        return ResponseEntity.ok(employeeService.addEmployee(createEmployeeDTO));
    }

    @GetMapping("/get-employee-by-branch/{branchId}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeeByBranch(@PathVariable long branchId) {
        return ResponseEntity.ok(employeeService.getEmployeeByBranch(branchId));
    }

}

