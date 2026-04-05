package com.smartreception.repository;


import com.smartreception.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    // Used by OAuth2 handler to check if a user already registered via Google/GitHub
    boolean existsByEmail(String email);
}