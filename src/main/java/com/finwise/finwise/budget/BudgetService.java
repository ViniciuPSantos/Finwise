package com.finwise.finwise.budget;

import com.finwise.finwise.auth.User;
import com.finwise.finwise.auth.UserRepository;
import com.finwise.finwise.budget.dto.BudgetRequest;
import com.finwise.finwise.budget.dto.BudgetResponse;
import com.finwise.finwise.budget.dto.BudgetStatusResponse;
import com.finwise.finwise.category.Category;
import com.finwise.finwise.category.CategoryRepository;
import com.finwise.finwise.dashboard.dto.CategorySpentProjection;
import com.finwise.finwise.shared.exception.CategoryNotFoundException;
import com.finwise.finwise.shared.exception.DuplicateBudgetException;
import com.finwise.finwise.shared.exception.InvalidCredentialsException;
import com.finwise.finwise.transaction.TransactionRepository;
import com.finwise.finwise.shared.exception.BudgetNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public BudgetService(BudgetRepository budgetRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public BudgetResponse create(String email, BudgetRequest request) {
        User user = resolveUser(email);

        Category category = categoryRepository
                .findByIdAndUserAndDeletedAtIsNull(request.categoryId(), user)
                .orElseThrow(CategoryNotFoundException::new);

        budgetRepository
                .findByUserAndCategoryAndYearAndMonth(user, category, request.year(), request.month())
                .ifPresent(b -> {
                    throw new DuplicateBudgetException("A budget for this category and period already exists");
                });

        Budget budget = new Budget();
        budget.setAmount(request.amount());
        budget.setYear(request.year());
        budget.setMonth(request.month());
        budget.setUser(user);
        budget.setCategory(category);

        return BudgetResponse.from(budgetRepository.save(budget));
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> list(String email) {
        User user = resolveUser(email);
        return budgetRepository.findByUser(user).stream()
                .map(BudgetResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BudgetResponse getById(String email, Long id) {
        User user = resolveUser(email);
        Budget budget = getOwnedBudget(id, user);
        return BudgetResponse.from(budget);
    }

    @Transactional
    public BudgetResponse update(String email, Long id, BudgetRequest request) {
        User user = resolveUser(email);
        Budget budget = getOwnedBudget(id, user);

        budget.setAmount(request.amount());

        return BudgetResponse.from(budgetRepository.save(budget));
    }

    @Transactional
    public void delete(String email, Long id) {
        User user = resolveUser(email);
        Budget budget = getOwnedBudget(id, user);
        budget.setDeletedAt(java.time.Instant.now());
        budgetRepository.save(budget);
    }

    private User resolveUser(String email){
        return userRepository.findByEmail(email)
            .orElseThrow(InvalidCredentialsException::new);
    }

    private Budget getOwnedBudget(Long id, User user){
        return budgetRepository.findByIdAndUser(id, user)
            .orElseThrow(BudgetNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<BudgetStatusResponse> getBudgetStatus(String email, Integer year, Integer month){
        User user = resolveUser(email);

        if(year == null || month == null){
            LocalDate now = LocalDate.now();
            year = now.getYear();
            month = now.getMonthValue();
        }

        List<Budget> budgets = budgetRepository.findByUserAndYearAndMonth(user, year, month);
        
        Map<Long, BigDecimal> spentByCategory = transactionRepository
            .sumExpensesByCategoryIdForMonth(user, year, month).stream()
            .collect(Collectors.toMap(
                    CategorySpentProjection::getCategoryId,
                    CategorySpentProjection::getTotalSpent));

        return budgets.stream().map(budget ->{
            BigDecimal amount = budget.getAmount();
            BigDecimal spent = spentByCategory.getOrDefault(budget.getCategory().getId(), BigDecimal.ZERO);
            BigDecimal remaining = amount.subtract(spent);

            BigDecimal percentage = BigDecimal.ZERO;
            if(amount.compareTo(BigDecimal.ZERO) > 0){
                percentage = spent
                    .divide(amount, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            }
            return new BudgetStatusResponse(
                budget.getId(),
                budget.getCategory().getId(),
                budget.getCategory().getName(),
                amount, spent, remaining, percentage);
                
        }).toList();
    }
}
