package com.example.bank.repository;

import com.example.bank.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Query("select e from Account e where e.customer.customerId = :customerId")
    List<Account> FindAllCustomerAccountByCustomerId(@Param("customerId") UUID customerId);

    @Query("select e from Account e where e.accountNumber = :accountNumber")
    Account getAccountByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("select (count(a) > 0) from Account a where a.accountNumber = :accountNumber")
    boolean existsByAccountNumber(@Param("accountNumber") String accountNumber);

}
