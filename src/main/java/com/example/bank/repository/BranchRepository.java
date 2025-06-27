package com.example.bank.repository;

import com.example.bank.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    @Query("select e from Branch e where e.bank.bankId = :bankId")
    List<Branch> findByBank_BankId(@Param("bankId") UUID bankId);


}
