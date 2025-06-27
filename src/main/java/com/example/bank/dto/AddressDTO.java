package com.example.bank.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddressDTO {

    @NotNull // increase size
    @Size(message = "size should be between 2 to 50" , min = 2 , max = 50)
    private String street;

    @NotNull // increase size
    @Size(message = "size should be between 2 to 50" , min = 2 , max = 50)
    private String city;

    @NotNull // increase size
    @Size(message = "size should be between 2 to 50" , min = 2 , max = 50)
    private String state;

    @NotNull
    @Size(message = "size should be  6 " , min = 6 , max = 6)
    private String zipCode;

    @NotNull
    @Size(message = "size should be between 2 to 50" , min = 2 , max = 50)
    private String country;

}
