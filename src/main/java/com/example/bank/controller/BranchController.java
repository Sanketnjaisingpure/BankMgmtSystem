package com.example.bank.controller;

import com.example.bank.dto.BranchDTO;
import com.example.bank.dto.CreateBranchDTO;
import com.example.bank.dto.CustomerDTO;
import com.example.bank.service.BranchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/bank-management-system/branch")
public class BranchController {

    private final BranchService branchService;

    @Autowired
    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create-branch")
    public ResponseEntity<BranchDTO> createBranch(@Valid @RequestBody CreateBranchDTO createBranchDTO){
        return ResponseEntity.ok(branchService.createBranch(createBranchDTO));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{branchId}")
    public ResponseEntity<BranchDTO> getBranchDTOById(@PathVariable("branchId") Long branchId){
        return ResponseEntity.ok(branchService.getBranchDTOById(branchId));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/bank/{bankId}")
    public ResponseEntity<List<BranchDTO>> getAllBranchByBankId(@PathVariable("bankId") UUID bankId){
        return ResponseEntity.ok(branchService.getAllBranchByBank(bankId));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/customer/{branchId}")
    public ResponseEntity<List<CustomerDTO>> getAllCustomerDTOByBranch(@PathVariable("branchId") long branchId){
        return ResponseEntity.ok(branchService.getCustomerDTOByBranch(branchId));
    }
}
