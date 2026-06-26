package com.finwise.finwise.recurring.dto;

import com.finwise.finwise.recurring.RecurringFrequency;
import com.finwise.finwise.recurring.RecurringTransaction;
import com.finwise.finwise.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record RecurringTransactionResponse(
        Long id,
        BigDecimal amount,
        TransactionType type,
        String description,
        RecurringFrequency frequency,
        LocalDate nextExecutionDate,
        boolean active,
        Long accountId,
        String accountName,
        Long categoryId,
        String categoryName,
        Instant createdAt
) {
    public static RecurringTransactionResponse from(RecurringTransaction r) {
        return new RecurringTransactionResponse(
                r.getId(),
                r.getAmount(),
                r.getType(),
                r.getDescription(),
                r.getFrequency(),
                r.getNextExecutionDate(),
                r.isActive(),
                r.getAccount().getId(),
                r.getAccount().getName(),
                r.getCategory().getId(),
                r.getCategory().getName(),
                r.getCreatedAt());
    }
}
