package com.example.bank.serviceImpl;

import com.example.bank.Enum.AccountType;
import com.example.bank.Enum.CardStatus;
import com.example.bank.Enum.CardType;
import com.example.bank.dto.CardDTO;
import com.example.bank.dto.CreateCardDTO;
import com.example.bank.exception.ResourceNotFoundException;
import com.example.bank.model.Account;
import com.example.bank.model.Card;
import com.example.bank.model.Customer;
import com.example.bank.repository.CardRepository;
import com.example.bank.service.AccountService;
import com.example.bank.service.CustomerService;
import com.example.bank.utils.AccountNumberGenerator;
import com.example.bank.utils.MaskedNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private MaskedNumber maskedNumber;

    @Mock
    private CustomerService customerService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AccountService accountService;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @InjectMocks
    private CardServiceImpl cardServiceImpl;

    private Customer testCustomer;
    private Card testCard;
    private Account testAccount;
    private CreateCardDTO testCreateCardDTO;


    @BeforeEach
    void setUp() {
        // Initialize mocks and other setup tasks
        testCustomer = new Customer();
        testCustomer.setCustomerId(UUID.randomUUID());
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("sanket@gmail.com");

        testAccount = new Account();
        testAccount.setAccountType(AccountType.SAVING);
        testAccount.setAccountId(UUID.randomUUID());
        testAccount.setAccountNumber("1234567890");
        testAccount.setCustomer(testCustomer);

        testCard = new Card();
        testCard.setCardNumber(12312334L);
        testCard.setCardStatus(CardStatus.ACTIVE);
        testCard.setCardType(CardType.CREDIT);
        testCard.setCardId(UUID.randomUUID());
        testCard.setExpirationDate("12/25");
        testCard.setCvv(213);
        testCard.setCustomer(testCustomer);
        testCard.setAccount(testAccount);

        testCreateCardDTO = new CreateCardDTO();
        testCreateCardDTO.setCardType(CardType.CREDIT);
        testCreateCardDTO.setCustomerId(testCustomer.getCustomerId());
        testCreateCardDTO.setAccountNumber(testAccount.getAccountNumber());


    }

    @Test
    void getCardDetailsByCardNumber_Success(){
        Card card = testCard;
        card.setCardId(UUID.randomUUID());
        // Mock the repository call
        when(cardRepository.getCardDetailsByCardNumber(card.getCardNumber())).thenReturn(card);
        when(maskedNumber.maskNumber(card.getCardNumber().toString())).thenReturn("**** **** **** 2345");

        Card foundCard = cardServiceImpl.getCardDetailsByCardNumber(card.getCardNumber());
        assertNotNull(foundCard);
        assertEquals(card.getCardNumber(), foundCard.getCardNumber());
    }

    @Test
    void getCardDetailsByCardNumber_NotFound() {
        long cardNumber = 1234567890123456L;
        when(cardRepository.getCardDetailsByCardNumber(cardNumber)).thenReturn(null);
        when(maskedNumber.maskNumber(String.valueOf(cardNumber))).thenReturn("**** **** **** 3456");
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, ()->cardServiceImpl.getCardDetailsByCardNumber(cardNumber));

        assertEquals("Card not found", ex.getMessage());
    }

    @Test
    void getCardByNumber_Success() {
        long cardNumber = testCard.getCardNumber();
        Card card = testCard;

        CardDTO testCardDTO = new CardDTO();
        testCardDTO.setCardNumber(testCard.getCardNumber());

        card.setCardNumber(cardNumber);
        when(cardRepository.getCardDetailsByCardNumber(testCard.getCardNumber())).thenReturn(card);
        when(maskedNumber.maskNumber(String.valueOf(cardNumber))).thenReturn("**** **** **** 2345");
        when(modelMapper.map(card, CardDTO.class)).thenReturn(testCardDTO);

        CardDTO foundCard = cardServiceImpl.getCardByNumber(cardNumber);
        assertNotNull(foundCard);
        assertEquals(testCard.getCardNumber(), foundCard.getCardNumber());
    }

    @Test
    void activateCard_Success() {
        // Given
        Card card = testCard;
        card.setCardStatus(CardStatus.INACTIVE);

        when(maskedNumber.maskNumber(String.valueOf(card.getCardNumber()))).thenReturn("**** **** **** 2345");
        when(cardRepository.getCardDetailsByCardNumber(card.getCardNumber())).thenReturn(card);
        when(cardRepository.save(any(Card.class))).thenReturn(card);  // Simulate save

        // When
        cardServiceImpl.activateCard(card.getCardNumber());

        // Then
        assertEquals(CardStatus.ACTIVE, card.getCardStatus());
        verify(cardRepository).save(card); // Optional: verify save was called
    }

    @Test
    void blockedCard_Success(){
        Card card = testCard;
        card.setCardStatus(CardStatus.ACTIVE);

        when(maskedNumber.maskNumber(String.valueOf(card.getCardNumber()))).thenReturn("**** **** **** 2345");
        when(cardRepository.getCardDetailsByCardNumber(card.getCardNumber())).thenReturn(card);
        when(cardRepository.save(any(Card.class))).thenReturn(card);  // Simulate save

        // when
        cardServiceImpl.blockCard(card.getCardNumber());
        // then
        assertEquals(CardStatus.BLOCKED, card.getCardStatus());
        verify(cardRepository).save(card); // Optional: verify save was called
    }

    @Test
    void getCardListByCustomer_Success() {
       Customer customer = testCustomer;
       Card card1 = testCard;

       CardDTO cardDTO1 = new CardDTO();
         cardDTO1.setCardNumber(card1.getCardNumber());
         cardDTO1.setCardType(card1.getCardType());
         cardDTO1.setCardStatus(card1.getCardStatus());

       Card card2 = testCard;
       CardDTO cardDTO2 = new CardDTO();
         cardDTO2.setCardNumber(card2.getCardNumber());
         cardDTO2.setCardType(card2.getCardType());
         cardDTO2.setCardStatus(card2.getCardStatus());

       customer.setCardList(List.of(card1,card2));

       when(customerService.findCustomerById(customer.getCustomerId())).thenReturn(customer);
       when(maskedNumber.maskNumber(customer.getCustomerId().toString())).thenReturn("**** **** **** 1234");
       when(modelMapper.map(card1, CardDTO.class)).thenReturn(cardDTO1);
       when(modelMapper.map(card2, CardDTO.class)).thenReturn(cardDTO2);

       List<CardDTO> cardDTOList = cardServiceImpl.getCardListByCustomer(customer.getCustomerId());

       assertNotNull(cardDTOList);

         assertEquals(2, cardDTOList.size());
         assertEquals(card1.getCardNumber(), cardDTOList.get(0).getCardNumber());
    }

    @Test
    void getCardListByCustomer_CardNotFound() {

        Customer customer = testCustomer;

        customer.setCardList(List.of());

        when(customerService.findCustomerById(customer.getCustomerId())).thenReturn(customer);
        when(maskedNumber.maskNumber(customer.getCustomerId().toString())).thenReturn("**** **** **** 1234");

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                cardServiceImpl.getCardListByCustomer(customer.getCustomerId()));

        assertEquals("Card not found", ex.getMessage());

    }

    @Test
    void issueNewCard_Success(){

        Card card = testCard;
        card.setCardStatus(testCard.getCardStatus());
        card.setCardType(testCard.getCardType());
        card.setCardNumber(testCard.getCardNumber());
        card.setCvv(testCard.getCvv());
        card.setExpirationDate(testCard.getExpirationDate());
        card.setCardId(UUID.randomUUID());
        card.setCustomer(testCustomer);
        card.setAccount(testAccount);


        CardDTO cardDTO = new CardDTO();
        cardDTO.setCardNumber(testCard.getCardNumber());
        cardDTO.setCardType(testCard.getCardType());
        cardDTO.setCardStatus(testCard.getCardStatus());


        when(accountNumberGenerator.generateUniqueCardNumber(cardRepository)).thenReturn(1312312312L);
        when(maskedNumber.maskNumber(anyString())).thenReturn("****12312");
        when(customerService.findCustomerById(testCreateCardDTO.getCustomerId())).thenReturn(testCustomer);
        when(accountService.getAccountByAccountNumber(testCreateCardDTO.getAccountNumber())).thenReturn(testAccount);
        when(accountNumberGenerator.generateUniqueCvv(cardRepository)).thenReturn(123);
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(modelMapper.map(any(Card.class), eq(CardDTO.class))).thenReturn(cardDTO);


        CardDTO issuedCard = cardServiceImpl.issueNewCard(testCreateCardDTO);
        assertNotNull(issuedCard);
        assertEquals(card.getCardNumber(), issuedCard.getCardNumber());
        assertEquals(card.getCardType(), issuedCard.getCardType());
        assertEquals(card.getCardStatus(), issuedCard.getCardStatus());
    }

    @Test
    void issueNewCard_CustomerHasMaxCards() {
        testCustomer.setCardList(List.of(testCard, testCard, testCard, testCard, testCard)); // 5 cards already

        when(customerService.findCustomerById(testCustomer.getCustomerId())).thenReturn(testCustomer);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                cardServiceImpl.issueNewCard(testCreateCardDTO));

        assertEquals("Customer already has maximum number of cards", ex.getMessage());
    }

    @Test
    void issueNewCard_CustomerAndAccountMismatch() {
        Customer customer = new Customer();
        customer.setCustomerId(UUID.randomUUID()); // ID A

        Customer differentCustomer = new Customer();
        differentCustomer.setCustomerId(UUID.randomUUID()); // ID B

        Account account = new Account();
        account.setAccountNumber("1234567890");
        account.setCustomer(differentCustomer); // mismatch here

        when(customerService.findCustomerById(any(UUID.class))).thenReturn(customer);
        when(accountService.getAccountByAccountNumber(anyString())).thenReturn(account);
        when(accountNumberGenerator.generateUniqueCardNumber(any())).thenReturn(1234567890123456L);
        when(maskedNumber.maskNumber(anyString())).thenReturn("*****1234");

        assertThrows(IllegalArgumentException.class, () -> {
            cardServiceImpl.issueNewCard(testCreateCardDTO);
        });

    }
}