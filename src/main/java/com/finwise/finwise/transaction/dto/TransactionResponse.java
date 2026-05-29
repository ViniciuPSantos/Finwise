package com.finwise.finwise.transaction.dto;

import com.finwise.finwise.transaction.Transaction;
import com.finwise.finwise.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record TransactionResponse(
    Long id,
    BigDecimal amount,
    TransactionType type,
    String description,
    LocalDate date,
    Long accountId,
    String accountName,
    Long categoryId,
    String categoryName,
    Instant createdAt
) {
    public static TransactionResponse from(Transaction t) {
        return new TransactionResponse(
            t.getId(),
            t.getAmount(),
            t.getType(),
            t.getDescription(),
            t.getDate(),
            t.getAccount().getId(),
            t.getAccount().getName(),
            t.getCategory().getId(),
            t.getCategory().getName(),
            t.getCreatedAt()
        );
    }
}
