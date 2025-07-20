package com.example.bank.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    @Test
    void testEmployeeCreation() {
        Employee employee = new Employee();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john@gmail.com");
        employee.setPassword("password123");
        employee.setEmployeeId(1L);
        employee.setBranch(new Branch()); // Assuming branch is not set for this test
        employee.setLoanList(List.of(new Loan())); // Assuming loan list is not set for this test
        employee.setAddress(new Address()); // Assuming address is not set for this test
        assertNotNull(employee.getEmployeeId(), "Employee ID should not be null");
        assertEquals("John", employee.getFirstName(), "First name should match");
        assertEquals("Doe", employee.getLastName(), "Last name should match");
        assertEquals("password123", employee.getPassword(), "Password should match");
        assertEquals(1L, employee.getEmployeeId(), "Employee ID should match");
        assertNotNull(employee.getBranch(), "Branch should not be null");
        assertNotNull(employee.getLoanList(), "Loan list should not be null");
        assertNotNull(employee.getAddress(), "Address should not be null");

    }
        
}