package com.example.bank.serviceImpl;

import com.example.bank.config.UserPrincipal;
import com.example.bank.exception.ResourceNotFoundException;
import com.example.bank.model.Users;
import com.example.bank.repository.UserDetailsRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceImplTest {
    @Mock private UserDetailsRepo userDetailsRepo;
    @InjectMocks private MyUserDetailsServiceImpl myUserDetailsServiceImpl;

    @BeforeEach
    void setUp() { }

    @Test
    void testLoadUserByUsername_success() {
        Users user = new Users();
        user.setUsername("testuser");
        when(userDetailsRepo.findByUsername("testuser")).thenReturn(user);
        UserDetails details = myUserDetailsServiceImpl.loadUserByUsername("testuser");
        assertNotNull(details);
        assertTrue(details instanceof UserPrincipal);
    }

    @Test
    void testLoadUserByUsername_notFound() {
        when(userDetailsRepo.findByUsername("nouser")).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> myUserDetailsServiceImpl.loadUserByUsername("nouser"));
    }

    @Test
    void testUpdatePassword_success() throws Exception {
        Users user = new Users();
        user.setId(1L);
        user.setPassword("old");
        when(userDetailsRepo.findByLinkedEntityId("uuid")).thenReturn(user);
        when(userDetailsRepo.save(any(Users.class))).thenReturn(user);
        Users updated = myUserDetailsServiceImpl.updatePassword("uuid", "newpass");
        assertNotNull(updated);
        assertEquals("newpass", updated.getPassword());
    }

    @Test
    void testUpdatePassword_userNotFound() {
        when(userDetailsRepo.findByLinkedEntityId("baduuid")).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> {
            myUserDetailsServiceImpl.updatePassword("baduuid", "pass");
        });
    }
} 