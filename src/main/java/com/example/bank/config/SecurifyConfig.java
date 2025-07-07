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

    @Autowired
    public SecurifyConfig(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/api/bank-management-system/card/**").hasRole("EMPLOYEE")

                        // EMPLOYEE endpoints
                        .requestMatchers("/api/bank-management-system/branch/**").hasRole("ADMIN")

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
        provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
        provider.setUserDetailsService(myUserDetailsService);
        return provider;
    }
}
