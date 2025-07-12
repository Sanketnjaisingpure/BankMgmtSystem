package com.example.bank.repository;

import com.example.bank.Enum.IdentityProofType;
import com.example.bank.model.Customer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest {



    private final CustomerRepository customerRepository;


    @Autowired
    public CustomerRepositoryTest(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @DisplayName("Test for checking if a customer exists by email, phone number, identity proof type, and identity proof ID")
    @Test
    void testExistsByEmailAndPhoneNumberAndIdentityProofTypeAndIdentityProofId() {



        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("johndoe@gmail.com");
        customer.setPhoneNumber("1234567890");
        customer.setAddress(null);
        customer.setIdentityProofType(IdentityProofType.PASSPORT);
        customer.setIdentityProofId("A123456789");
        customer.setDateOfBirth(LocalDate.parse("2000-06-24"));
        customer.setPassword("john@1234");

        customerRepository.save(customer);

        // When


        // Then
        boolean exists = customerRepository.existsByEmailAndPhoneNumberAndIdentityProofTypeAndIdentityProofId(
                "johndoe@gmail.com", "1234567890", IdentityProofType.PASSPORT, "A123456789");

        assertTrue(exists, "Customer should exist with the given email, phone number, identity proof type, and ID");

        boolean notExists = customerRepository.existsByEmailAndPhoneNumberAndIdentityProofTypeAndIdentityProofId(
                "janedoe@gmail.com", "0987654321", IdentityProofType.AADHAAR_CARD, "B987654321");

        assertFalse(notExists, "Customer should not exist with different details");
    }

    @Test
    @DisplayName("Test repository is not null")
    void testRepositoryIsNotNull() {
        assertNotNull(customerRepository);
    }
}