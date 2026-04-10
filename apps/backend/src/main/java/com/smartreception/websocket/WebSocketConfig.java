package com.smartreception.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// @EnableWebSocketMessageBroker turns on WebSocket support in Spring Boot
// WebSocket keeps a live connection open between frontend and backend
// so the backend can PUSH data to the frontend without the frontend asking
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /topic is where the backend sends messages TO the frontend
        // The frontend subscribes to /topic/appointments to receive new bookings
        registry.enableSimpleBroker("/topic");

        // /app is the prefix for messages FROM the frontend TO the backend
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // This is the URL the frontend connects to when opening a WebSocket
        // SockJS is a fallback library - if WebSocket is not supported, it uses HTTP
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // allow all origins for now - restrict in production
                .withSockJS();
    }
}