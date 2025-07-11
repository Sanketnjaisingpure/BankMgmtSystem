package com.example.bank.repository;

import com.example.bank.Enum.IdentityProofType;
import com.example.bank.model.Address;
import com.example.bank.model.Customer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @DisplayName("Test for checking if a customer exists by email, phone number, identity proof type, and identity proof ID")
    @Test
    void testExistsByEmailAndPhoneNumberAndIdentityProofTypeAndIdentityProofId() {
        // Given
        Address address = new Address();
        address.setCity("Mumbai");
        address.setState("Maharashtra");
        address.setCountry("India");
        address.setZipCode("400001");
        address.setStreet("123 Main St");

        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("johndoe@gmail.com");
        customer.setPhoneNumber("1234567890");
        customer.setAddress(address);
        customer.setIdentityProofType(IdentityProofType.PASSPORT);
        customer.setIdentityProofId("A123456789");
        customer.setDateOfBirth(LocalDate.parse("2000-06-24"));
        customer.setPassword("john@1234");
        address.setCustomer(customer);

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