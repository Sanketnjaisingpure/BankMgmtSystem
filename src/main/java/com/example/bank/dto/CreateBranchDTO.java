package com.example.bank.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateBranchDTO {

    @NotNull
    @Size(message = "size should be between 2 to 50" , min = 2 , max = 50)
    private String branchName;

    @NotNull
    private UUID bankId;

    @NotNull
    @Valid
    private AddressDTO addressDTO;

}
/*
* {
  "branchName": "Main Branch",
  "bankId": "123e4567-e89b-12d3-a456-426614174000",
  "addressDTO": {
    "street": "MG Road",
    "city": "Bengaluru",
    "state": "Karnataka",
    "zipCode": "560001",
    "country": "India"
  }
}*/
