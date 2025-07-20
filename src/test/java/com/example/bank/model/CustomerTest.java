package com.example.bank.model;

import com.example.bank.Enum.IdentityProofType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void testCustomerCreation() {

        Customer customer = new Customer();
        UUID customerId = UUID.randomUUID();
        customer.setCustomerId(customerId);
        customer.setFirstName("John");
        customer.setMiddleName("M");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPassword("password123");
        customer.setPhoneNumber("1234567890");
        customer.setDateOfBirth(LocalDate.of(1990, 1, 1));
        customer.setIdentityProofType(IdentityProofType.AADHAAR_CARD);
        customer.setIdentityProofId("123456789012");


        assertEquals(customerId, customer.getCustomerId());
        assertEquals("John", customer.getFirstName());
        assertEquals("M", customer.getMiddleName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("john.doe@example.com", customer.getEmail());
        assertEquals("password123", customer.getPassword());
        assertEquals("1234567890", customer.getPhoneNumber());
        assertEquals(LocalDate.of(1990, 1, 1), customer.getDateOfBirth());
        assertEquals(IdentityProofType.AADHAAR_CARD, customer.getIdentityProofType());
        assertEquals("123456789012", customer.getIdentityProofId());

    }

    @Test
    void testDefaultListInitialization() {
        Customer customer = new Customer();

        assertNotNull(customer.getAccountList()," Account list should be initialized");
        assertNotNull(customer.getLoanList()," Loan list should be initialized");
        assertNotNull(customer.getCardList()," Card list should be initialized");
    }

    @Test
    void testAddressAssignment(){
        Customer customer = new Customer();
        Address address = new Address();
        address.setStreet("123 Main St");
        address.setCity("Springfield");
        address.setState("IL");
        address.setZipCode("62701");

        customer.setAddress(address);

        assertEquals("123 Main St", customer.getAddress().getStreet());
        assertEquals("Springfield", customer.getAddress().getCity());
        assertEquals("IL", customer.getAddress().getState());
        assertEquals("62701", customer.getAddress().getZipCode());
    }
}