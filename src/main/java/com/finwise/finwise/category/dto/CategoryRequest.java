package com.finwise.finwise.category.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
    @NotBlank(message = "Name is required")
    String name
) {}
