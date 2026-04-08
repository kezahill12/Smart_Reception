package com.smartreception.controller;

import com.smartreception.dto.DoctorRequest;
import com.smartreception.dto.DoctorResponse;
import com.smartreception.service.DoctorService;
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
@RequestMapping("/api/doctors")
@Tag(name = "Doctor API", description = "Endpoints for doctor management")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    // GET /api/doctors
    // All roles can view the list of doctors
    @GetMapping
    @Operation(summary = "Get all doctors")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'MANAGER')")
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.findAll());
    }

    // GET /api/doctors/{id}
    // All roles can view a single doctor
    @GetMapping("/{id}")
    @Operation(summary = "Get a doctor by ID")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'MANAGER')")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable UUID id) {
        return ResponseEntity.ok(doctorService.findById(id));
    }

    // POST /api/doctors
    // Only MANAGER can add a new doctor
    @PostMapping
    @Operation(summary = "Create a new doctor")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<DoctorResponse> createDoctor(
            @Valid @RequestBody DoctorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(doctorService.save(request));
    }

    // PUT /api/doctors/{id}
    // Only MANAGER can update a doctor
    @PutMapping("/{id}")
    @Operation(summary = "Update a doctor")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<DoctorResponse> updateDoctor(
            @PathVariable UUID id,
            @Valid @RequestBody DoctorRequest request) {
        return ResponseEntity.ok(doctorService.update(id, request));
    }

    // DELETE /api/doctors/{id}
    // Only MANAGER can delete a doctor
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a doctor")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteDoctor(@PathVariable UUID id) {
        doctorService.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
