package com.finwise.finwise.shared.exception;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException() {
        super("Too many failed login attempts. Try again in 15 minutes.");
    }
}
