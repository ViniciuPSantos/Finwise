package com.finwise.finwise.shared.exception;

public class TransferNotFoundException extends ResourceNotFoundException {
    public TransferNotFoundException() {
        super("Transfer not found");
    }
}
