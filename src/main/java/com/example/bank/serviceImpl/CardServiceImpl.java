package com.example.bank.serviceImpl;

import com.example.bank.Enum.CardStatus;
import com.example.bank.dto.CardDTO;
import com.example.bank.dto.CreateCardDTO;
import com.example.bank.exception.ResourceNotFoundException;
import com.example.bank.model.Account;
import com.example.bank.model.Card;
import com.example.bank.model.Customer;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.CardRepository;
import com.example.bank.repository.CustomerRepository;
import com.example.bank.service.AccountService;
import com.example.bank.service.CardService;
import com.example.bank.service.CustomerService;
import com.example.bank.utils.AccountNumberGenerator;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CardServiceImpl implements CardService {

    private final CustomerService customerService;

    private final CardRepository cardRepository;

    private final AccountService accountService;

    private final ModelMapper modelMapper;

    private final AccountNumberGenerator accountNumberGenerator;

    @Autowired
    public CardServiceImpl(CustomerService customerService, CardRepository cardRepository,AccountService accountService,
                                ModelMapper modelMapper, AccountNumberGenerator accountNumberGenerator){
        this.accountService = accountService;
        this.accountNumberGenerator = accountNumberGenerator;
        this.cardRepository = cardRepository;
        this.customerService =customerService;
        this.modelMapper = modelMapper;
    }

    private static final Logger log = LoggerFactory.getLogger(CardServiceImpl.class);


    @Transactional
    @Override
    public CardDTO issueNewCard(CreateCardDTO createCardDTO) {
        log.info("Create Card initiated");

        Card card = new Card();
        Long cardNumber = accountNumberGenerator.generateUniqueCardNumber(cardRepository);
        card.setCardNumber(cardNumber);
        card.setCardStatus(CardStatus.ACTIVE);
        card.setCardType(createCardDTO.getCardType());
        log.info("Finding Customer by ID: {}", createCardDTO.getCustomerId());
        Customer customer = customerService.findCustomerById(createCardDTO.getCustomerId());

        long count = customer.getCardList().size();

        if (count >= 5) {
            log.warn("Customer already has maximum number of cards");
            throw new IllegalArgumentException("Customer already has maximum number of cards");
        }

        card.setCustomer(customer);
        Account account = accountService.getAccountByAccountNumber(createCardDTO.getAccountNumber());

        if(!account.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            log.warn("Customer {} don't have account {}", customer.getCustomerId(),account.getAccountNumber());
            throw new IllegalArgumentException("Customer and Account does not match");
        }
        card.setAccount(account);

        int cvv = accountNumberGenerator.generateUniqueCvv(cardRepository);
        card.setCvv(cvv);
        LocalDate expiration = LocalDate.now().plusYears(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        String expirationDate = expiration.format(formatter);
        card.setExpirationDate(expirationDate);
        cardRepository.save(card);
        log.info("Card created successfully");
        return modelMapper.map(card, CardDTO.class);
    }

    @Override
    public CardDTO getCardByNumber(long cardNumber) {
        return modelMapper.map(this.getCardDetailsByCardNumber(cardNumber),CardDTO.class);
    }

    @Override
    public List<CardDTO> getCardListByCustomer(UUID customerId) {
        log.info("Finding list of Card used by one customer");
        Customer customer = customerService.findCustomerById(customerId);
        log.info("Found list of card used by one customer");

        List<Card> cardList = customer.getCardList();
        if (cardList.isEmpty()){
            log.warn("Card not found for this customer");
            throw new ResourceNotFoundException("Card not found");
        }
        log.info("Customer Card list found successfully");
        return cardList.stream().map(card -> modelMapper.map(card,CardDTO.class)).collect(Collectors.toList());
    }

    @Override
    public void blockCard(long cardNumber) {
        log.info("Blocking card initialized");
        Card card = this.getCardDetailsByCardNumber(cardNumber);
        card.setCardStatus(CardStatus.BLOCKED);
        log.info("card block successfully");
        cardRepository.save(card);
    }

    @Override
    public void activateCard(long cardNumber) {
        Card card = this.getCardDetailsByCardNumber(cardNumber);
        card.setCardStatus(CardStatus.ACTIVE);
        log.info("card activated successfully");
        cardRepository.save(card);
    }

    @Override
    public Card getCardDetailsByCardNumber(Long cardNumber) {
        Card card = cardRepository.getCardDetailsByCardNumber(cardNumber);
        log.info("finding Card Details by Card Number {}" , cardNumber);
        if (card==null){
            log.warn("Card not found");
            throw new ResourceNotFoundException("Card not found");
        }log.info("Card found successfully");
        return card;
    }
}
