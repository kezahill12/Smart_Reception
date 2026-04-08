package com.smartreception.repository;

import com.smartreception.model.Role;
import com.smartreception.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// We are extending the existing UserRepository concept
// but giving it its own interface for clarity
// This queries the users table filtering by role
@Repository
public interface ReceptionistRepository extends JpaRepository<User, UUID> {

    // Get all users who have the RECEPTIONIST role
    // Spring generates: SELECT * FROM users WHERE role = ?
    List<User> findAllByRole(Role role);

    // Find a specific user by email
    Optional<User> findByEmail(String email);

    // Check if email already exists
    boolean existsByEmail(String email);

    // Check if phone already exists
    boolean existsByPhone(String phone);
}