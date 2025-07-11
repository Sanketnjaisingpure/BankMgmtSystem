package com.example.bank.service;

import com.example.bank.dto.CustomerDTO;
import com.example.bank.exception.ResourceNotFoundException;
import com.example.bank.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Test
    void serviceLoadsAndReturnsEmptyList() {
        // Call any real method; adjust to match your API
        assertThrows(ResourceNotFoundException.class, () -> {
            customerService.findCustomerById(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        });
    }
}
