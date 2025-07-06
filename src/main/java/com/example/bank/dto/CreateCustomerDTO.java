package com.example.bank.dto;

import com.example.bank.Enum.IdentityProofType;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCustomerDTO {


    @NotNull
    @Size(message = "size should be between 2 to 30" , min = 2 , max = 30)
    private String firstName;

    @NotNull
    @Size(message = "size should be between 2 to 30" , min = 2 , max = 30)
    private String middleName;

    @NotNull
    @Size(message = "size should be between 2 to 30" , min = 2 , max = 30)
    private String lastName;

    @Column(unique = true)
    @NotNull
    @Email(message = "email should be valid")
    private String email;

    @NotNull
    private String password;

    @NotNull
    @Size(message = "size should be 10" , min = 10 , max = 10)
    private String phoneNumber;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private IdentityProofType identityProofType;

    @Size(message = "size should be between 5 to 20" , min = 5 , max = 20)
    @NotNull
    private String identityProofId;

    @Valid
    @NotNull
    private AddressDTO addressDTO;
}
