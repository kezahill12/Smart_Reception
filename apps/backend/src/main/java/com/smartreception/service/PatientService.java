package com.smartreception.service;

import com.smartreception.config.MapperConfig;
import com.smartreception.dto.PatientRequest;
import com.smartreception.dto.PatientResponse;
import com.smartreception.model.Patient;
import com.smartreception.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// @Service means Spring manages this class
// All business logic lives here - the controller just calls this
@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final MapperConfig mapper;

    // Spring automatically injects these two dependencies
    public PatientService(PatientRepository patientRepository, MapperConfig mapper) {
        this.patientRepository = patientRepository;
        this.mapper = mapper;
    }

    // Get every patient and convert each one to a response DTO
    public List<PatientResponse> findAll() {
        return patientRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    // Find one patient by their ID - throw a clear error if not found
    public PatientResponse findById(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        return mapper.toResponse(patient);
    }

    // Create a new patient from a request DTO
    public PatientResponse save(@Valid PatientRequest request) {
        Patient patient = mapper.toEntity(request);
        Patient saved = patientRepository.save(patient);
        return mapper.toResponse(saved);
    }

    // Update an existing patient - only change fields that are provided
    public PatientResponse update(UUID id, PatientRequest request) {
        // First check if patient exists
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

        // Update only the fields coming from the request
        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());

        Patient updated = patientRepository.save(existing);
        return mapper.toResponse(updated);
    }

    // Delete a patient by ID - throw error if they don't exist
    public void delete(UUID id) {
        if (!patientRepository.existsById(id)) {
            throw new RuntimeException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }
}