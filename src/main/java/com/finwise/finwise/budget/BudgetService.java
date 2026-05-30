package com.finwise.finwise.budget;

import com.finwise.finwise.auth.User;
import com.finwise.finwise.auth.UserRepository;
import com.finwise.finwise.budget.dto.BudgetRequest;
import com.finwise.finwise.budget.dto.BudgetResponse;
import com.finwise.finwise.category.Category;
import com.finwise.finwise.category.CategoryRepository;
import com.finwise.finwise.shared.exception.CategoryNotFoundException;
import com.finwise.finwise.shared.exception.DuplicateBudgetException;
import com.finwise.finwise.shared.exception.InvalidCredentialsException;
import com.finwise.finwise.shared.exception.BudgetNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public BudgetService(BudgetRepository budgetRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public BudgetResponse create(String email, BudgetRequest request) {
        User user = resolveUser(email);

        Category category = categoryRepository
                .findByIdAndUser(request.categoryId(), user)
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

    public List<BudgetResponse> list(String email) {
        User user = resolveUser(email);
        return budgetRepository.findByUser(user).stream()
                .map(BudgetResponse::from)
                .toList();
    }

    public BudgetResponse getById(String email, Long id) {
        User user = resolveUser(email);
        Budget budget = getOwnedBudget(id, user);
        return BudgetResponse.from(budget);
    }

    public BudgetResponse update(String email, Long id, BudgetRequest request) {
        User user = resolveUser(email);
        Budget budget = getOwnedBudget(id, user);

        budget.setAmount(request.amount());

        return BudgetResponse.from(budgetRepository.save(budget));
    }

    public void delete(String email, Long id) {
        User user = resolveUser(email);
        Budget budget = getOwnedBudget(id, user);
        budgetRepository.delete(budget);
    }

    private User resolveUser(String email){
        return userRepository.findByEmail(email)
            .orElseThrow(InvalidCredentialsException::new);
    }

    private Budget getOwnedBudget(Long id, User user){
        return budgetRepository.findByIdAndUser(id, user)
            .orElseThrow(BudgetNotFoundException::new);
    }
}
