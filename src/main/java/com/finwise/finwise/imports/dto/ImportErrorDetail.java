package com.finwise.finwise.imports.dto;

public record ImportErrorDetail(
        int line,
        String reason
) {}
