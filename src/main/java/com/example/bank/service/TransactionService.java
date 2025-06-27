package com.example.bank.service;

import com.example.bank.dto.TransactionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TransactionService {

    TransactionDTO  getTransactionById(UUID transactionId);

    Page<TransactionDTO> findTransactionByAccountNumber(Long accountNumber , Pageable pageable);

}
