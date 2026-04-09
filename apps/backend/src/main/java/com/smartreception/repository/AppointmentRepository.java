package com.smartreception.repository;

import com.smartreception.model.Appointment;
import com.smartreception.model.AppointmentStatus;
import com.smartreception.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    // Get all appointments for a specific doctor
    List<Appointment> findAllByDoctor(Doctor doctor);

    // Get all appointments for a specific patient
    List<Appointment> findAllByPatient_Id(UUID patientId);

    // Get all appointments with a specific status
    List<Appointment> findAllByStatus(AppointmentStatus status);

    // ── CONFLICT CHECKING QUERY ───────────────────────────────────
    // This is the most important query in the whole module
    // It checks if a doctor already has a SCHEDULED appointment
    // within 30 minutes of the requested time
    // If this returns true we reject the booking
    @Query("""
            SELECT COUNT(a) > 0 FROM Appointment a
            WHERE a.doctor.id = :doctorId
            AND a.status = 'SCHEDULED'
            AND a.appointmentTime BETWEEN :start AND :end
            """)
    boolean existsConflict(
            @Param("doctorId") UUID doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}