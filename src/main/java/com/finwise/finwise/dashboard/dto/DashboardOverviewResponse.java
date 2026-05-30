package com.finwise.finwise.dashboard.dto;

import com.finwise.finwise.budget.dto.BudgetStatusResponse;

import java.util.List;

public record DashboardOverviewResponse(
    Integer year,
    Integer month,
    IncomeExpenseSummaryResponse summary,
    List<CategorySpendingResponse> spendingByCategory,
    List<BudgetStatusResponse> budgetStatus
) {}
