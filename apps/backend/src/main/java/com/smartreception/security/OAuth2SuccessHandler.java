package com.smartreception.security;


import com.smartreception.model.Role;
import com.smartreception.model.User;
import com.smartreception.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// This runs automatically AFTER Google or GitHub successfully authenticates the user
// At this point we know who the person is (Google gave us their name and email)
// Our job: save them if they are new, then give them a JWT token
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public OAuth2SuccessHandler(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // Step 1: Get the user info that Google/GitHub returned
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name  = oAuth2User.getAttribute("name");

        // Step 2: Check if this person already has an account
        // If not, create one with RECEPTIONIST role by default
        if (!userRepository.existsByEmail(email)) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPassword(null); // OAuth2 users have no password
            newUser.setRole(Role.RECEPTIONIST); // default role for new OAuth2 users
            userRepository.save(newUser);
        }

        // Step 3: Load the user to get their role
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found after OAuth2 login"));

        // Step 4: Generate a JWT token for this user
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // Step 5: Redirect the frontend with the token in the URL
        // The frontend reads this token from the URL and stores it
        // Change this URL to match your frontend address
        String redirectUrl = "http://localhost:3000/oauth2/callback?token=" + token;
        response.sendRedirect(redirectUrl);
    }
}
