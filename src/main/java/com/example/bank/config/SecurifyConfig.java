package com.example.bank.config;

import com.example.bank.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // It says don't go for default security configuration, use this one instead
// This annotation enables Spring Security's web security support and provides the Spring MVC integration.
// It allows you to customize the security configuration for your application.
// By using this annotation, you can define your own security rules, such as authentication and authorization
public class SecurifyConfig {
    // This class can be used to define custom security configurations, such as authentication providers,
    // password encoders, and access rules for different endpoints.
    // You can extend WebSecurity Configure Adapter to override methods like configure(http http)
    // to set up your security policies.

    private final MyUserDetailsService myUserDetailsService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public SecurifyConfig(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Card endpoints: EMPLOYEE only
                        .requestMatchers("/api/bank-management-system/card/**").hasRole("EMPLOYEE")
                        // Branch endpoints: ADMIN only
                        .requestMatchers("/api/bank-management-system/branch/**").hasRole("ADMIN")
                        // Account endpoints: EMPLOYEE for most, CUSTOMER for some
                        .requestMatchers("/api/bank-management-system/account/**").hasAnyRole("EMPLOYEE", "CUSTOMER")
                        // Loan endpoints: EMPLOYEE for most, CUSTOMER for some
                        .requestMatchers("/api/bank-management-system/loan/**").hasAnyRole("EMPLOYEE", "CUSTOMER")
                        // Employee endpoints: EMPLOYEE for most, ADMIN for add-employee
                        .requestMatchers("/api/bank-management-system/employee/add-employee").hasRole("ADMIN")
                        .requestMatchers("/api/bank-management-system/employee/**").hasRole("EMPLOYEE")
                        // Customer endpoints: ADMIN, EMPLOYEE for most, CUSTOMER for some, permitAll for create
                        .requestMatchers("/api/bank-management-system/customer/create-customer").permitAll()
                        .requestMatchers("/api/bank-management-system/customer/update-customer/**").hasRole("CUSTOMER")
                        .requestMatchers("/api/bank-management-system/customer/account/**").hasRole("CUSTOMER")
                        .requestMatchers("/api/bank-management-system/customer/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        // Any other endpoint must be authenticated
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(myUserDetailsService);
        return provider;
    }
}
