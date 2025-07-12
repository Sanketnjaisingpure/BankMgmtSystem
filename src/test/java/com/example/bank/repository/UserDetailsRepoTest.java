package com.example.bank.repository;

import com.example.bank.model.Users;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserDetailsRepoTest {


    private  final UserDetailsRepo userDetailsRepo;

    @Autowired
    public UserDetailsRepoTest( UserDetailsRepo userDetailsRepo) {
        this.userDetailsRepo = userDetailsRepo;
    }

    @Test
    void findByUsername() {
        Users user = new Users();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setLinkedEntityId("testLinkedEntityId");
        user.setRole("ROLE_CUSTOMER");
        user.setEntityType("INDIVIDUAL");

        userDetailsRepo.save(user);

        Users foundUser = userDetailsRepo.findByUsername("testUser");

        assertNotNull(foundUser, "User should not be null");
        assertEquals("testUser", foundUser.getUsername(), "Username should match");
        assertEquals("testPassword", foundUser.getPassword(), "Password should match");
        assertNotEquals("testUser1",foundUser.getUsername(),"Username should not match a different value");

    }

    @Test
    void findByLinkedEntityId() {
        Users user = new Users();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setLinkedEntityId("testLinkedEntityId");
        user.setRole("ROLE_CUSTOMER");
        user.setEntityType("INDIVIDUAL");

        userDetailsRepo.save(user);

        Users foundUser = userDetailsRepo.findByLinkedEntityId("testLinkedEntityId");

        assertNotNull(foundUser, "User should not be null");
        assertEquals("testLinkedEntityId", foundUser.getLinkedEntityId(), "Linked Entity ID should match");
        assertEquals("testUser", foundUser.getUsername(), "Username should match");
        assertNotEquals("testLinkedEntityId1", foundUser.getLinkedEntityId(), "Linked Entity ID should not match a different value");
    }
}