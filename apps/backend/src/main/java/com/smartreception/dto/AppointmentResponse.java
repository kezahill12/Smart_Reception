package com.smartreception.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

// What we send back to the frontend after any appointment operation
@Data
public class AppointmentResponse {

    private UUID id;

    // We return IDs and names so the frontend can display them
    private UUID patientId;
    private String patientName;

    private UUID doctorId;
    private String doctorName;
    private String doctorSpecialization;

    private LocalDateTime appointmentTime;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
}