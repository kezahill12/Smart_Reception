package com.smartreception.controller;

import com.smartreception.dto.PatientRequest;
import com.smartreception.dto.PatientResponse;
import com.smartreception.service.PatientService;
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
@RequestMapping("/api/patients")
@Tag(name = "Patient API", description = "Endpoints for patient management")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // GET /api/patients → returns all patients
    @GetMapping
    @Operation(summary = "Get all patients")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'MANAGER')")
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        return ResponseEntity.ok(patientService.findAll());
    }

    // GET /api/patients/{id} → returns one patient by ID
    @GetMapping("/{id}")
    @Operation(summary = "Get a patient by ID")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'MANAGER')")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.findById(id));
    }

    // POST /api/patients → creates a new patient
    // @Valid triggers the validation rules we wrote in PatientRequest
    @PostMapping
    @Operation(summary = "Create a new patient")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'MANAGER')")
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody PatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.save(request));
    }

    // PUT /api/patients/{id} → updates an existing patient
    @PutMapping("/{id}")
    @Operation(summary = "Update a patient")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'MANAGER')")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable UUID id,
            @Valid @RequestBody PatientRequest request) {
        return ResponseEntity.ok(patientService.update(id, request));
    }

    // DELETE /api/patients/{id} → deletes a patient
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a patient")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'MANAGER')")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        patientService.delete(id);
        return ResponseEntity.noContent().build(); // returns 204 No Content
    }
}