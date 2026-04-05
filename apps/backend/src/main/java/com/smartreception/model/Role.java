package com.smartreception.model;


public enum Role {
    RECEPTIONIST,   // can manage patients
    DOCTOR,         // can view patients and appointments
    MANAGER         // full access
}