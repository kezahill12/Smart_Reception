package com.smartreception.dto;


import lombok.Data;
import java.util.UUID;

// What we send BACK to the frontend
// Notice: password is NOT here - we never send passwords back
@Data
public class ReceptionistResponse {

    private UUID id;
    private String name;
    private String email;
    private String phone;
    // role is always RECEPTIONIST but we include it
    // so the frontend knows what role this user has
    private String role;
}