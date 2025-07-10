package com.example.bank.repository;

import com.example.bank.Enum.IdentityProofType;
import com.example.bank.model.Address;
import com.example.bank.model.Customer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@SpringBootTest
class CustomerRepositoryTest {


    @Autowired
    private CustomerRepository customerRepository;

    @DisplayName("Test for checking if a customer exists by email, phone number, identity proof type, and identity proof ID")
    @Test
    void existsByEmailAndPhoneNumberAndIdentityProofTypeAndIdentityProofId() {
        // This test should check if the method existsByEmailAndPhoneNumberAndIdentityProofTypeAndIdentityProofId
        // in the CustomerRepository works as expected.
        // You would typically mock the repository and verify the behavior.
        // Example:
        // CustomerRepository customerRepository = mock(CustomerRepository.class);
        // when(customerRepository.existsByEmailAndPhoneNumberAndIdentityProofTypeAndIdentityProofId(...)).thenReturn(true);
        // assertTrue(customerRepository.existsByEmailAndPhoneNumberAndIdentityProofTypeAndIdentityProofId(...));
        // Note: Replace the above with actual test logic as per your application context.

        Address address = new Address();
        address.setAddressId(1L);
        address.setCity("Mumbai");
        address.setState("Maharashtra");
        address.setCountry("India");
        address.setZipCode("400001");
        address.setStreet("123 Main St");
        // Assuming you have a CustomerRepository instance to test against

        Customer customer = new Customer();
        customer.setCustomerId(UUID.fromString("c388cd37-f795-4a04-8fb3-fe5d156873ba"));
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("johndoe@gmail.com");
        customer.setPhoneNumber("1234567890");
        customer.setAddress(address);
        customer.setIdentityProofType(IdentityProofType.PASSPORT);
        customer.setIdentityProofId("A123456789");
        customer.setDateOfBirth(LocalDate.parse("2000-06-24"));
        customer.setPassword("john@1234");
        customerRepository.save(customer);
        boolean exists = customerRepository.existsByEmailAndPhoneNumberAndIdentityProofTypeAndIdentityProofId(
                "johndoe@gmail.com","1234567890", IdentityProofType.PASSPORT, "A123456789");

        assertTrue(exists, "Customer should exist with the given email, phone number, identity proof type, and ID");

        boolean notExists = customerRepository.existsByEmailAndPhoneNumberAndIdentityProofTypeAndIdentityProofId(
                "janedoe@gmail.com", "0987654321", IdentityProofType.AADHAAR_CARD, "B987654321");
        assertFalse(notExists, "Customer should not exist with different details");
    }



}