package com.finwise.finwise.dashboard;

import com.finwise.finwise.auth.User;
import com.finwise.finwise.auth.UserRepository;
import com.finwise.finwise.budget.BudgetService;
import com.finwise.finwise.budget.dto.BudgetStatusResponse;
import com.finwise.finwise.dashboard.dto.CategorySpendingResponse;
import com.finwise.finwise.dashboard.dto.ComparisonResponse;
import com.finwise.finwise.dashboard.dto.DashboardOverviewResponse;
import com.finwise.finwise.dashboard.dto.IncomeExpenseProjection;
import com.finwise.finwise.dashboard.dto.IncomeExpenseSummaryResponse;
import com.finwise.finwise.dashboard.dto.MonthlyEvolutionProjection;
import com.finwise.finwise.dashboard.dto.MonthlyEvolutionResponse;
import com.finwise.finwise.shared.exception.InvalidCredentialsException;
import com.finwise.finwise.transaction.TransactionRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BudgetService budgetService;

    public DashboardService(TransactionRepository transactionRepository, UserRepository userRepository, BudgetService budgetService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.budgetService = budgetService;
    }

    public List<CategorySpendingResponse> getSpendingByCategory(String email, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        LocalDate[] period = resolvePeriod(startDate, endDate);
        startDate = period[0];
        endDate = period[1];

        return transactionRepository.sumExpensesByCategory(user, startDate, endDate);
    }

    public IncomeExpenseSummaryResponse getIncomeExpenseSummary(
            String email, LocalDate startDate, LocalDate endDate) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        LocalDate[] period = resolvePeriod(startDate, endDate);
        startDate = period[0];
        endDate = period[1];

        IncomeExpenseProjection p = transactionRepository.sumByType(user, startDate, endDate);

        BigDecimal income = p.getTotalIncome() != null ? p.getTotalIncome() : BigDecimal.ZERO;
        BigDecimal expense = p.getTotalExpense() != null ? p.getTotalExpense() : BigDecimal.ZERO;
        BigDecimal balance = income.subtract(expense);

        return new IncomeExpenseSummaryResponse(income, expense, balance);
    }

    public List<MonthlyEvolutionResponse> getMonthlyEvolution(
            String email, LocalDate startDate, LocalDate endDate) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        LocalDate[] period = resolvePeriod(startDate, endDate);

        List<MonthlyEvolutionProjection> rows = transactionRepository.sumByMonth(user, period[0], period[1]);

        return rows.stream()
                .map(r -> {
                    BigDecimal income = r.getTotalIncome() != null ? r.getTotalIncome() : BigDecimal.ZERO;
                    BigDecimal expense = r.getTotalExpense() != null ? r.getTotalExpense() : BigDecimal.ZERO;
                    String label = String.format("%04d-%02d", r.getYear(), r.getMonth());
                    return new MonthlyEvolutionResponse(label, income, expense, income.subtract(expense));
                })
                .toList();
    }

    private LocalDate[] resolvePeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        return new LocalDate[] { startDate, endDate };
    }

    public DashboardOverviewResponse getOverview(String email, Integer year, Integer month){
        if(year == null || month == null){
            LocalDate now = LocalDate.now();
            year = now.getYear();
            month = now.getMonthValue();
        }

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        IncomeExpenseSummaryResponse summary = 
            getIncomeExpenseSummary(email, startDate, endDate);

        List<CategorySpendingResponse> spending =
            getSpendingByCategory(email, startDate, endDate);

        List<BudgetStatusResponse> budgetStatus = 
            budgetService.getBudgetStatus(email, year, month);

        return new DashboardOverviewResponse(year, month, summary, spending, budgetStatus);
    }

    public ComparisonResponse getComparison(String email, Integer year, Integer month) {
        if (year == null || month == null) {
            LocalDate now = LocalDate.now();
            year = now.getYear();
            month = now.getMonthValue();
        }

        LocalDate currentStart = LocalDate.of(year, month, 1);
        LocalDate currentEnd = currentStart.withDayOfMonth(currentStart.lengthOfMonth());

        LocalDate previousStart = currentStart.minusMonths(1);
        LocalDate previousEnd = previousStart.withDayOfMonth(previousStart.lengthOfMonth());

        IncomeExpenseSummaryResponse current = getIncomeExpenseSummary(email, currentStart, currentEnd);
        IncomeExpenseSummaryResponse previous = getIncomeExpenseSummary(email, previousStart, previousEnd);

        BigDecimal incomeChange = percentChange(previous.totalIncome(), current.totalIncome());
        BigDecimal expenseChange = percentChange(previous.totalExpense(), current.totalExpense());

        return new ComparisonResponse(year, month, current, previous, incomeChange, expenseChange);
    }

    private BigDecimal percentChange(BigDecimal before, BigDecimal after) {
        if (before == null || before.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return after.subtract(before)
                .divide(before, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
