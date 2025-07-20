package com.example.bank.model;

import com.example.bank.Enum.CardStatus;
import com.example.bank.Enum.CardType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void testCardCreation() {

        Customer customer = new Customer();

        Account account = new Account();
        Card card = new Card();
        card.setCardId(UUID.randomUUID());
        card.setCardNumber(1231231234567890L);
        card.setCardType(CardType.DEBIT);
        card.setCvv(123);
        card.setCardStatus(CardStatus.ACTIVE);
        card.setExpirationDate("12/25");
        card.setCustomer(customer);
        card.setAccount(account);

        assertNotNull(card.getCardId(), "Card ID should not be null");
        assertEquals(1231231234567890L, card.getCardNumber(), "Card number should match");
        assertEquals(CardType.DEBIT, card.getCardType(), "Card type should be DEBIT");
        assertEquals(123, card.getCvv(), "CVV should match");
        assertEquals(CardStatus.ACTIVE, card.getCardStatus(), "Card status should be ACTIVE");
        assertEquals("12/25", card.getExpirationDate(), "Expiration date should match");
        assertNotNull(card.getCustomer(), "Customer should not be null");
        assertNotNull(card.getAccount(), "Account should not be null");

    }
}