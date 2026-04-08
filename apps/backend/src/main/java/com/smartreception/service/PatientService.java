package com.smartreception.service;

import com.smartreception.config.MapperConfig;
import com.smartreception.dto.PatientRequest;
import com.smartreception.dto.PatientResponse;
import com.smartreception.exception.DuplicateResourceException;
import com.smartreception.exception.ResourceNotFoundException;
import com.smartreception.model.Patient;
import com.smartreception.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final MapperConfig mapper;

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

    // Find one patient by their ID
    // Throws 404 if no patient exists with that ID
    public PatientResponse findById(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + id));
        return mapper.toResponse(patient);
    }

    // Create a new patient
    // Throws 409 if email or phone already belongs to another patient
    public PatientResponse save(PatientRequest request) {

        // Check for duplicate email before saving
        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "A patient with email " + request.getEmail() + " already exists");
        }

        // Check for duplicate phone before saving
        if (patientRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException(
                    "A patient with phone " + request.getPhone() + " already exists");
        }

        Patient patient = mapper.toEntity(request);
        Patient saved = patientRepository.save(patient);
        return mapper.toResponse(saved);
    }

    // Update an existing patient
    // Throws 404 if patient does not exist
    // Throws 409 if the new email belongs to a different patient
    public PatientResponse update(UUID id, PatientRequest request) {

        // Confirm the patient exists first
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + id));

        // Check if the new email belongs to a DIFFERENT patient already
        patientRepository.findByEmail(request.getEmail()).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new DuplicateResourceException(
                        "Email " + request.getEmail() + " is already used by another patient");
            }
        });

        // Apply the updated fields
        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());

        Patient updated = patientRepository.save(existing);
        return mapper.toResponse(updated);
    }

    // Delete a patient by ID
    // Throws 404 if patient does not exist
    public void delete(UUID id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }
}