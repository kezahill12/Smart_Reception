package com.smartreception.controller;


import com.smartreception.dto.AuthResponse;
import com.smartreception.dto.LoginRequest;
import com.smartreception.model.User;
import com.smartreception.repository.UserRepository;
import com.smartreception.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth API", description = "Login and authentication endpoints")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    // POST /api/auth/login
    // Frontend sends: { "email": "jane@clinic.com", "password": "pass123" }
    // We return:      { "token": "eyJhbG...", "role": "DOCTOR" }
    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

        // Step 1: Ask Spring Security to verify the email and password
        // This internally calls UserDetailsServiceImpl.loadUserByUsername()
        // and checks the password with BCrypt
        // If wrong credentials → it throws an exception automatically
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Step 2: Get the authenticated user from the result
        User user = (User) authentication.getPrincipal();

        // Step 3: Generate a JWT token for this user
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // Step 4: Return the token and role to the frontend
        return ResponseEntity.ok(new AuthResponse(token, user.getRole().name()));
    }
}