package com.smartreception.security;

import com.smartreception.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
// @EnableMethodSecurity allows us to use @PreAuthorize on individual controller methods
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsServiceImpl userDetailsService,
                          OAuth2SuccessHandler oAuth2SuccessHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF - not needed for stateless JWT APIs
                .csrf(csrf -> csrf.disable())

                // Define which endpoints are public and which need authentication
                .authorizeHttpRequests(auth -> auth

                        // These endpoints are open to everyone (no token needed)
                        .requestMatchers(
                                "/api/auth/**",         // login endpoint
                                "/oauth2/**",           // OAuth2 redirect URLs
                                "/swagger-ui/**",       // API docs
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Patient endpoints:
                        // Only RECEPTIONIST and MANAGER can create, update, delete
                        .requestMatchers("POST /api/patients/**").hasAnyRole("RECEPTIONIST", "MANAGER")
                        .requestMatchers("PUT /api/patients/**").hasAnyRole("RECEPTIONIST", "MANAGER")
                        .requestMatchers("DELETE /api/patients/**").hasRole("MANAGER")

                        // Any other request just needs a valid token (any role)
                        .anyRequest().authenticated()
                )

                // Tell Spring: do NOT create sessions - we use JWT, not cookies
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Set up OAuth2 login with Google and GitHub
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler) // run our handler after successful OAuth2
                )

                // Tell Spring which AuthenticationProvider to use
                .authenticationProvider(authenticationProvider())

                // Add our JWT filter BEFORE Spring's default login filter
                // So JWT is checked first on every request
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // BCryptPasswordEncoder safely hashes passwords before storing them
    // Never store plain text passwords - BCrypt adds salt and hashes automatically
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // This tells Spring: use our UserDetailsService + BCrypt to verify logins
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // AuthenticationManager is needed by AuthController to trigger the login process
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}