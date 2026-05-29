package com.finwise.finwise.account.dto;

import com.finwise.finwise.account.Account;
import com.finwise.finwise.account.AccountType;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
    Long id,
    String name,
    AccountType type,
    BigDecimal balance,
    Instant createdAt
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
            account.getId(),
            account.getName(),
            account.getType(),
            account.getBalance(),
            account.getCreatedAt()
        );
    }
}
