package com.smartreception.service;

import com.smartreception.dto.DoctorRequest;
import com.smartreception.dto.DoctorResponse;
import com.smartreception.exception.DuplicateResourceException;
import com.smartreception.exception.ResourceNotFoundException;
import com.smartreception.model.Doctor;
import com.smartreception.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    // ── GET ALL ──────────────────────────────────────────────────
    // Fetch every doctor and convert each to a response DTO
    public List<DoctorResponse> findAll() {
        return doctorRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── GET ONE ──────────────────────────────────────────────────
    // Throws 404 if no doctor exists with that ID
    public DoctorResponse findById(UUID id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with id: " + id));
        return toResponse(doctor);
    }

    // ── CREATE ───────────────────────────────────────────────────
    // Throws 409 if email or phone already belongs to another doctor
    public DoctorResponse save(DoctorRequest request) {

        // Make sure no other doctor already has this email
        if (doctorRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "A doctor with email " + request.getEmail() + " already exists");
        }

        // Make sure no other doctor already has this phone number
        if (doctorRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException(
                    "A doctor with phone " + request.getPhone() + " already exists");
        }

        Doctor doctor = toEntity(request);
        Doctor saved = doctorRepository.save(doctor);
        return toResponse(saved);
    }

    // ── UPDATE ───────────────────────────────────────────────────
    // Throws 404 if doctor does not exist
    // Throws 409 if the new email belongs to a different doctor
    public DoctorResponse update(UUID id, DoctorRequest request) {

        // First confirm the doctor exists
        Doctor existing = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with id: " + id));

        // Check if the new email belongs to a DIFFERENT doctor already
        doctorRepository.findByEmail(request.getEmail()).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new DuplicateResourceException(
                        "Email " + request.getEmail() + " is already used by another doctor");
            }
        });

        // Apply the updated fields
        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());
        existing.setSpecialization(request.getSpecialization());

        Doctor updated = doctorRepository.save(existing);
        return toResponse(updated);
    }

    // ── DELETE ───────────────────────────────────────────────────
    // Throws 404 if doctor does not exist
    public void delete(UUID id) {
        if (!doctorRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Doctor not found with id: " + id);
        }
        doctorRepository.deleteById(id);
    }

    // ── PRIVATE HELPERS ──────────────────────────────────────────
    // Convert request DTO → Doctor entity (for saving to database)
    private Doctor toEntity(DoctorRequest request) {
        Doctor doctor = new Doctor();
        doctor.setName(request.getName());
        doctor.setEmail(request.getEmail());
        doctor.setPhone(request.getPhone());
        doctor.setSpecialization(request.getSpecialization());
        return doctor;
    }

    // Convert Doctor entity → response DTO (for sending to frontend)
    private DoctorResponse toResponse(Doctor doctor) {
        DoctorResponse response = new DoctorResponse();
        response.setId(doctor.getId());
        response.setName(doctor.getName());
        response.setEmail(doctor.getEmail());
        response.setPhone(doctor.getPhone());
        response.setSpecialization(doctor.getSpecialization());
        return response;
    }
}