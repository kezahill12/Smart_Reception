package com.smartreception.service;


import com.smartreception.dto.ReceptionistRequest;
import com.smartreception.dto.ReceptionistResponse;
import com.smartreception.exception.DuplicateResourceException;
import com.smartreception.exception.ResourceNotFoundException;
import com.smartreception.model.Role;
import com.smartreception.model.User;
import com.smartreception.repository.ReceptionistRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReceptionistService {

    private final ReceptionistRepository receptionistRepository;
    private final PasswordEncoder passwordEncoder;

    // PasswordEncoder is injected to hash passwords before saving
    // We defined BCryptPasswordEncoder as a bean in SecurityConfig
    public ReceptionistService(ReceptionistRepository receptionistRepository,
                               PasswordEncoder passwordEncoder) {
        this.receptionistRepository = receptionistRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ── GET ALL ──────────────────────────────────────────────────
    // Only fetch users who have the RECEPTIONIST role
    public List<ReceptionistResponse> findAll() {
        return receptionistRepository.findAllByRole(Role.RECEPTIONIST)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── GET ONE ──────────────────────────────────────────────────
    // Find one receptionist by ID - throw 404 if not found
    public ReceptionistResponse findById(UUID id) {
        User user = receptionistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Receptionist not found with id: " + id));

        // Make sure the user we found is actually a receptionist
        if (!user.getRole().equals(Role.RECEPTIONIST)) {
            throw new ResourceNotFoundException(
                    "Receptionist not found with id: " + id);
        }

        return toResponse(user);
    }

    // ── CREATE ───────────────────────────────────────────────────
    // Create a new receptionist account
    // Only MANAGER can do this (enforced in the controller)
    public ReceptionistResponse save(ReceptionistRequest request) {

        // Check for duplicate email
        if (receptionistRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "A user with email " + request.getEmail() + " already exists");
        }

        // Check for duplicate phone
        if (receptionistRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException(
                    "A user with phone " + request.getPhone() + " already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        // IMPORTANT: always hash the password before saving
        // never store plain text passwords
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // Role is always RECEPTIONIST when created through this service
        user.setRole(Role.RECEPTIONIST);

        User saved = receptionistRepository.save(user);
        return toResponse(saved);
    }

    // ── UPDATE ───────────────────────────────────────────────────
    // Update an existing receptionist
    public ReceptionistResponse update(UUID id, ReceptionistRequest request) {

        // Confirm the receptionist exists
        User existing = receptionistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Receptionist not found with id: " + id));

        // Make sure we are updating a receptionist not another role
        if (!existing.getRole().equals(Role.RECEPTIONIST)) {
            throw new ResourceNotFoundException(
                    "Receptionist not found with id: " + id);
        }

        // Check if the new email belongs to a DIFFERENT user already
        receptionistRepository.findByEmail(request.getEmail()).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new DuplicateResourceException(
                        "Email " + request.getEmail() + " is already used by another user");
            }
        });

        // Apply updated fields
        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());
        // Re-hash the new password if it was changed
        existing.setPassword(passwordEncoder.encode(request.getPassword()));

        User updated = receptionistRepository.save(existing);
        return toResponse(updated);
    }

    // ── DELETE ───────────────────────────────────────────────────
    // Delete a receptionist by ID
    public void delete(UUID id) {
        User user = receptionistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Receptionist not found with id: " + id));

        // Make sure we are not accidentally deleting a doctor or manager
        if (!user.getRole().equals(Role.RECEPTIONIST)) {
            throw new ResourceNotFoundException(
                    "Receptionist not found with id: " + id);
        }

        receptionistRepository.deleteById(id);
    }

    // ── PRIVATE HELPER ───────────────────────────────────────────
    // Convert User entity → ReceptionistResponse DTO
    private ReceptionistResponse toResponse(User user) {
        ReceptionistResponse response = new ReceptionistResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole().name());
        return response;
    }
}