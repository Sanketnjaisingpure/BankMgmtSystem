package com.example.bank.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long branchId;

    private String branchName;

    private String ifscCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bankId")
    @JsonBackReference
    private Bank bank;

    // Branch.java
    @OneToOne(mappedBy = "branch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Address address;

    @OneToMany(mappedBy = "branch" , cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Employee> employeeList = new ArrayList<>();

    @OneToMany(mappedBy = "branch" , cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Account> accountList = new ArrayList<>();

/*
* {
  "branchName": "Civil Lines Branch",
  "bankId": "10101010-1010-1010-1010-101010101010",
  "addressDTO": {
    "street": "Civil Lines",
    "city": "Jaipur",
    "state": "Rajasthan",
    "zipCode": "302006",
    "country": "India"
  }
}
*
* {
  "branchName": "Salt Lake Branch",
  "bankId": "99999999-9999-9999-9999-999999999999",
  "addressDTO": {
    "street": "Salt Lake City",
    "city": "Kolkata",
    "state": "West Bengal",
    "zipCode": "700091",
    "country": "India"
  }
}
*
* {
  "branchName": "Jaya nagar Branch",
  "bankId": "88888888-8888-8888-8888-888888888888",
  "addressDTO": {
    "street": "4th Block",
    "city": "Bengaluru",
    "state": "Karnataka",
    "zipCode": "560011",
    "country": "India"
  }
}
*
*
* {
  "branchName": "Sector 17 Branch",
  "bankId": "77777777-7777-7777-7777-777777777777",
  "addressDTO": {
    "street": "Sector 17 Market",
    "city": "Chandigarh",
    "state": "Chandigarh",
    "zipCode": "160017",
    "country": "India"
  }
}
*
* {
  "branchName": "Ram Nagar Branch",
  "bankId": "66666666-6666-6666-6666-666666666666",
  "addressDTO": {
    "street": "Marine Drive",
    "city": "Mumbai",
    "state": "Maharashtra",
    "zipCode": "400020",
    "country": "India"
  }
}
* */

}
