package com.finwise.finwise.budget.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record BudgetRequest(
        @NotNull(message = "Amount is required") @Positive(message = "Amount must be positive") BigDecimal amount,

        @NotNull(message = "Category id is required") Long categoryId,

        @NotNull(message = "Year is required") @Min(value = 2000, message = "Year must be 2000 or later") Integer year,

        @NotNull(message = "Month is required") @Min(value = 1, message = "Month must be between 1 and 12") @Max(value = 12, message = "Month must be between 1 and 12") Integer month) {

}
