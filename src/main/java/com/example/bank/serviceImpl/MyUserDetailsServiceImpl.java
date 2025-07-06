package com.example.bank.serviceImpl;

import com.example.bank.config.UserPrincipal;
import com.example.bank.exception.ResourceNotFoundException;
import com.example.bank.model.Users;
import com.example.bank.repository.UserDetailsRepo;
import com.example.bank.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsServiceImpl implements MyUserDetailsService{

    private  final UserDetailsRepo userDetailsRepo;

    @Autowired
    public MyUserDetailsServiceImpl(UserDetailsRepo userDetailsRepo) {
        this.userDetailsRepo = userDetailsRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users user = userDetailsRepo.findByUsername(username);

        if (user==null){
            throw new ResourceNotFoundException("User not found with username: " + username);
        }

        return new UserPrincipal(user);
    }


    public Users updatePassword(String UUID, String newPassword) throws Exception {

        Users users = userDetailsRepo.findByLinkedEntityId(UUID);
        if (users==null) {
            throw new ResourceNotFoundException("User not found with linked entity ID: " + UUID);
        }
        users.setPassword(newPassword);
        userDetailsRepo.save(users);
        return users;
    }
}
