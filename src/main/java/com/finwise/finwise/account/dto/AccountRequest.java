package com.finwise.finwise.account.dto;

import com.finwise.finwise.account.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AccountRequest(
    @NotBlank(message = "Name is required")
    String name,

    @NotNull(message = "Type is required")
    AccountType type,

    @NotNull(message = "Initial balance is required")
    BigDecimal balance
) {}
