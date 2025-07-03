package com.example.bank.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // CSRF (Cross-Site Request Forgery)
        // it is mostly used for put post requests https://chatgpt.com/share/686355d3-77d4-8000-8d89-687e2e2e6c81
        http.csrf(customizer->customizer.disable());
        // what is below line doing?
        // It disables the default security configuration for the application.
        // This means that the application will not have any default security settings applied,
        // allowing you to define your own security rules and configurations.
        // It is useful when you want to have complete control over the security configuration
        // and do not want to rely on the default settings provided by Spring Security.
        // It is important to note that disabling the default security configuration can expose your application to security
        // vulnerabilities if you do not implement your own security measures.
        http.authorizeHttpRequests(request->request.anyRequest().authenticated());
        // This line configures the application to require authentication for all HTTP requests.
        // It means that any request made to the application must be authenticated before it can be processed.
//        http.formLogin(Customizer.withDefaults());
        // This line enables form-based login for the application.
        // It allows users to log in using a form, typically with a username and password.
        // The Customizer.withDefaults() method provides default configurations for the form login,
        // such as the login page URL and the default success and failure URLs.
        // This means that the application will use the default login page provided by Spring Security,
        // and it will handle the login process automatically without requiring additional configuration.
        http.httpBasic(Customizer.withDefaults());
        // This line enables HTTP Basic authentication for the application.
        // HTTP Basic authentication is a simple authentication scheme that uses a username and password
        // to authenticate users. When a user tries to access a protected resource,
        // the application will prompt for a username and password in the HTTP request headers.

        http.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // This line configures the session management for the application.
        // It sets the session creation policy to stateless, meaning that the application will not create
        // or maintain any session state on the server side. Each request will be treated as an independent request,
        // and no session information will be stored on the server. This is useful for applications that do not require session management,
        // such as RESTful APIs, where each request is self-contained and does not rely on session state.


        return http.build();

    }


    // UserDetailsService Bean
    // we have controller, service, repository layers ,but when user send a request to the application,
    // request goes to the controller, then service, then repository, to db, and to verity  user credentials
    // we need to use UserDetailsService to load user details for authentication
    /*@Bean
    public UserDetailsService userDetailsService(){

        UserDetails user1 = User.
                withDefaultPasswordEncoder()// don't use it , it is deprecated this method is used to create a user with a default password encoder
                .username("sanket")
                .password("s@123")
                .roles("USER") // roles are used to define the authority of the user
                .build();

        UserDetails user2 = User.
                withDefaultPasswordEncoder()
                .username("admin")
                .password("admin@123")
                .roles("ADMIN") // roles are used to define the authority of the user
                .build()

        return new InMemoryUserDetailsManager(user1, user2);
        but this is returning hard coded users
    }*/

    // when we pass username and password in the request to the server, It is basically Un-authenticated object
    // It goes to authenticated provider, which is responsible for authenticating the user
    // then make it as authenticated object

    @Bean
    public AuthenticationProvider authenticationProvider(){

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    }

}
