package com.example.bank.model;

import com.example.bank.Enum.IdentityProofType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID customerId;

    private String firstName;

    private String middleName;

    private String lastName;

    @Email
    private String email;

    private String phoneNumber;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private IdentityProofType identityProofType;

    private String identityProofId;

    @CreatedDate
    private LocalDateTime createdAt;


    @LastModifiedDate
    private LocalDateTime updatedAt;


    // Customer.java
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "addressId")
    private Address address;


    @OneToMany(mappedBy = "customer",cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JsonManagedReference
    private List<Account> accountList = new ArrayList<>();

    @OneToMany(mappedBy = "customer" ,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private List<Loan> loanList = new ArrayList<>();

    @OneToMany(mappedBy = "customer",cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JsonManagedReference
    private List<Card> cardList = new ArrayList<>();

    /*
    *
    * {
  "firstName": "Sachin",
  "middleName": "Vasudev",
  "lastName": "Kamley",
  "email": "sachin.vasudev.kamley@gmail.com",
  "phoneNumber": "9012873450",
  "dateOfBirth": "2000-07-15",
  "identityProofType": "AADHAAR_CARD",
  "identityProofId": "8912905671",
  "addressDTO": {
    "street": "Yashoda Nagar",
    "city": "Amravati",
    "state": "Maharashtra",
    "zipCode": "442201",
    "country": "INDIA"
  }
}
*
* {
  "firstName": "Amit",
  "middleName": "Kumar",
  "lastName": "Sharma",
  "email": "amit.sharma@gmail.com",
  "phoneNumber": "9876543210",
  "dateOfBirth": "1988-11-05",
  "identityProofType": "VOTER_ID",
  "identityProofId": "XYZ1234567",
  "addressDTO": {
    "street": "Sector 15",
    "city": "Noida",
    "state": "Uttar Pradesh",
    "zipCode": "201301",
    "country": "INDIA"
  }
}
*
*
* {
  "firstName": "Neha",
  "middleName": "Sunil",
  "lastName": "Deshmukh",
  "email": "neha.deshmukh@gmail.com",
  "phoneNumber": "9123456780",
  "dateOfBirth": "1992-09-10",
  "identityProofType": "PASSPORT",
  "identityProofId": "K1234567",
  "addressDTO": {
    "street": "Park Street",
    "city": "Kolkata",
    "state": "West Bengal",
    "zipCode": "700016",
    "country": "INDIA"
  }
}
*
*
* {
  "firstName": "Rahul",
  "middleName": "Suresh",
  "lastName": "Verma",
  "email": "rahul.verma@gmail.com",
  "phoneNumber": "9001234567",
  "dateOfBirth": "1990-01-28",
  "identityProofType": "DRIVING_LICENCE",
  "identityProofId": "MH1212345678901",
  "addressDTO": {
    "street": "MG Road",
    "city": "Bengaluru",
    "state": "Karnataka",
    "zipCode": "560001",
    "country": "INDIA"
  }
}
*
* */
}
