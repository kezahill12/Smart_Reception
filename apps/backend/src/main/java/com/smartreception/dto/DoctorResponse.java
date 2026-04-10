package com.smartreception.dto;


import lombok.Data;
import java.util.UUID;

// What we send BACK to the frontend after any doctor operation
// We control exactly what is visible - nothing more, nothing less
@Data
public class DoctorResponse {

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String specialization;
}
