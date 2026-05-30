package com.finwise.finwise.imports.dto;

public record ImportErrorDetails(
    int line,
    String reason
){}