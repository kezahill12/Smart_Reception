package com.smartreception.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// What the frontend sends when creating or updating a receptionist
@Data
public class ReceptionistRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Please provide a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Password is required")
    // Password is required because receptionists log into the system
    // unlike patients who are just records in the database
    private String password;
}