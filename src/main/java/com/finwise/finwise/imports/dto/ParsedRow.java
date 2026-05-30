package com.finwise.finwise.imports.dto;

public record ParsedRow(
    int line,
    String date,
    String amount,
    String type,
    String description,
    String account,
    String category
){}
