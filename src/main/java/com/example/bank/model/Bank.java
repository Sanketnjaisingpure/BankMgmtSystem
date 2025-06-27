package com.example.bank.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID bankId;

    private String bankName;

    private String headOfficeAddress;

    @OneToMany(cascade = CascadeType.ALL ,mappedBy = "bank")
    @JsonManagedReference
    private List<Branch> branch = new ArrayList<>();


/*    CREATE EXTENSION IF NOT EXISTS "pgcrypto";

    ALTER TABLE bank
    ALTER COLUMN bank_id SET DEFAULT gen_random_uuid();

    INSERT INTO bank (bank_name, head_office_address)
VALUES
  ('Axis Bank', 'Mumbai, Maharashtra'),
  ('Punjab National Bank', 'New Delhi, Delhi'),
  ('Bank of Baroda', 'Vadodara, Gujarat'),
  ('Kotak Mahindra Bank', 'Mumbai, Maharashtra'),
  ('Yes Bank', 'Mumbai, Maharashtra'),
  ('IndusInd Bank', 'Mumbai, Maharashtra'),
  ('Canara Bank', 'Bengaluru, Karnataka');*/



}
