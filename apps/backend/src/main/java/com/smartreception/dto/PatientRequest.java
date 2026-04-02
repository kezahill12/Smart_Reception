package com.smartreception.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PatientRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Please provide a valid email")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;
}
