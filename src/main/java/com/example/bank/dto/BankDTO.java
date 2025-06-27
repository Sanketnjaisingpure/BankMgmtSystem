package com.example.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class BankDTO {

    private UUID bankId;

    private String bankName;

    private String headOfficeAddress;

    private List<BranchDTO> branchDTOList = new ArrayList<>();
}
