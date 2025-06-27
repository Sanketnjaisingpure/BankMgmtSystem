package com.example.bank.service;

import com.example.bank.dto.CardDTO;
import com.example.bank.dto.CreateCardDTO;
import com.example.bank.model.Card;

import java.util.List;
import java.util.UUID;

public interface CardService {

    CardDTO issueNewCard(CreateCardDTO createCardDTO);

    CardDTO getCardByNumber(long cardNumber);

    List<CardDTO> getCardListByCustomer(UUID customerId);

    void blockCard(long CardNumber);

    void activateCard(long CardNumber);

    Card getCardDetailsByCardNumber(Long cardNumber);
}
