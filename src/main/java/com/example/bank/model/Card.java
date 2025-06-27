package com.example.bank.model;

import com.example.bank.Enum.CardStatus;
import com.example.bank.Enum.CardType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Entity
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID cardId;

    // we need to encrypt it
    private Long cardNumber;

    @Enumerated(EnumType.STRING)
    private CardType cardType;

    private String expirationDate; // MM/YY

    // encrypted
    private int cvv;

    @Enumerated(EnumType.STRING)
    private CardStatus cardStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    @JsonBackReference
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountId")
    @JsonBackReference
    private Account account;
}
