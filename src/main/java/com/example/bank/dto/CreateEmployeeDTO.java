package com.example.bank.dto;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateEmployeeDTO {

    @NotNull
    @Size(message = "size should be between 2 to 30" , min = 2 , max = 30)
    private String firstName;

    @NotNull
    @Size(message = "size should be between 2 to 30" , min = 2 , max = 30)
    private String lastName;

    @Column(unique = true)
    @Email
    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private Long branchId;


    @Valid
    @NotNull
    private AddressDTO addressDTO;

}
