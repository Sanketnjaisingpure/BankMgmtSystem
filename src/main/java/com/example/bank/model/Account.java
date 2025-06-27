package com.example.bank.model;

import com.example.bank.Enum.AccountStatus;
import com.example.bank.Enum.AccountType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID accountId;

    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    private LocalDate openDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branchId")
    private Branch branch;

    @OneToMany(mappedBy = "account" , cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Transaction> transactionList = new ArrayList<>();


    @OneToMany(mappedBy = "account" , cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Card> cardList = new ArrayList<>();

    @OneToMany(mappedBy = "account" , cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Loan> loanList = new ArrayList<>();

}
