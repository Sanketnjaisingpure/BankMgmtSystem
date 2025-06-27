package com.example.bank.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeDTO {

    private String firstName;

    private String lastName;

    private String email;

    private String branchName;

    private String bankName;

    private AddressDTO addressDTO;
}
