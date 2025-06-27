package com.example.bank.model;

import com.example.bank.Enum.LoanStatus;
import com.example.bank.Enum.LoanType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID loanId;

    @Enumerated(EnumType.STRING)
    private LoanType loanType;

    private BigDecimal totalAmount;

    private BigDecimal amountPaid;

    private double interestRate;

    private int termInMonth;

    @Enumerated(EnumType.STRING)
   private LoanStatus loanStatus;


   private LocalDate startDate = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "accountId")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId")
    private Employee employee;

}
