package com.finwise.finwise.dashboard;

import com.finwise.finwise.auth.User;
import com.finwise.finwise.auth.UserRepository;
import com.finwise.finwise.dashboard.dto.CategorySpendingResponse;
import com.finwise.finwise.dashboard.dto.IncomeExpenseProjection;
import com.finwise.finwise.dashboard.dto.IncomeExpenseSummaryResponse;
import com.finwise.finwise.dashboard.dto.MonthlyEvolutionProjection;
import com.finwise.finwise.dashboard.dto.MonthlyEvolutionResponse;
import com.finwise.finwise.shared.exception.InvalidCredentialsException;
import com.finwise.finwise.transaction.TransactionRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public DashboardService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
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

    public LocalDate[] resolvePeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        return new LocalDate[] { startDate, endDate };
    }
}
