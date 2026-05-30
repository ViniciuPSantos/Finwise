package com.finwise.finwise.budget.dto;

import java.math.BigDecimal;

public record BudgetStatusResponse(
    Long budgetId,
    Long categoryId,
    String categoryName,
    BigDecimal amount,
    BigDecimal spent,
    BigDecimal remaining,
    BigDecimal percentage
) {}
