package com.example.bank.controller;

import com.example.bank.dto.LoanApplicationDTO;
import com.example.bank.dto.LoanApplyDTO;
import com.example.bank.dto.LoanDTO;
import com.example.bank.dto.LoanInstallmentDTO;
import com.example.bank.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/bank-management-system/loan")
public class LoanController {

    private final LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PostMapping("/apply-loan")
    public ResponseEntity<LoanDTO> applyForLoan(@RequestBody LoanApplicationDTO loanApplicationDTO) {
       return ResponseEntity.status(HttpStatus.OK).body(loanService.applyForLoan(loanApplicationDTO));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PutMapping("/make-loan-payment")
    public ResponseEntity<LoanInstallmentDTO> makeLoanPayment(@RequestBody LoanApplyDTO loanApplyDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(loanService.makeLoanPayment(loanApplyDTO));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("/{loanId}")
    public ResponseEntity<LoanDTO> getLoanById(@PathVariable("loanId") UUID loanId) {
        return ResponseEntity.status(HttpStatus.OK).body(loanService.getLoanById(loanId));
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LoanDTO>> getLoanCustomerById(@PathVariable("customerId") UUID customerId) {
        return ResponseEntity.status(HttpStatus.OK).body(loanService.getLoanCustomerById(customerId));

    }

}
