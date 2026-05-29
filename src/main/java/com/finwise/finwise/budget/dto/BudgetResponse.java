package com.finwise.finwise.budget.dto;

import com.finwise.finwise.budget.Budget;

import java.math.BigDecimal;

public record BudgetResponse(
        Long id,
        BigDecimal amount,
        Integer year,
        Integer month,
        Long categoryId,
        String categoryName) {
    public static BudgetResponse from(Budget budget) {
        return new BudgetResponse(
                budget.getId(),
                budget.getAmount(),
                budget.getYear(),
                budget.getMonth(),
                budget.getCategory().getId(),
                budget.getCategory().getName());
    }
}
