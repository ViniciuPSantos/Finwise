package com.finwise.finwise.category.dto;

import com.finwise.finwise.category.Category;

import java.time.Instant;

public record CategoryResponse(
    Long id,
    String name,
    Instant createdAt
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getCreatedAt()
        );
    }
}
