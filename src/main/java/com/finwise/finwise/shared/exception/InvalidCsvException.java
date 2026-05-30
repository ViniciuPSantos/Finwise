package com.finwise.finwise.shared.exception;

public class InvalidCsvException extends RuntimeException {
    public InvalidCsvException(String message){
        super(message);
    }
}
