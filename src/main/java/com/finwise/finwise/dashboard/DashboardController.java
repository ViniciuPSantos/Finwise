package com.finwise.finwise.dashboard;

import com.finwise.finwise.dashboard.dto.CategorySpendingResponse;
import com.finwise.finwise.dashboard.dto.DashboardOverviewResponse;
import com.finwise.finwise.dashboard.dto.IncomeExpenseSummaryResponse;
import com.finwise.finwise.dashboard.dto.MonthlyEvolutionResponse;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/spending-by-category")
    public List<CategorySpendingResponse> spendingByCategory(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return dashboardService.getSpendingByCategory(email, startDate, endDate);
    }

    @GetMapping("/income-expense-summary")
    public IncomeExpenseSummaryResponse incomeExpenseSummary(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return dashboardService.getIncomeExpenseSummary(email, startDate, endDate);
    }

    @GetMapping("/monthly-evolution")
    public List<MonthlyEvolutionResponse> monthlyEvolution(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return dashboardService.getMonthlyEvolution(email, startDate, endDate);
    }

    @GetMapping("/overview")
    public DashboardOverviewResponse overview(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return dashboardService.getOverview(email, year, month);
    }
}