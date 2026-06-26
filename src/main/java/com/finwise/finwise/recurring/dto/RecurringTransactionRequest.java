package com.finwise.finwise.recurring.dto;

import com.finwise.finwise.recurring.RecurringFrequency;
import com.finwise.finwise.transaction.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecurringTransactionRequest(
        @NotNull @Positive BigDecimal amount,
        @NotNull TransactionType type,
        @NotBlank String description,
        @NotNull RecurringFrequency frequency,
        @NotNull LocalDate startDate,
        @NotNull Long accountId,
        @NotNull Long categoryId
) {}
