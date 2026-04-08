package com.smartreception.model;


import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

// This is the Doctor table in the database
// Each field becomes a column
@Entity
@Table(name = "doctors")
@Data
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    // Each doctor must have a unique email
    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phone;

    // What the doctor specializes in
    // e.g. "Cardiology", "Pediatrics"
    @Column(nullable = false)
    private String specialization;
}
