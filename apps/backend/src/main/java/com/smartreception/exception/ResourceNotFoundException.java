package com.smartreception.exception;

// A specific exception for when something is not found
// We throw this instead of RuntimeException when a record does not exist
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}