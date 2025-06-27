package com.example.bank.repository;

import com.example.bank.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card , UUID> {

    @Query("select c from Card c  where c.cardNumber = :cardNumber")
    Card getCardDetailsByCardNumber(@Param("cardNumber") long  cardNumber );


    @Query("select count(c) > 0 from Card c where c.cvv = :cvv")
    boolean existsByCvv(@Param("cvv") int cvv);

    @Query("select count(c) > 0 from Card c where c.cardNumber = :cardNumber")
    boolean existsByCardNumber(@Param("cardNumber") long cardNumber);
}
