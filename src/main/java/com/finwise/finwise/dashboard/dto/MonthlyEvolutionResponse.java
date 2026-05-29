package com.finwise.finwise.dashboard.dto;

import java.math.BigDecimal;

public record MonthlyEvolutionResponse(
        String month,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance) {

}
