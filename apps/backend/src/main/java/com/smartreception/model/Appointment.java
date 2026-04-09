package com.smartreception.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointments")
@Data
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Which patient this appointment is for
    // ManyToOne means many appointments can belong to one patient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Which doctor this appointment is with
    // ManyToOne means many appointments can belong to one doctor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // When the appointment is scheduled for
    // LocalDateTime holds both date and time e.g. 2024-03-15T09:30:00
    @Column(nullable = false)
    private LocalDateTime appointmentTime;

    // Current status of the appointment
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    // Optional notes or reason for the visit
    // e.g. "Follow up on blood pressure medication"
    @Column
    private String notes;

    // When this record was created in the system
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Automatically set createdAt before the record is first saved
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        // If no status is set, default to SCHEDULED
        if (this.status == null) {
            this.status = AppointmentStatus.SCHEDULED;
        }
    }
}