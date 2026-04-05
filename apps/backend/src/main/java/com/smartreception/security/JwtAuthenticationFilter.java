package com.smartreception.security;

import com.smartreception.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// This filter runs ONCE per request (OncePerRequestFilter guarantees that)
// Its job: read the JWT from the request header and authenticate the user
// Think of it as a security guard checking ID cards at the door
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: Read the Authorization header
        // It should look like: "Bearer eyJhbGc..."
        final String authHeader = request.getHeader("Authorization");

        // Step 2: If there is no token or it does not start with "Bearer ", skip this filter
        // The request will either be allowed (public endpoint) or rejected by SecurityConfig
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Cut off the "Bearer " prefix to get the raw token
        final String token = authHeader.substring(7);

        // Step 4: Extract the email from inside the token
        final String email = jwtUtil.extractEmail(token);

        // Step 5: Only proceed if we got an email AND the user is not already authenticated
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Step 6: Load the full user from the database
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Step 7: Validate the token (check it matches this user and is not expired)
            if (jwtUtil.isTokenValid(token, userDetails.getUsername())) {

                // Step 8: Create an authentication object with the user's details and roles
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,                         // no credentials needed (token already verified)
                                userDetails.getAuthorities()  // their roles
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Step 9: Tell Spring Security "this user is authenticated"
                // From this point, @PreAuthorize and role checks work automatically
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Step 10: Continue to the next filter or the actual controller
        filterChain.doFilter(request, response);
    }
}