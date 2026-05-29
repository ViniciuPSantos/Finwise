package com.finwise.finwise.shared.exception;

public class TransactionNotFoundException extends ResourceNotFoundException {
    public TransactionNotFoundException() {
        super("Transaction not found");
    }
}