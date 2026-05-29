package com.finwise.finwise.shared.exception;

public class AccountNotFoundException extends ResourceNotFoundException {

    public AccountNotFoundException() {
        super("Account not found");
    }
}
