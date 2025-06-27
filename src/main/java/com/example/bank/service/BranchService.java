package com.example.bank.service;

import com.example.bank.dto.BranchDTO;
import com.example.bank.dto.CreateBranchDTO;
import com.example.bank.dto.CustomerDTO;
import com.example.bank.model.Branch;

import java.util.List;
import java.util.UUID;

public interface BranchService {

    BranchDTO createBranch(CreateBranchDTO createBranchDTO);

    BranchDTO getBranchDTOById(Long branchId);

    List<BranchDTO> getAllBranchByBank(UUID bankId);

    List<CustomerDTO>  getCustomerByBranch(long branchId);

    Branch findBranchByBranchId(Long branchId);

}
