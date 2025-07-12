package com.example.bank.repository;

import com.example.bank.Enum.CardStatus;
import com.example.bank.model.Card;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CardRepositoryTest {


    private final CardRepository cardRepository;

    @Autowired
    public CardRepositoryTest(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Test
    void getCardDetailsByCardNumber() {
        Card card = new Card();
        card.setCardNumber(1234567890123456L);
        card.setCvv(123);
        card.setAccount(null);
        card.setCardStatus(CardStatus.ACTIVE);
        card.setCustomer(null);
        cardRepository.save(card);

        Card foundCard = cardRepository.getCardDetailsByCardNumber(1234567890123456L);

        assertNotNull(foundCard, "Card should not be null");
        assertEquals(1234567890123456L,foundCard.getCardNumber(), "Card number should match");
        assertNotEquals(1231231231231234L, foundCard.getCardNumber(), "Card number should not match a different value");

    }

    @Test
    void existsByCvv() {
        Card card = new Card();
        card.setCardNumber(1234567890123456L);
        card.setCvv(123);
        card.setAccount(null);
        card.setCardStatus(CardStatus.ACTIVE);
        card.setCustomer(null);
        cardRepository.save(card);

        boolean exists = cardRepository.existsByCvv(123);
        assertTrue(exists, "Card with CVV 123 should exist");

        boolean notExists = cardRepository.existsByCvv(999);
        assertFalse(notExists, "Card with CVV 999 should not exist");
    }

    @Test
    void existsByCardNumber() {

        Card card = new Card();
        card.setCardNumber(1234567890123456L);
        card.setCvv(123);
        card.setAccount(null);
        card.setCardStatus(CardStatus.ACTIVE);
        card.setCustomer(null);
        cardRepository.save(card);

        boolean exists = cardRepository.existsByCardNumber(1234567890123456L);
        assertTrue(exists, "Card with number 1234567890123456 should exist");

        boolean notExists = cardRepository.existsByCardNumber(9999999999999999L);
        assertFalse(notExists, "Card with number 9999999999999999 should not exist");
    }
}