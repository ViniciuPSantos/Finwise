package com.finwise.finwise.transfer.dto;

import com.finwise.finwise.transfer.Transfer;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record TransferResponse(
        Long id,
        BigDecimal amount,
        String description,
        LocalDate date,
        Long fromAccountId,
        String fromAccountName,
        Long toAccountId,
        String toAccountName,
        Instant createdAt
) {
    public static TransferResponse from(Transfer t) {
        return new TransferResponse(
                t.getId(),
                t.getAmount(),
                t.getDescription(),
                t.getDate(),
                t.getFromAccount().getId(),
                t.getFromAccount().getName(),
                t.getToAccount().getId(),
                t.getToAccount().getName(),
                t.getCreatedAt());
    }
}
