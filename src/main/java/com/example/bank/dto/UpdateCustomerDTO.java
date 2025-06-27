package com.example.bank.dto;

import com.example.bank.Enum.IdentityProofType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomerDTO {

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(message = "size should be 10" , min = 10 , max = 10)
    private String phoneNumber;

    @NotNull
    private IdentityProofType identityProofType;

    @NotNull
    @Size(message = "size should be  10 " , min = 10 , max = 10)
    private String identityProofId;

    @NotNull
    @Valid
    private AddressDTO addressDTO;
}
