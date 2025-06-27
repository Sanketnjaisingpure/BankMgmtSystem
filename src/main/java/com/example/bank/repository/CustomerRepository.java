package com.example.bank.repository;

import com.example.bank.Enum.IdentityProofType;
import com.example.bank.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer , UUID> {

    @Query("select (count(c) > 0) from Customer c where c.email = :email and c.phoneNumber = :phoneNumber" +
            " and c.identityProofType = :identityId  or c.identityProofId = :identityProofId")
    boolean existsByEmailAndPhoneNumberAndIdentityProofType(@Param("email") String email,@Param("phoneNumber")  String phoneNumber
            , @Param("identityId") IdentityProofType identityProofType , @Param("identityProofId") String identityProofId);


}
