package com.example.bank.controller;

import com.example.bank.dto.AccountDTO;
import com.example.bank.dto.CreateAccountDTO;
import com.example.bank.dto.TransactionDTO;
import com.example.bank.dto.TransactionRequestDTO;
import com.example.bank.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/bank-management-system/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PostMapping("/create-account")
    public ResponseEntity<AccountDTO> createAccount( @Valid @RequestBody CreateAccountDTO createAccountDTO){
        return ResponseEntity.ok(accountService.createAccount(createAccountDTO));
    }
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable("accountId") UUID accountId){
        return ResponseEntity.ok(accountService.getAccountDTOById(accountId));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PutMapping("/close-account/{accountNumber}")
    public ResponseEntity<String> closeAccount(@PathVariable("accountNumber") String accountNumber){
        accountService.closeAccount(accountNumber);
        return ResponseEntity.ok("Account closed Successfully");
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PutMapping("/activate-account/{accountNumber}")
    public ResponseEntity<String> activateAccount(@PathVariable("accountNumber") String accountNumber){
        accountService.activateAccount(accountNumber);
        return ResponseEntity.ok("Account activated Successfully");
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PutMapping("/deposit")
    public ResponseEntity<TransactionDTO> deposit( @RequestBody TransactionRequestDTO transactionRequestDTO){
        return ResponseEntity.ok(accountService.deposit(transactionRequestDTO));
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PutMapping("/withdraw")
    public ResponseEntity<TransactionDTO> withdraw(@Valid @RequestBody TransactionRequestDTO transactionRequestDTO){
        return ResponseEntity.ok(accountService.withDraw(transactionRequestDTO));
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PutMapping("/transfer")
    public ResponseEntity<TransactionDTO> transfer(@Valid @RequestBody TransactionRequestDTO transactionRequestDTO){
        return ResponseEntity.ok(accountService.transfer(transactionRequestDTO));
    }

}
