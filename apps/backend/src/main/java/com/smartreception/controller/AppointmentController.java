package com.smartreception.controller;

import com.smartreception.dto.AppointmentRequest;
import com.smartreception.dto.AppointmentResponse;
import com.smartreception.model.AppointmentStatus;
import com.smartreception.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Appointment API", description = "Endpoints for appointment management")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // GET /api/appointments
    // All roles can view appointments
    @GetMapping
    @Operation(summary = "Get all appointments")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'MANAGER')")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.findAll());
    }

    // GET /api/appointments/{id}
    // All roles can view a single appointment
    @GetMapping("/{id}")
    @Operation(summary = "Get an appointment by ID")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'MANAGER')")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.findById(id));
    }

    // GET /api/appointments/patient/{patientId}
    // All roles can view appointments for a specific patient
    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get all appointments for a patient")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'MANAGER')")
    public ResponseEntity<List<AppointmentResponse>> getByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(appointmentService.findByPatient(patientId));
    }

    // GET /api/appointments/status/{status}
    // All roles can filter appointments by status
    @GetMapping("/status/{status}")
    @Operation(summary = "Get appointments by status")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'MANAGER')")
    public ResponseEntity<List<AppointmentResponse>> getByStatus(
            @PathVariable AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.findByStatus(status));
    }

    // POST /api/appointments
    // Only RECEPTIONIST and MANAGER can book appointments
    @PostMapping
    @Operation(summary = "Book a new appointment")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'MANAGER')")
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.save(request));
    }

    // PATCH /api/appointments/{id}/status
    // RECEPTIONIST and MANAGER can update appointment status
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update appointment status")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'MANAGER')")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, status));
    }

    // DELETE /api/appointments/{id}
    // Only MANAGER can delete an appointment
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an appointment")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}