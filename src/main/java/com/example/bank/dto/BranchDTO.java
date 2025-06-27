package com.example.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BranchDTO {

    private String branchName;

    private String ifscCode;

    private BankSummaryDTO bankSummaryDTO;

    private AddressDTO addressDTO;

}
