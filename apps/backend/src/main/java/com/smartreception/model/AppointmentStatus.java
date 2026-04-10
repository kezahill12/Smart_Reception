package com.smartreception.model;

// The 3 possible states of an appointment
// SCHEDULED → appointment is booked and upcoming
// COMPLETED → appointment has happened
// CANCELLED → appointment was cancelled
public enum AppointmentStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED
}