package com.finwise.finwise.dashboard.dto;

import java.math.BigDecimal;

public record ComparisonResponse(
        int currentYear,
        int currentMonth,
        IncomeExpenseSummaryResponse current,
        IncomeExpenseSummaryResponse previous,
        BigDecimal incomeChangePercent,
        BigDecimal expenseChangePercent
) {}
