package com.smartreception.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // Lombok generates a constructor with all fields
public class AuthResponse {

    private String token;   // the JWT token
    private String role;    // the user's role so frontend can show correct screens
}