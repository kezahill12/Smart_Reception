package com.smartreception.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// What the frontend sends to CREATE or UPDATE a doctor
// We validate here so bad data never reaches the database
@Data
public class DoctorRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Please provide a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Specialization is required")
    // Examples: "Cardiology", "Pediatrics", "General Practice"
    private String specialization;
}