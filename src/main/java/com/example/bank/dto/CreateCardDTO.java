package com.example.bank.dto;

import com.example.bank.Enum.CardStatus;
import com.example.bank.Enum.CardType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCardDTO {

    @NotNull
    private UUID customerId;

    @NotNull
    private String accountNumber;

    @NotNull
    private CardType cardType;

}
