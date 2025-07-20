package com.example.bank.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsersTest {

    @Test
    void testUserCreation() {
        Users user = new Users();
        user.setEntityType("Customer");
        user.setLinkedEntityId("12345");
        user.setPassword("password123");
        user.setUsername("testUser");
        user.setId(1L);

        assertNotNull(user.getEntityType(), "Entity type should not be null");
        assertEquals("Customer", user.getEntityType(), "Entity type should match");
        assertEquals("12345", user.getLinkedEntityId(), "Linked entity ID should match");
        assertEquals("password123", user.getPassword(), "Password should match");
        assertEquals("testUser", user.getUsername(), "Username should match");
        assertEquals(1L, user.getId(), "ID should match");

    }

}