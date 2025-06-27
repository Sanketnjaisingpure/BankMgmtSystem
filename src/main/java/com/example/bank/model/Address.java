package com.example.bank.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long addressId;

    private String street;

    private String city;

    private String state;

    private String zipCode;

    private String country;

    // Address.java
    @OneToOne(mappedBy = "address", fetch = FetchType.LAZY)
    private Customer customer;

    @OneToOne(mappedBy = "address", fetch = FetchType.LAZY)
    private Employee employee;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branchId")
    @JsonBackReference
    private Branch branch;

}
