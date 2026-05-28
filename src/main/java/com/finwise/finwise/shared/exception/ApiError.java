package com.finwise.finwise.shared.exception;

import java.time.Instant;
import java.util.Map;

public record ApiError(
    Instant timestamp,
    int status,
    String error,
    Map<String, String> details
){
    public static ApiError of(int status, String error){
        return new ApiError(Instant.now(), status, error, null);
    }

    public static ApiError of(int status, String error, Map<String, String> details){
        return new ApiError(Instant.now(), status, error, details);
    }
}
