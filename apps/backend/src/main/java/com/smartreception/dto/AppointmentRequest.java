package com.smartreception.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

// What the frontend sends when booking an appointment
@Data
public class AppointmentRequest {

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @NotNull(message = "Doctor ID is required")
    private UUID doctorId;

    @NotNull(message = "Appointment time is required")
    @Future(message = "Appointment time must be in the future")
    // @Future ensures nobody books an appointment in the past
    private LocalDateTime appointmentTime;

    // Notes are optional - receptionist can leave this empty
    private String notes;
}
