package com.smartreception.service;


import com.smartreception.dto.AppointmentRequest;
import com.smartreception.dto.AppointmentResponse;
import com.smartreception.exception.DuplicateResourceException;
import com.smartreception.exception.ResourceNotFoundException;
import com.smartreception.model.Appointment;
import com.smartreception.model.AppointmentStatus;
import com.smartreception.model.Doctor;
import com.smartreception.model.Patient;
import com.smartreception.repository.AppointmentRepository;
import com.smartreception.repository.DoctorRepository;
import com.smartreception.repository.PatientRepository;
import com.smartreception.websocket.AppointmentNotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentNotificationService notificationService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              AppointmentNotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.notificationService = notificationService;
    }

    // ── GET ALL ──────────────────────────────────────────────────
    public List<AppointmentResponse> findAll() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── GET ONE ──────────────────────────────────────────────────
    public AppointmentResponse findById(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + id));
        return toResponse(appointment);
    }

    // ── GET BY PATIENT ───────────────────────────────────────────
    public List<AppointmentResponse> findByPatient(UUID patientId) {
        return appointmentRepository.findAllByPatient_Id(patientId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── GET BY STATUS ────────────────────────────────────────────
    public List<AppointmentResponse> findByStatus(AppointmentStatus status) {
        return appointmentRepository.findAllByStatus(status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── CREATE ───────────────────────────────────────────────────
    public AppointmentResponse save(AppointmentRequest request) {

        // Step 1: Make sure the patient exists
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + request.getPatientId()));

        // Step 2: Make sure the doctor exists
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with id: " + request.getDoctorId()));

        // Step 3: CONFLICT CHECK
        // We block a 30-minute window around the requested time
        // So if an appointment is at 09:00, nobody can book that doctor from 08:30 to 09:30
        LocalDateTime start = request.getAppointmentTime().minusMinutes(30);
        LocalDateTime end   = request.getAppointmentTime().plusMinutes(30);

        boolean hasConflict = appointmentRepository.existsConflict(
                doctor.getId(), start, end);

        if (hasConflict) {
            throw new DuplicateResourceException(
                    "Doctor " + doctor.getName() +
                            " already has an appointment around " +
                            request.getAppointmentTime() +
                            ". Please choose a different time.");
        }

        // Step 4: Build and save the appointment
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setNotes(request.getNotes());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        Appointment saved = appointmentRepository.save(appointment);

        // Step 5: Notify all connected clients via WebSocket
        // This broadcasts the new appointment to the frontend in real time
        notificationService.notifyNewAppointment(toResponse(saved));

        return toResponse(saved);
    }

    // ── UPDATE STATUS ────────────────────────────────────────────
    // We only allow updating the STATUS and NOTES of an appointment
    // To change doctor or patient, cancel and rebook
    public AppointmentResponse updateStatus(UUID id, AppointmentStatus newStatus) {
        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + id));

        existing.setStatus(newStatus);
        Appointment updated = appointmentRepository.save(existing);
        return toResponse(updated);
    }

    // ── DELETE ───────────────────────────────────────────────────
    public void delete(UUID id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Appointment not found with id: " + id);
        }
        appointmentRepository.deleteById(id);
    }

    // ── PRIVATE HELPER ───────────────────────────────────────────
    private AppointmentResponse toResponse(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setPatientId(appointment.getPatient().getId());
        response.setPatientName(appointment.getPatient().getName());
        response.setDoctorId(appointment.getDoctor().getId());
        response.setDoctorName(appointment.getDoctor().getName());
        response.setDoctorSpecialization(appointment.getDoctor().getSpecialization());
        response.setAppointmentTime(appointment.getAppointmentTime());
        response.setStatus(appointment.getStatus().name());
        response.setNotes(appointment.getNotes());
        response.setCreatedAt(appointment.getCreatedAt());
        return response;
    }
}
