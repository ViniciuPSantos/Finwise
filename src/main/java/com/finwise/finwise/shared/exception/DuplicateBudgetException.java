package com.finwise.finwise.shared.exception;

public class DuplicateBudgetException extends RuntimeException {
    public DuplicateBudgetException(String message){
        super(message);
    }
}
