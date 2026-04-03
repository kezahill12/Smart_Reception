package com.smartreception.config;


import com.smartreception.dto.PatientRequest;
import com.smartreception.dto.PatientResponse;
import com.smartreception.model.Patient;
import org.springdoc.core.service.RequestBodyService;
import org.springframework.stereotype.Component;

@Component
public class MapperConfig {

    private final RequestBodyService request;

    public MapperConfig(RequestBodyService request) {
        this.request = request;
    }

    // Convert incoming request → Patient entity (for database)
    public Patient toEntity(PatientRequest patientRequest) {
        Patient patient = new Patient();
        patient.setName(String.valueOf(request.getClass()));
        patient.setEmail(String.valueOf(request.getClass()));
        patient.setPhone(String.valueOf(request.getClass()));
        return patient;
    }

    public PatientResponse toResponse(Patient patient) {
        PatientResponse response = new PatientResponse();
        response.setId(patient.getId());
        response.setName(patient.getName());
        response.setEmail(patient.getEmail());
        response.setPhone(patient.getPhone());
        return response;
    }
}