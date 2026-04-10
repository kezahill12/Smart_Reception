package com.smartreception.repository;

import com.smartreception.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    // Used to check for duplicate emails before saving
    Optional<Patient> findByEmail(String email);

    // Used to check for duplicate emails before saving
    boolean existsByEmail(String email);

    // Used to check for duplicate phone numbers before saving
    boolean existsByPhone(String phone);
}