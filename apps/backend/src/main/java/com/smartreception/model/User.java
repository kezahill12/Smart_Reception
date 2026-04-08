package com.smartreception.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
// UserDetails is a Spring Security interface
// By implementing it, Spring Security knows how to use this class for login
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    // This is the login username (email)
    @Column(unique = true, nullable = false)
    private String email;

    // Phone is unique per user
    // Nullable because OAuth2 users may not provide a phone number
    @Column(unique = true)
    private String phone;

    // Password is nullable because OAuth2 users (Google/GitHub) have no password
    @Column
    private String password;

    // The role decides what this user is allowed to do
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // This tells Spring Security which permissions this user has
    // We convert our Role into a GrantedAuthority that Spring understands
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
        // Examples: ROLE_RECEPTIONIST, ROLE_DOCTOR, ROLE_MANAGER
    }

    // Spring Security uses getUsername() as the unique identifier
    // We use email as our username
    @Override
    public String getUsername() {
        return email;
    }

    // Account status checks - all return true to keep things simple
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}