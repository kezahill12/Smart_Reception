package com.smartreception.repository;


import com.smartreception.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    // Spring generates SQL from this method name automatically:
    // SELECT * FROM doctors WHERE email = ?
    // Used to check for duplicate emails before saving
    Optional<Doctor> findByEmail(String email);

    // Used to check for duplicate phone numbers before saving
    boolean existsByPhone(String phone);

    // Used to check for duplicate emails before saving
    boolean existsByEmail(String email);

}
