package com.smartreception.websocket;

import com.smartreception.dto.AppointmentResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

// This service sends real-time messages to the frontend using WebSocket
// When a new appointment is booked, all connected clients receive it instantly
// The frontend subscribes to /topic/appointments and receives updates automatically
@Service
public class AppointmentNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public AppointmentNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Called by AppointmentService every time a new appointment is saved
    // Broadcasts the appointment details to ALL connected frontend clients
    public void notifyNewAppointment(AppointmentResponse appointment) {
        messagingTemplate.convertAndSend(
                "/topic/appointments", // frontend subscribes to this channel
                appointment            // the appointment data sent as JSON
        );
    }
}
