package com.example.bank.dto;

import com.example.bank.Enum.CardStatus;
import com.example.bank.Enum.CardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CardDTO {

    private UUID customerId;
    private String accountNumber;
    private CardType cardType;
    private long cardNumber;
    private int cvv;
    private String expirationDate;
    private CardStatus cardStatus;

}
