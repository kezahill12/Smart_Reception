package com.smartreception.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

import static io.jsonwebtoken.Jwts.*;

// JwtUtil is responsible for 3 things:
// 1. Generating a token when a user logs in
// 2. Reading the email out of a token
// 3. Checking if a token is still valid (not expired, not tampered)
@Component
public class JwtUtil {

    // The secret key is loaded from application.properties
    // It must be at least 256 bits long for HS256 algorithm
    @Value("${jwt.secret}")
    private String secret;

    // How long the token lasts (loaded from application.properties)
    // Default: 86400000 ms = 24 hours
    @Value("${jwt.expiration:86400000}")
    private long expirationMs;

    // Convert the secret string into a cryptographic Key object
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // GENERATE a token for a logged-in user
    // We store the email and role INSIDE the token (these are called "claims")
    public String generateToken(String email, String role) {
        return builder()
                .setSubject(email)                          // who this token belongs to
                .claim("role", role)                        // their role stored inside
                .setIssuedAt(new Date())                    // when it was created
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs)) // when it expires
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // sign with our secret
                .compact();                                 // build it into a string
    }

    // READ the email from inside a token
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // READ the role from inside a token
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    // CHECK if a token is valid - not expired and email matches
    public boolean isTokenValid(String token, String email) {
        final String extractedEmail = extractEmail(token);
        return extractedEmail.equals(email) && !isTokenExpired(token);
    }

    // Helper: check if the token's expiry date has passed
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Helper: parse the token and extract all claims (the data inside)
    // This also verifies the signature - if someone tampered with the token, this throws an error
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}