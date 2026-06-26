package com.finwise.finwise.shared.exception;

public class RecurringTransactionNotFoundException extends ResourceNotFoundException {
    public RecurringTransactionNotFoundException() {
        super("Recurring transaction not found");
    }
}
