package com.example.bank.controller;

import com.example.bank.dto.CardDTO;
import com.example.bank.dto.CreateCardDTO;
import com.example.bank.service.CardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/bank-management-system/card")
public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PostMapping("/create-card")
    public ResponseEntity<CardDTO> createCard(@Valid  @RequestBody CreateCardDTO createCardDTO) {
        return ResponseEntity.ok(cardService.issueNewCard(createCardDTO));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("/get-card/{cardNumber}")
    public ResponseEntity<CardDTO> getCard(@PathVariable("cardNumber") long cardNumber) {
        return ResponseEntity.ok(cardService.getCardByNumber(cardNumber));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("/get-card-list/{customerId}")
    public ResponseEntity<List<CardDTO>> getCardList(@PathVariable("customerId") UUID customerId) {
        return ResponseEntity.ok(cardService.getCardListByCustomer(customerId));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PutMapping("/block-card/{cardNumber}")
    public ResponseEntity<String> blockCard(@PathVariable("cardNumber") long cardNumber) {
        cardService.blockCard(cardNumber);
        return ResponseEntity.status(HttpStatus.OK).body("Card blocked successfully");
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PutMapping("/activate-card/{cardNumber}")
    public ResponseEntity<String> activateCard(@PathVariable("cardNumber") long cardNumber) {
        cardService.activateCard(cardNumber);
        return ResponseEntity.status(HttpStatus.OK).body("Card activated successfully");
    }
}
