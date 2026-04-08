package com.smartreception.controller;


import com.smartreception.dto.ReceptionistRequest;
import com.smartreception.dto.ReceptionistResponse;
import com.smartreception.service.ReceptionistService;
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
@RequestMapping("/api/receptionists")
@Tag(name = "Receptionist API", description = "Endpoints for receptionist management")
public class ReceptionistController {

    private final ReceptionistService receptionistService;

    public ReceptionistController(ReceptionistService receptionistService) {
        this.receptionistService = receptionistService;
    }

    // GET /api/receptionists
    // MANAGER can see all receptionists
    @GetMapping
    @Operation(summary = "Get all receptionists")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<ReceptionistResponse>> getAllReceptionists() {
        return ResponseEntity.ok(receptionistService.findAll());
    }

    // GET /api/receptionists/{id}
    // MANAGER can view a single receptionist
    @GetMapping("/{id}")
    @Operation(summary = "Get a receptionist by ID")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ReceptionistResponse> getReceptionistById(@PathVariable UUID id) {
        return ResponseEntity.ok(receptionistService.findById(id));
    }

    // POST /api/receptionists
    // Only MANAGER can create a receptionist account
    @PostMapping
    @Operation(summary = "Create a new receptionist")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ReceptionistResponse> createReceptionist(
            @Valid @RequestBody ReceptionistRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(receptionistService.save(request));
    }

    // PUT /api/receptionists/{id}
    // Only MANAGER can update a receptionist
    @PutMapping("/{id}")
    @Operation(summary = "Update a receptionist")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ReceptionistResponse> updateReceptionist(
            @PathVariable UUID id,
            @Valid @RequestBody ReceptionistRequest request) {
        return ResponseEntity.ok(receptionistService.update(id, request));
    }

    // DELETE /api/receptionists/{id}
    // Only MANAGER can delete a receptionist
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a receptionist")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteReceptionist(@PathVariable UUID id) {
        receptionistService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
