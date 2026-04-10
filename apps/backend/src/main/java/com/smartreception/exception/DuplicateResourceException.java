package com.smartreception.exception;


// A specific exception for when someone tries to create a duplicate record
// e.g. two doctors with the same email
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
