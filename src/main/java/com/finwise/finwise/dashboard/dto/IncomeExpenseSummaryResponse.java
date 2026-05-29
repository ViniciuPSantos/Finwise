package com.finwise.finwise.dashboard.dto;

import java.math.BigDecimal;

public record IncomeExpenseSummaryResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance) {
}