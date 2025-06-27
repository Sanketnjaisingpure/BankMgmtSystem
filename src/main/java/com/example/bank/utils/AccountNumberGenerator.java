package com.example.bank.utils;

import com.example.bank.model.Branch;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.CardRepository;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AccountNumberGenerator {

    private final static Random random = new Random();

    public String generateUniqueAccountNumber(AccountRepository accountRepository) {
        String AccountNumber;
        do {
            AccountNumber = generateRandomDigitNumber(12);
        }while (accountRepository.existsByAccountNumber(AccountNumber));
        return AccountNumber;
    }
    private String generateRandomDigitNumber(int length) {
        StringBuilder sb = new StringBuilder();
        sb.append(random.nextInt(9)+1);
        for (int i = 1; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public int generateUniqueCvv(CardRepository cardRepository){
        int cvv;
        do {
            cvv = Integer.parseInt(generateRandomDigitNumber(3));
        } while (cardRepository.existsByCvv(cvv));
        return cvv;
    }
    public Long generateUniqueCardNumber(CardRepository cardRepository) {
        Long cardNumber;
        do {
            cardNumber = Long.valueOf(generateRandomDigitNumber(16));
        } while (cardRepository.existsByCardNumber(cardNumber));
        return cardNumber;
    }
}
