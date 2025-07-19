package com.example.bank.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    private String firstName;

    private String middleName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private AddressDTO addressDTO;
}
