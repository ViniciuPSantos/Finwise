package com.finwise.finwise.shared.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("Insufficient balance for this operation");
    }
}
