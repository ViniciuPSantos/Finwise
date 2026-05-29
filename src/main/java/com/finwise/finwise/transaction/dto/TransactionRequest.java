package com.finwise.finwise.transaction.dto;

import com.finwise.finwise.transaction.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    BigDecimal amount,

    @NotNull(message = "Type is required")
    TransactionType type,

    @NotNull(message = "Description is required")
    String description,

    @NotNull(message = "Date is required")
    LocalDate date,

    @NotNull(message = "Account is required")
    Long accountId,

    @NotNull(message = "Category is required")
    Long categoryId
) {
    
}
